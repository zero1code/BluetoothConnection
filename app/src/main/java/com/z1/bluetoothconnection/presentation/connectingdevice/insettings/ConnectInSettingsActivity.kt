package com.z1.bluetoothconnection.presentation.connectingdevice.insettings

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.z1.bluetoothconnection.Application
import com.z1.bluetoothconnection.databinding.ActivityConnectInSettingsBinding
import com.z1.bluetoothconnection.presentation.connectingdevice.adapter.BluetoothDevicesAdapter
import com.z1.bluetoothconnection.presentation.viewmodel.BluetoothViewModel
import kotlinx.coroutines.launch

class ConnectInSettingsActivity : AppCompatActivity() {
    private var _binding: ActivityConnectInSettingsBinding? = null
    private val binding get() = _binding!!

    private val pairedDevicesAdapter by lazy { BluetoothDevicesAdapter() }
    private val scannedDevicesAdapter by lazy { BluetoothDevicesAdapter() }

    private val bluetoothViewModel = Application.bluetoothViewModel

    private val bluetoothPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permission ->
        when {
            permission.getOrDefault(
                Manifest.permission.BLUETOOTH_CONNECT,
                false
            ) -> openBluetoothSettings()

            else ->
                Toast.makeText(this, "Permissao nao concedida", Toast.LENGTH_SHORT).show()
        }
    }

    private val enableBluetoothLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                findBluetoothDevices()
            } else {
                Toast.makeText(
                    this,
                    "Ativação do Bluetooth cancelada pelo usuário",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityConnectInSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindObservers()
        bindListeners()
    }

    private fun bindObservers() {
        lifecycleScope.launch {
            bluetoothViewModel.state.collect { state ->
                if (state.isConnecting) {
                    Toast.makeText(this@ConnectInSettingsActivity, "Conectando...", Toast.LENGTH_SHORT).show()
                }

                if (state.isConnected) {
                    Toast.makeText(this@ConnectInSettingsActivity, "Voce esta conectado.", Toast.LENGTH_SHORT)
                        .show()
                }

                if (state.bluetoothDevice != null) {
                    binding.tvConnectedIn.text = "Conectado em: ${state.bluetoothDevice?.name}"
                } else {
                    binding.tvConnectedIn.text = "Conectado em: Nada"
                }

                if (state.errorMessage != null) {
                    Toast.makeText(this@ConnectInSettingsActivity, state.errorMessage, Toast.LENGTH_SHORT)
                        .show()
                    binding.tvConnectedIn.text = "Conectado em: Nada"
                }

                pairedDevicesAdapter.submitList(state.pairedDevices)
                scannedDevicesAdapter.submitList(state.scannedDevices)
            }
        }
    }

    private fun bindListeners() {
        binding.apply {
            btInSettings.setOnClickListener {
                if (isAndroidVersionAtLeast12()) requestBluetoothPermission()
                else openBluetoothSettings()
            }

        }

        pairedDevicesAdapter.onBluetoothDeviceClickListener = { bluetoothDevice ->
            bluetoothViewModel.connectToDevice(bluetoothDevice)
        }

        scannedDevicesAdapter.onBluetoothDeviceClickListener = { bluetoothDevice ->
            bluetoothViewModel.connectToDevice(bluetoothDevice)
        }
    }

    private fun openBluetoothSettings() {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        enableBluetoothLauncher.launch(intent)
    }

    private fun findBluetoothDevices() {
        Intent(Settings.ACTION_BLUETOOTH_SETTINGS).also {
            startActivity(it)
        }
    }


    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestBluetoothPermission() {
        bluetoothPermissionRequest.launch(BLUETOOTH_PERMISSION)
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
    private fun isAndroidVersionAtLeast12() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        @RequiresApi(Build.VERSION_CODES.S)
        private val BLUETOOTH_PERMISSION = arrayOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
        )

        private const val REQUEST_ENABLE_BLUETOOTH = 1
    }
}