package com.example.labexam02

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.labexam02.databinding.FragmentAnalyticBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AnalyticFragment : Fragment() {
    private var _binding: FragmentAnalyticBinding? = null
    private val binding get() = _binding!!
    private val util = utill()

    // Maps to store category totals and transaction counts
    private val categoryTotals = mutableMapOf<String, Double>()
    private val categoryTransactions = mutableMapOf<String, MutableList<Transactions>>()

    // SharedPreferences for storing budget values
    private lateinit var budgetPrefs: SharedPreferences
    private val BUDGET_PREFS = "budget_preferences"
    private val BUDGET_PREFIX = "budget_"

    // Notification
    private val CHANNEL_ID = "budget_notification_channel"
    private val NOTIFICATION_ID = 101
    private val REQUEST_NOTIFICATION_PERMISSION = 100

    // Category colors for chart
    private val categoryColors = mapOf(
        "Food" to Color.rgb(255, 102, 102),          // Pink
        "Transportation" to Color.rgb(102, 178, 255), // Blue
        "Gifts" to Color.rgb(255, 178, 102),         // Yellow
        "Shopping" to Color.rgb(102, 255, 102),      // Green
        "Entertainment" to Color.rgb(178, 102, 255), // Purple
        "Bills" to Color.rgb(102, 255, 255),         // Cyan
        "Other" to Color.rgb(255, 153, 51)           // Orange
    )

    // Default color for categories not in the map
    private val defaultColor = Color.rgb(128, 128, 128) // Gray

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyticBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize SharedPreferences for budgets
        budgetPrefs = requireContext().getSharedPreferences(BUDGET_PREFS, Context.MODE_PRIVATE)

        // Create notification channel
        createNotificationChannel()

        // Request notification permission
        requestNotificationPermission()

        // Initialize UI
        setupWeekSelector()

        // Load and process transaction data
        loadTransactions()

        // Update UI with processed data
        setupBarChart()
        populateCategoryList()

        // Check if any budgets are exceeded
        checkBudgetExceeded()
    }

    private fun setupWeekSelector() {
        // Set current week text
        binding.tvWeekDropdown.text = getCurrentWeekText()

        // Setup click listener for week selection
        binding.tvWeekDropdown.setOnClickListener {
            // You can implement week selection dialog here
        }
    }

    private fun getCurrentWeekText(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())

        // Set to first day of current week (Sunday)
        calendar.firstDayOfWeek = Calendar.SUNDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        val startDate = dateFormat.format(calendar.time)

        // Move to last day of week (Saturday)
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val endDate = dateFormat.format(calendar.time)

        return "$startDate - $endDate"
    }

    private fun loadTransactions() {
        context?.let { ctx ->
            // Load all transactions from JSON file
            val allTransactions = util.loadDataFromFile<Transactions>(ctx, util.expenseFileName)

            // Clear previous data
            categoryTotals.clear()
            categoryTransactions.clear()

            // Filter and process transactions
            val expenseTransactions = allTransactions.filter { it.isExpense }

            expenseTransactions.forEach { transaction ->
                // Add to category totals
                val currentTotal = categoryTotals[transaction.category] ?: 0.0
                categoryTotals[transaction.category] = currentTotal + transaction.amount

                // Add to category transactions list
                if (!categoryTransactions.containsKey(transaction.category)) {
                    categoryTransactions[transaction.category] = mutableListOf()
                }
                categoryTransactions[transaction.category]?.add(transaction)
            }
        }
    }

    private fun setupBarChart() {
        // Get reference to chart from binding
        val barChart = binding.barChart

        // Prepare bar entries and labels
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()
        val colors = ArrayList<Int>()

        // Sort categories by total amount
        val sortedCategories = categoryTotals.entries.sortedByDescending { it.value }

        // Create bar entries from category data (up to 7 categories)
        sortedCategories.take(7).forEachIndexed { index, (category, amount) ->
            entries.add(BarEntry(index.toFloat(), amount.toFloat()))
            labels.add(category)
            colors.add(categoryColors[category] ?: defaultColor)
        }

        // Create the dataset
        val dataSet = BarDataSet(entries, "Category Expenses")
        dataSet.colors = colors
        dataSet.valueTextSize = 12f

        // Create and style the bar data
        val barData = BarData(dataSet)
        barData.barWidth = 0.7f

        // Set up the chart
        barChart.data = barData
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.setDrawGridBackground(false)
        barChart.setDrawBarShadow(false)
        barChart.setDrawValueAboveBar(true)

        // Customize X axis
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)

        // Customize Y axis
        val leftAxis = barChart.axisLeft
        leftAxis.setDrawGridLines(true)
        leftAxis.axisMinimum = 0f

        // Disable right axis
        barChart.axisRight.isEnabled = false

        // Animate chart
        barChart.animateY(1000)

        // Refresh chart
        barChart.invalidate()
    }

    private fun populateCategoryList() {
        // Get reference to container and clear it
        val container = binding.categoryListContainer
        container.removeAllViews()

        // Sort categories by total amount
        val sortedCategories = categoryTotals.entries.sortedByDescending { it.value }

        // Add category cards to container
        sortedCategories.forEach { (category, amount) ->
            val card = createCategoryCard(category, amount)
            container.addView(card)
        }
    }

    private fun createCategoryCard(category: String, amount: Double): CardView {
        // Inflate layout for category card
        val card = layoutInflater.inflate(
            R.layout.expense_category_item,
            binding.categoryListContainer,
            false
        ) as CardView

        // Set category name
        card.findViewById<TextView>(R.id.tvCategoryName).text = category

        // Set transaction count
        val transactionCount = categoryTransactions[category]?.size ?: 0
        card.findViewById<TextView>(R.id.tvTransactionCount).text =
            "$transactionCount transaction${if (transactionCount > 1) "s" else ""}"

        // Set amount
        card.findViewById<TextView>(R.id.tvCategoryAmount).text =
            String.format("%.2f", amount)

        // Get budget from SharedPreferences
        val budget = getBudgetForCategory(category)

        // Set budget text if available
        if (budget > 0) {
            val budgetTextView = card.findViewById<TextView>(R.id.tvCategoryBudget)
            budgetTextView?.text = "Budget: ${String.format("%.2f", budget)}"
            budgetTextView?.visibility = View.VISIBLE

            // Change color if budget exceeded
            if (amount > budget) {
                card.findViewById<TextView>(R.id.tvCategoryAmount).setTextColor(Color.RED)
            }
        }

        // Set click listener to open budget dialog
        card.setOnClickListener {
            showBudgetDialog(category, amount)
        }

        return card
    }

    private fun showBudgetDialog(category: String, currentAmount: Double) {
        val dialogView = layoutInflater.inflate(R.layout.budget_dialog, null)
        val currentBudget = getBudgetForCategory(category)

        // Set dialog title
        dialogView.findViewById<TextView>(R.id.tvDialogTitle).text = "Set Budget for $category"

        // Set current budget if exists
        val budgetInput = dialogView.findViewById<TextInputEditText>(R.id.etBudgetAmount)
        if (currentBudget > 0) {
            budgetInput.setText(currentBudget.toString())
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        // Set button click listeners
        dialogView.findViewById<View>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.btnSave).setOnClickListener {
            val budgetText = budgetInput.text.toString()
            if (budgetText.isNotEmpty()) {
                try {
                    val budgetAmount = budgetText.toDouble()
                    saveBudgetForCategory(category, budgetAmount)

                    // Update UI
                    populateCategoryList()

                    // Check if budget is exceeded
                    if (currentAmount > budgetAmount) {
                        showBudgetExceededNotification(category, budgetAmount, currentAmount)
                    }

                    Toast.makeText(requireContext(), "Budget set for $category", Toast.LENGTH_SHORT).show()
                } catch (e: NumberFormatException) {
                    Toast.makeText(requireContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun getBudgetForCategory(category: String): Double {
        return budgetPrefs.getFloat(BUDGET_PREFIX + category, 0f).toDouble()
    }

    private fun saveBudgetForCategory(category: String, budget: Double) {
        budgetPrefs.edit().putFloat(BUDGET_PREFIX + category, budget.toFloat()).apply()
    }

    private fun createNotificationChannel() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "Budget Notifications"
                val descriptionText = "Notifications for exceeded budgets"
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                    enableLights(true)
                    lightColor = Color.RED
                    enableVibration(true)
                    setShowBadge(true)
                }

                val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)

                Log.d("BudgetNotification", "Notification channel created")
            }
        } catch (e: Exception) {
            Log.e("BudgetNotification", "Error creating notification channel", e)
        }
    }

    private fun showBudgetExceededNotification(category: String, budget: Double, spent: Double) {
        try {
            // Create an intent that opens your app
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pendingIntent = PendingIntent.getActivity(
                requireContext(),
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            // Build notification
            val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Budget Alert: $category")
                .setContentText("Budget exceeded by ${String.format("%.2f", spent - budget)}")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setVibrate(longArrayOf(0, 250, 250, 250))

            // Add notification sound
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

            // Send notification
            val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationId = category.hashCode()
            notificationManager.notify(notificationId, builder.build())

            Log.d("BudgetNotification", "Notification sent for $category")
        } catch (e: Exception) {
            Log.e("BudgetNotification", "Error sending notification", e)
        }
    }

    private fun checkBudgetExceeded() {
        categoryTotals.forEach { (category, amount) ->
            val budget = getBudgetForCategory(category)
            if (budget > 0 && amount > budget) {
                showBudgetExceededNotification(category, budget, amount)
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_NOTIFICATION_PERMISSION
                )
            } else {
                // Permission already granted
                // Uncomment this line to test notifications immediately
                // testNotification()
            }
        } else {
            // For lower Android versions, no runtime permission needed
            // Uncomment this line to test notifications immediately
            // testNotification()
        }
    }

    // Test notification function - use only for debugging
    private fun testNotification() {
        Handler(Looper.getMainLooper()).postDelayed({
            showBudgetExceededNotification("Test Category", 100.0, 150.0)
            Toast.makeText(requireContext(), "Test notification sent", Toast.LENGTH_SHORT).show()
        }, 3000) // 3 seconds delay
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toast.makeText(requireContext(), "Notification permission granted", Toast.LENGTH_SHORT).show()
                // Uncomment to test notifications immediately after permission granted
                // testNotification()
            } else {
                Toast.makeText(requireContext(),
                    "Notification permission denied. Enable in settings to receive budget alerts.",
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}