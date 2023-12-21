package com.z1.bluetoothconnection.presentation.mainactivity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.z1.bluetoothconnection.databinding.ActivityMainBinding
import com.z1.bluetoothconnection.presentation.connectingdevice.inapp.ConnectInAppActivity
import com.z1.bluetoothconnection.presentation.connectingdevice.insettings.ConnectInSettingsActivity

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindListeners()
    }

    private fun bindListeners() {
        binding.apply {
            btInApp.setOnClickListener {
                Intent(this@MainActivity, ConnectInAppActivity::class.java).also {
                    startActivity(it)
                }
            }
            btInSettings.setOnClickListener {
                Intent(this@MainActivity, ConnectInSettingsActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
    }
}