package com.example.labexam02

import java.util.Date

data class Transactions(
    val id:Int,
    val title:String,
    val amount:Double,
    val date: String,
    val category: String,
    val isExpense: Boolean
)

