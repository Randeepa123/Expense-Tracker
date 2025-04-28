package com.example.labexam02

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

class ProfileFragment : Fragment() {
    // SharedPreferences constants
    companion object {
        const val PROFILE_PREFS = "profile_preferences"
        const val KEY_NAME = "user_name"
        const val KEY_EMAIL = "user_email"
        const val KEY_CURRENCY = "user_currency"

        // Currency symbols map
        val CURRENCY_SYMBOLS = mapOf(
            "USD" to "$",
            "EUR" to "€",
            "GBP" to "£",
            "INR" to "₹",
            "JPY" to "¥",
            "CAD" to "C$",
            "AUD" to "A$",
            "SLR" to "Rs"
        )

        // Currency conversion rates (relative to USD)
        val CURRENCY_RATES = mapOf(
            "USD" to 1.0,
            "EUR" to 0.92,
            "GBP" to 0.79,
            "INR" to 83.45,
            "JPY" to 156.67,
            "CAD" to 1.37,
            "AUD" to 1.52,
            "SLR" to 0.0033,
        )
    }

    private lateinit var profilePrefs: SharedPreferences

    // UI Elements
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var currencySpinner: Spinner
    private lateinit var saveButton: Button
    private lateinit var selectedCurrencyText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize SharedPreferences
        profilePrefs = requireContext().getSharedPreferences(PROFILE_PREFS, Context.MODE_PRIVATE)

        // Initialize UI elements
        nameEditText = view.findViewById(R.id.etProfileName)
        emailEditText = view.findViewById(R.id.etProfileEmail)
        currencySpinner = view.findViewById(R.id.spinnerCurrency)
        saveButton = view.findViewById(R.id.btnSaveProfile)
        selectedCurrencyText = view.findViewById(R.id.tvSelectedCurrency)

        // Setup currency spinner
        setupCurrencySpinner()

        // Load saved profile data
        loadProfileData()

        // Setup save button
        saveButton.setOnClickListener {
            saveProfileData()
        }

        return view
    }

    private fun setupCurrencySpinner() {
        // Create spinner adapter with currency options
        val currencyOptions = CURRENCY_SYMBOLS.keys.toList()
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            currencyOptions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        currencySpinner.adapter = adapter

        // Set spinner selection listener
        currencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCurrency = currencyOptions[position]
                val currencySymbol = CURRENCY_SYMBOLS[selectedCurrency] ?: "$"
                selectedCurrencyText.text = "Selected: $selectedCurrency ($currencySymbol)"
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun loadProfileData() {
        // Load saved name and email
        val savedName = profilePrefs.getString(KEY_NAME, "")
        val savedEmail = profilePrefs.getString(KEY_EMAIL, "")
        val savedCurrency = profilePrefs.getString(KEY_CURRENCY, "USD")

        // Set values to UI elements
        nameEditText.setText(savedName)
        emailEditText.setText(savedEmail)

        // Set spinner selection
        val currencyOptions = CURRENCY_SYMBOLS.keys.toList()
        val currencyIndex = currencyOptions.indexOf(savedCurrency)
        if (currencyIndex >= 0) {
            currencySpinner.setSelection(currencyIndex)
        }
    }

    private fun saveProfileData() {
        // Get values from UI elements
        val name = nameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val selectedCurrency = currencySpinner.selectedItem.toString()

        // Save to SharedPreferences
        profilePrefs.edit().apply {
            putString(KEY_NAME, name)
            putString(KEY_EMAIL, email)
            putString(KEY_CURRENCY, selectedCurrency)
            apply()
        }

        // Show confirmation message
        Toast.makeText(requireContext(), "Profile saved successfully", Toast.LENGTH_SHORT).show()
    }
}