package com.z1.bluetoothconnection.presentation.connectingdevice.inapp

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.z1.bluetoothconnection.Application
import com.z1.bluetoothconnection.R
import com.z1.bluetoothconnection.databinding.ActivityConnectInAppBinding
import com.z1.bluetoothconnection.databinding.ActivityMainBinding
import com.z1.bluetoothconnection.presentation.chat.ChatActivity
import com.z1.bluetoothconnection.presentation.connectingdevice.adapter.BluetoothDevicesAdapter
import com.z1.bluetoothconnection.presentation.viewmodel.BluetoothViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ConnectInAppActivity : AppCompatActivity() {
    private var _binding: ActivityConnectInAppBinding? = null
    private val binding get() = _binding!!

    private val pairedDevicesAdapter by lazy { BluetoothDevicesAdapter() }
    private val scannedDevicesAdapter by lazy { BluetoothDevicesAdapter() }

    private val bluetoothViewModel = Application.bluetoothViewModel
        //BluetoothViewModel(Application.appContainer.bluetoothController)

    private var bluetoothJob: Job? = null

    private val bluetoothManager by lazy {
        applicationContext.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private val bluetoothPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permission ->
        when {
            permission.getOrDefault(
                Manifest.permission.BLUETOOTH_CONNECT,
                false
            ) -> checkIfBluetoothIsActive()

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
        _binding = ActivityConnectInAppBinding.inflate(layoutInflater)
        setContentView(binding.root)


        bindRecyclerView()
        bindObservers()
        bindListeners()
    }

    private fun bindObservers() {
        bluetoothJob = lifecycleScope.launch {
            bluetoothViewModel.state.collect { state ->
                if (state.isConnecting) {
                    Toast.makeText(this@ConnectInAppActivity, "Conectando...", Toast.LENGTH_SHORT).show()
                }

                if (state.isConnected) {
                    Intent(this@ConnectInAppActivity, ChatActivity::class.java).also {
                        startActivity(it)
                    }
                }

                if (state.bluetoothDevice != null) {
                    binding.tvConnectedIn.text = "Conectado em: ${state.bluetoothDevice?.name}"
                    binding.btDisconnect.visibility = View.VISIBLE
                } else {
                    binding.tvConnectedIn.text = "Conectado em: Nada"
                    binding.btDisconnect.visibility = View.INVISIBLE
                }

                if (state.errorMessage != null) {
                    Toast.makeText(this@ConnectInAppActivity, state.errorMessage, Toast.LENGTH_SHORT)
                        .show()
                    binding.tvConnectedIn.text = "Conectado em: Nada"
                    binding.btDisconnect.visibility = View.INVISIBLE
                }

                pairedDevicesAdapter.submitList(state.pairedDevices)
                scannedDevicesAdapter.submitList(state.scannedDevices)
            }
        }
    }

    private fun bindListeners() {
        binding.apply {
            btScan.setOnClickListener {
                if (isAndroidVersionAtLeast12()) requestBluetoothPermission()
                else findBluetoothDevices()
            }

            btWaitConnection.setOnClickListener {
                bluetoothViewModel.waitForIncomingConnections()
            }

            btDisconnect.setOnClickListener {
                bluetoothViewModel.disconnectFromDevice()
            }
        }

        pairedDevicesAdapter.onBluetoothDeviceClickListener = { bluetoothDevice ->
            bluetoothViewModel.connectToDevice(bluetoothDevice)
        }

        scannedDevicesAdapter.onBluetoothDeviceClickListener = { bluetoothDevice ->
            bluetoothViewModel.connectToDevice(bluetoothDevice)
        }
    }

    private fun bindRecyclerView() {
        binding.apply {
            rvPairedDevices.adapter = pairedDevicesAdapter
//            rvPairedDevices.addItemDecoration(DividerItemDecoration(
//                baseContext,
//                LinearLayoutManager.VERTICAL
//            ))
            rvScannedDevices.adapter = scannedDevicesAdapter
//            rvScannedDevices.addItemDecoration(DividerItemDecoration(
//                baseContext,
//                LinearLayoutManager.VERTICAL
//            ))
        }
    }

    private fun checkIfBluetoothIsActive() {
        bluetoothAdapter?.let { btAdapter ->
            if (!btAdapter.isEnabled) activeBluetoothIntent()
            else findBluetoothDevices()
        } ?: Toast.makeText(this, "Sem suporte ao bluetooth", Toast.LENGTH_SHORT).show()
    }

    private fun activeBluetoothIntent() {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        enableBluetoothLauncher.launch(intent)
    }

    private fun findBluetoothDevices() {
        bluetoothViewModel.startScan()
    }


    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestBluetoothPermission() {
        bluetoothPermissionRequest.launch(BLUETOOTH_PERMISSION)
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
    private fun isAndroidVersionAtLeast12() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    override fun onPause() {
        super.onPause()
        bluetoothJob?.cancel()
        bluetoothJob = null
    }

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