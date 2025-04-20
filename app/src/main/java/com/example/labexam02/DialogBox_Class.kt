package com.example.labexam02

import android.content.DialogInterface
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class DialogBox_Class:DialogFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val etTitle=view.findViewById<TextView>(R.id.editTitle);
        val etAmount=view.findViewById<TextView>(R.id.editExpenseAmount);
        val etBtnSubmit=view.findViewById<Button>(R.id.buttonSubmit);

        etBtnSubmit.setOnClickListener {
            val name = etTitle.text.toString()
            val amount = etAmount.text.toString().toIntOrNull() ?: 0

            dialog?.dismiss();
            Toast.makeText(requireContext(), "Name: $name, Amount: $amount", Toast.LENGTH_SHORT).show()

        }

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
}