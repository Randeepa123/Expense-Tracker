package com.example.labexam02

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import java.util.Calendar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [enterIncomefragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class enterIncomefragment() : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        return inflater.inflate(R.layout.fragment_enter_incomefragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val newUtill= utill();

        //getting user inputs
        val incomeTitle=view?.findViewById<TextView>(R.id.editIncomeTitle);
        val incomeAmount=view?.findViewById<TextView>(R.id.editIncomeAmount);
        val incomeSubmitButton=view?.findViewById<Button>(R.id.buttonIncomeSubmit);

        val textViewDate = view.findViewById<TextView>(R.id.textViewIncomeDate)
        val imageViewCalendar = view.findViewById<ImageView>(R.id.imageViewIncomeCalendar)
        val category = view.findViewById<Spinner>(R.id.incomeCategorySpinner)

        imageViewCalendar.setOnClickListener {
            showDatePicker(textViewDate)
        }

        incomeSubmitButton?.setOnClickListener {
            val titleText = incomeTitle?.text.toString()
            val amountText = incomeAmount?.text.toString()
            val dateText = textViewDate.text.toString()
            val selectedCategory = category.selectedItem.toString()

            if (titleText.isNotEmpty() && amountText.isNotEmpty()&& dateText.isNotEmpty()) {
                val amountDouble = amountText.toDoubleOrNull()

                if (amountDouble != null) {
                    newUtill.addAnItem(requireContext(), false, titleText, amountDouble,dateText,selectedCategory)
                    Log.d("DialogFragment", "Button clicked")

                    Toast.makeText(requireContext(), "Income saved!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Invalid amount", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePicker(textView: TextView) {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = "${dayOfMonth}/${month + 1}/$year"
                textView.text = selectedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment enterIncomefragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            enterIncomefragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}