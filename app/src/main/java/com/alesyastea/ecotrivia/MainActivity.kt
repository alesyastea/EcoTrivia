package com.alesyastea.ecotrivia

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.alesyastea.ecotrivia.databinding.ActivityMainBinding
import com.alesyastea.ecotrivia.ui.battery.BatteryReceiver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var batteryReceiver: BatteryReceiver
    private lateinit var root_view: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.fragment_splash)

        CoroutineScope(Dispatchers.Main).launch {
            delay(2500)
            setContentView(binding.root)
            binding.bottomNavMenu.setupWithNavController(
                navController = binding.navHostFragment.findNavController()
            )
        }

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        root_view = findViewById(android.R.id.content)
        batteryReceiver = BatteryReceiver(root_view, this)

        hideAndroidToolbar()
    }

    private fun hideAndroidToolbar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    override fun onStart() {
        super.onStart()
        batteryReceiver.registerReceiver()
    }

    override fun onStop() {
        super.onStop()
        batteryReceiver.unregisterReceiver()
    }
}
