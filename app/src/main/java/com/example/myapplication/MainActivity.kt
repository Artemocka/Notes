package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.db.DatabaseProvider

class MainActivity : AppCompatActivity() {

    lateinit var navController: NavController
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DatabaseProviderWrap.createDao(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        window.statusBarColor = 0

        setContentView(binding.root)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)


        WindowCompat.setDecorFitsSystemWindows(window, false)


    }
}