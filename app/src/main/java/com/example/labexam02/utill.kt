package com.example.labexam02

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class utill {
    val incomeFileName="incomes.json";
    val expenseFileName="expenses.json";

    //saving data as jason file
    fun saveListtoFile(context: Context,fileName: String,data: List<Any>){
        val jsonString= Gson().toJson(data);
        val file= File(context.filesDir,fileName);
        file.writeText(jsonString);
    }

    //loading list from the file
    inline fun<reified T> loadDataFromFile(context: Context,fileName: String): MutableList<T>{
        val file= File(context.filesDir,fileName);
        if (!file.exists()){
            return mutableListOf();
        }
        val type=object: TypeToken<MutableList<T>>(){}.type;
        val json=file.readText();


        return Gson().fromJson(json,type);
    }

    //add new item
    fun addAnItem(context: Context, isExpense: Boolean, Title: String, Amount: Double){
        if(isExpense==true){
            val expenses=loadDataFromFile<Expense>(context,expenseFileName);
            val id=if(expenses.isEmpty())1 else expenses.maxOf { it.id }+1;
            val newExpense= Expense(id=id, title = Title, amount = Amount);
            expenses.add(newExpense);
            saveListtoFile(context,expenseFileName,expenses);
        }else{
            val incomes=loadDataFromFile<Expense>(context,incomeFileName);
            val id=if(incomes.isEmpty())1 else incomes.maxOf { it.id }+1;
            val newExpense= Expense(id=id, title = Title, amount = Amount);
            incomes.add(newExpense);
            saveListtoFile(context,incomeFileName,incomes);
        }

    }

    //updating an existing Item
    fun updateItem(context: Context,isExpense: Boolean,Title: String,Amount: Double,id:Int){
        if (isExpense==true){
            val expenses=loadDataFromFile<Expense>(context,expenseFileName);
            val UpdatedExpense=expenses.map { if(it.id==id)it.copy(title = Title, amount = Amount)else it };
            saveListtoFile(context,expenseFileName,UpdatedExpense);
        }else{
            val incomes=loadDataFromFile<Expense>(context,incomeFileName);
            val UpdatedIncome=incomes.map { if(it.id==id)it.copy(title = Title, amount = Amount)else it };
            saveListtoFile(context,incomeFileName,UpdatedIncome);
        }
    }

    //Deleting an Item
    fun deleteItem(context: Context, id:Int, isExpense: Boolean){
        if (isExpense==true){
            val expenses=loadDataFromFile<Expense>(context,expenseFileName);
            val filteredExpenses=expenses.filter { it.id!=id };
            saveListtoFile(context,expenseFileName,filteredExpenses);
        }else{
            val incomes=loadDataFromFile<Expense>(context,incomeFileName);
            val filteredIncomes=incomes.filter { it.id!=id };
            saveListtoFile(context,incomeFileName,filteredIncomes);
        }
    }
}