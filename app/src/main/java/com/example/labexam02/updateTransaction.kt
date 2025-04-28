package com.example.labexam02

import HomeFragment
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import java.util.Calendar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [updateTransaction.newInstance] factory method to
 * create an instance of this fragment.
 */
class updateTransaction(val item: Transactions) : DialogFragment() {
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
        return inflater.inflate(R.layout.fragment_update_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


             val updateTitle=view.findViewById<TextView>(R.id.UpdateIncomeTitle);
             val updateAmount=view.findViewById<TextView>(R.id.UpdateIncomeAmount);
             val textViewDate = view.findViewById<TextView>(R.id.textViewDateUpdate)
             val imageViewCalendar = view.findViewById<ImageView>(R.id.imageViewCalendarUpdate)
             val Updatebtn=view.findViewById<Button>(R.id.buttonUpdateTransaction);
             val Btncancell=view.findViewById<Button>(R.id.buttoncancellTransaction);

        imageViewCalendar.setOnClickListener {
            showDatePicker(textViewDate)
        }


            updateTitle.text = item.title
            updateAmount.text = item.amount.toString()
            textViewDate.text=item.date.toString()
            val category=item.category.toString()
            Updatebtn.setOnClickListener {
                val newTitle = updateTitle.text.toString()
                val newAmount = updateAmount.text.toString().toDoubleOrNull() ?: 0.0
                val date=textViewDate.text.toString()
                val category=category

                val utill = utill()
                utill.updateItem(context=requireContext(),isExpense =item.isExpense,Amount=newAmount,Title=newTitle,id=item.id,date=date, category =category )
                parentFragment?.let { fragment ->
                    if (fragment is HomeFragment) {
                        fragment.refreshData() // This will reload the transactions and update the RecyclerView
                    }
                }
                dismiss();
            }

            Btncancell.setOnClickListener {
                dismiss();
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
         * @return A new instance of fragment updateTransaction.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(items: Transactions) =
            updateTransaction(item = items)
            }
    }
