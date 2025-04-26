package com.example.labexam02

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class utill {
    val expenseFileName="Transactions.json";

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
            val expenses=loadDataFromFile<Transactions>(context,expenseFileName);
            val id=if(expenses.isEmpty())1 else expenses.maxOf { it.id }+1;
            val newExpense= Transactions(id=id, title = Title, amount = Amount,isExpense=isExpense);
            expenses.add(newExpense);
            saveListtoFile(context,expenseFileName,expenses);
    }

    //updating an existing Item
    fun updateItem(context: Context,isExpense: Boolean,Title: String,Amount: Double,id:Int){
            val expenses=loadDataFromFile<Transactions>(context,expenseFileName);
            val UpdatedExpense=expenses.map { if(it.id==id)it.copy(title = Title, amount = Amount)else it };
            saveListtoFile(context,expenseFileName,UpdatedExpense);


    }

    //Deleting an Item
    fun deleteItem(context: Context, id:Int, isExpense: Boolean){
            val expenses=loadDataFromFile<Transactions>(context,expenseFileName);
            val filteredExpenses=expenses.filter { it.id!=id };
            saveListtoFile(context,expenseFileName,filteredExpenses);
    }
}