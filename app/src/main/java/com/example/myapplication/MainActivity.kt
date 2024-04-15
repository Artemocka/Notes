package com.example.myapplication

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.viemodel.ActivityViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private val activityViewModel by viewModels<ActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityViewModel

        binding = ActivityMainBinding.inflate(layoutInflater)
        window.statusBarColor = 0
        setContentView(binding.root)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)


        WindowCompat.setDecorFitsSystemWindows(window, false)


    }
}