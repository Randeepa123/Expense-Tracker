import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.labexam02.FinanceAdapter
import com.example.labexam02.ProfileFragment
import com.example.labexam02.R
import com.example.labexam02.Transactions
import com.example.labexam02.utill

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var itemList: MutableList<Transactions>
    private val util = utill()
    private lateinit var adapter: FinanceAdapter

    // UI elements
    private lateinit var currencySymbolTextView: TextView
    private lateinit var amountValueTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize UI elements
        currencySymbolTextView = view.findViewById(R.id.currency_symbol)
        amountValueTextView = view.findViewById(R.id.amount_value)

        // Setup RecyclerView
        try {


        itemList = util.loadDataFromFile<Transactions>(requireContext(), "Transactions.json").reversed().toMutableList()
        val recyclerView = view.findViewById<RecyclerView>(R.id.homerecyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = FinanceAdapter(itemList, requireContext(), childFragmentManager)
        recyclerView.adapter = adapter}catch (e: Exception){
            e.printStackTrace();
        }

        // Update balance display
        updateBalanceDisplay()
    }

    override fun onResume() {
        super.onResume()
        loadTransactions()
        updateBalanceDisplay()
    }

    private fun loadTransactions() {
        val updatedList = util.loadDataFromFile<Transactions>(requireContext(), "Transactions.json").reversed()
        itemList.clear()
        itemList.addAll(updatedList)
        adapter.updateData(updatedList)
    }

    private fun updateBalanceDisplay() {
        val allTransactions = util.loadDataFromFile<Transactions>(requireContext(), "Transactions.json")

        // Calculate total income and expenses
        var totalIncome = 0.0
        var totalExpense = 0.0

        allTransactions.forEach { transaction ->
            if (transaction.isExpense) {
                totalExpense += transaction.amount
            } else {
                totalIncome += transaction.amount
            }
        }

        // Calculate balance
        val balance = totalIncome - totalExpense

        // Get preferred currency from SharedPreferences
        val profilePrefs = requireContext().getSharedPreferences(
            ProfileFragment.PROFILE_PREFS, Context.MODE_PRIVATE
        )
        val preferredCurrency = profilePrefs.getString(
            ProfileFragment.KEY_CURRENCY, "USD"
        ) ?: "USD"

        // Get currency symbol
        val currencySymbol = ProfileFragment.CURRENCY_SYMBOLS[preferredCurrency] ?: "$"

        // Convert balance to preferred currency
        val usdToPreferredRate = ProfileFragment.CURRENCY_RATES[preferredCurrency] ?: 1.0
        val convertedBalance = balance * usdToPreferredRate

        // Update UI
        currencySymbolTextView.text = currencySymbol
        amountValueTextView.text = String.format("%.2f", convertedBalance)
    }

    fun refreshData() {
        loadTransactions()
        updateBalanceDisplay()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
