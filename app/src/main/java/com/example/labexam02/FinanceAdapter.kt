package com.example.labexam02

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView

class FinanceAdapter(private val items: MutableList<Transactions>,private val context: Context,private val fragmentManager: FragmentManager) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val TYPE_EXPENSE = 1
    private val TYPE_INCOME = 0



    override fun getItemViewType(position: Int): Int {
        return if (items[position].isExpense) TYPE_EXPENSE else TYPE_INCOME
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_EXPENSE) {
            val view = inflater.inflate(R.layout.expense_card, parent, false)
            ExpenseViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.income_card, parent, false)
            IncomeViewHolder(view)
        }
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        if (holder is ExpenseViewHolder) {
            holder.bind(item,context,fragmentManager)
        } else if (holder is IncomeViewHolder) {
            holder.bind(item,context,fragmentManager)
        }

    }

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Transactions, context: Context, fragmentManager: FragmentManager) {
            // Bind data to expense card views
            itemView.findViewById<TextView>(R.id.expenseCardcategory).text = item.category
            itemView.findViewById<TextView>(R.id.smalltitle).text = item.title
            itemView.findViewById<TextView>(R.id.expenseAmount).text = "- $. ${item.amount}"
            itemView.findViewById<TextView>(R.id.Expensedate).text=item.date
            val btnEdit=itemView.findViewById<ImageButton>(R.id.btnEditTransactions);
            val btnDelete =itemView.findViewById<ImageButton>(R.id.btnDeleteTransaction)
            btnEdit.setOnClickListener {
                val dialog = updateTransaction.newInstance(item)
                dialog.show(fragmentManager, "edit_transactions")
            }

            btnDelete.setOnClickListener {
                val dialog = DeleteConfirmBox.newInstance(item)
                dialog.show(fragmentManager, "edit_transactions")
            }
        }
    }

    class IncomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Transactions,context: Context, fragmentManager: FragmentManager) {
            // Bind data to income card views
            itemView.findViewById<TextView>(R.id.incomeCardcategory).text = item.category
            itemView.findViewById<TextView>(R.id.smalltitle).text = item.title
            itemView.findViewById<TextView>(R.id.incomeAmount).text = "+ $. ${item.amount}"
            itemView.findViewById<TextView>(R.id.IncomeDate).text=item.date
            val btnEdit=itemView.findViewById<ImageButton>(R.id.btnEditTransactions);
            val btnDelete =itemView.findViewById<ImageButton>(R.id.btnDeleteTransaction)
            btnEdit.setOnClickListener {
                val dialog = updateTransaction.newInstance(item)
                dialog.show(fragmentManager, "edit_transactions")
            }
            btnDelete.setOnClickListener {
                val dialog = DeleteConfirmBox.newInstance(item)
                dialog.show(fragmentManager, "edit_transactions")
            }
        }
    }

    fun updateData(newList: List<Transactions>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged() // Notify the adapter that data has changed
    }




}