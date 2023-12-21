package com.z1.bluetoothconnection.presentation.viewmodel

import com.z1.bluetoothconnection.domain.model.BluetoothDevice
import com.z1.bluetoothconnection.domain.message.BluetoothMessage
import com.z1.bluetoothconnection.domain.model.BluetoothDeviceDomain

data class BluetoothUiState(
    val scannedDevices: List<BluetoothDevice> = emptyList(),
    val pairedDevices: List<BluetoothDevice> = emptyList(),
    val bluetoothDevice: BluetoothDeviceDomain? = null,
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val errorMessage: String? = null,
    val messages: List<BluetoothMessage> = emptyList()
)
