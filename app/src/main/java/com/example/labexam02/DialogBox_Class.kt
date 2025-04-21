package com.example.labexam02

import android.content.DialogInterface
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

class DialogBox_Class:DialogFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val etTitle=view.findViewById<TextView>(R.id.editTitle);
        val etAmount=view.findViewById<TextView>(R.id.editExpenseAmount);
        val etBtnSubmit=view.findViewById<Button>(R.id.buttonSubmit);
        val selectedCategory=view.findViewById<Spinner>(R.id.btn_cash);




        selectedCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent?.getItemAtPosition(position).toString()

                when (selectedItem) {
                    "Expense" -> replaceAdditemFragment(enterExpensefragment(),false)
                    "Income" -> replaceAdditemFragment(enterIncomefragment(),true)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Optional: handle case when nothing is selected
            }
        }

/*
        etBtnSubmit.setOnClickListener {
            val name = etTitle.text.toString()
            val amount = etAmount.text.toString().toIntOrNull() ?: 0


            dialog?.dismiss();
            Toast.makeText(requireContext(), "Name: $name, Amount: $amount", Toast.LENGTH_SHORT).show()

        }
*/
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_expense, container, false)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 1350)
            setBackgroundDrawableResource(android.R.color.transparent)
            setGravity(Gravity.BOTTOM)
            attributes.windowAnimations =R.style.DialogAnimation;
        }

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        (activity as? MainActivity)?.restoreBottomNavigationSelection();
    }


    private fun replaceAdditemFragment(fragment: Fragment,isIncome:Boolean): Boolean{
        val shoppingIconSpinner=view?.findViewById<Spinner>(R.id.btn_shopping);
        childFragmentManager.beginTransaction()
        .replace(R.id.AmountDisplayFragment,fragment)
        .commit()

        if(isIncome==true){
            shoppingIconSpinner?.visibility = View.INVISIBLE
        }else{
            shoppingIconSpinner?.visibility = View.VISIBLE
        }
        return true}
}

