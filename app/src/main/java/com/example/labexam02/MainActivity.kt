package com.example.labexam02

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.labexam02.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ViewBinding setup
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root) // Use ViewBinding's root

        ViewCompat.setOnApplyWindowInsetsListener(mainBinding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Ensure bottomNavigationView doesn't modify insets
        mainBinding.bottomNavigationView.setOnApplyWindowInsetsListener(null)
        mainBinding.bottomNavigationView.setPadding(0, 0, 0, 0)

    }
}