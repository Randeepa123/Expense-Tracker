package com.example.labexam02

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.labexam02.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ViewBinding setup
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root) // Use ViewBinding's root

        if(savedInstanceState==null){
            replaceFragment(HomeFragment())
        }

        ViewCompat.setOnApplyWindowInsetsListener(mainBinding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Ensure bottomNavigationView doesn't modify insets
        mainBinding.bottomNavigationView.setOnApplyWindowInsetsListener(null)
        mainBinding.bottomNavigationView.setPadding(0, 0, 0, 0)

        mainBinding.bottomNavigationView.setOnItemSelectedListener {item->
            when(item.itemId){
                R.id.home_icon -> replaceFragment(HomeFragment())
                R.id.add_icon->showDialog()
                R.id.profile_icon -> replaceFragment(ProfileFragment())
                R.id.analytics_con -> replaceFragment(AnalyticFragment())
                else -> false
            }
        }


    }

    private fun replaceFragment(fragment: Fragment): Boolean{

        supportFragmentManager.beginTransaction()
        .replace(R.id.frame,fragment)
        .commit()

        return true
    }

    private fun showDialog(): Boolean{
        val dialogBox= DialogBox_Class();
        dialogBox.show(supportFragmentManager,"CustomAddExpense");
        return true;
    }

    public fun restoreBottomNavigationSelection() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.frame)

        when (currentFragment) {
            is HomeFragment -> mainBinding.bottomNavigationView.selectedItemId = R.id.home_icon
            is ProfileFragment -> mainBinding.bottomNavigationView.selectedItemId = R.id.profile_icon
            is AnalyticFragment -> mainBinding.bottomNavigationView.selectedItemId = R.id.analytics_con
        }
    }
}