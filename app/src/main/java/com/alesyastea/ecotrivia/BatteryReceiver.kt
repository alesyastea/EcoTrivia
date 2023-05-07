package com.alesyastea.ecotrivia

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.os.BatteryManager
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

class BatteryReceiver(private val root: View, private val context: Context) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val batteryLevel: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val batteryStatus: Int = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        if (batteryLevel <= 20 && batteryStatus != BatteryManager.BATTERY_STATUS_CHARGING) {
            showBatterySnackbar(batteryLevel)
        }
    }

    @SuppressLint("StringFormatMatches")
    private fun showBatterySnackbar(batteryLevel: Int) {
        val batteryText = context.resources.getString(R.string.battery_text, batteryLevel)

        val snackbar = Snackbar.make(root, batteryText, Snackbar.LENGTH_LONG)
        snackbar.view.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.flamingo))
        snackbar.setActionTextColor(ContextCompat.getColor(context, R.color.white))
        val icon = ContextCompat.getDrawable(context, R.drawable.ic_battery_low)
        snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            .setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null)
        snackbar.show()
    }

    fun registerReceiver() {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(this, filter)
    }

    fun unregisterReceiver() {
        context.unregisterReceiver(this)
    }
}

