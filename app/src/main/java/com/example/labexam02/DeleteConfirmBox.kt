package com.example.labexam02

import HomeFragment
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DeleteConfirmBox.newInstance] factory method to
 * create an instance of this fragment.
 */
class DeleteConfirmBox(val item: Transactions) : DialogFragment() {
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
        return inflater.inflate(R.layout.fragment_delete_confirm_box, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val confirmBtn=view.findViewById<Button>(R.id.btn_delete)
        val cancellBtn=view.findViewById<Button>(R.id.btn_cancel)

        confirmBtn.setOnClickListener {
            val utill= utill()
            utill.deleteItem(requireContext(),item.id, isExpense = item.isExpense)
            parentFragment?.let { fragment ->
                if (fragment is HomeFragment) {
                    fragment.refreshData() // This will reload the transactions and update the RecyclerView
                }
            }
            dismiss();
        }
            cancellBtn.setOnClickListener {
                dismiss();
            }
        }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DeleteConfirmBox.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(item: Transactions) =
            DeleteConfirmBox(item = item)
    }
}