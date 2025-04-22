package com.example.labexam02

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FinanceAdapter(private val items: List<Transactions>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
            holder.bind(item)
        } else if (holder is IncomeViewHolder) {
            holder.bind(item)
        }
    }

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Transactions) {
            // Bind data to expense card views
            itemView.findViewById<TextView>(R.id.expenseCardcategory).text = item.title
            itemView.findViewById<TextView>(R.id.expenseAmount).text = "- Rs. ${item.amount}"
        }
    }

    class IncomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Transactions) {
            // Bind data to income card views
            itemView.findViewById<TextView>(R.id.incomeCardcategory).text = item.title
            itemView.findViewById<TextView>(R.id.incomeAmount).text = "+ Rs. ${item.amount}"
        }
    }

}