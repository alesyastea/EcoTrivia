package com.alesyastea.ecotrivia

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.alesyastea.ecotrivia.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.TRANSPARENT
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.fragment_splash)

        CoroutineScope(Dispatchers.Main).launch {
            delay(2500)
            setContentView(binding.root)
            binding.bottomNavMenu.setupWithNavController(
                navController = binding.navHostFragment.findNavController()
            )
        }
    }
}
