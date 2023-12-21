package com.z1.bluetoothconnection.domain

import com.z1.bluetoothconnection.domain.message.BluetoothMessage

sealed interface ConnectionResult {
    object ConnectionEstablished: ConnectionResult
    data class TransferSucceeded(val message: BluetoothMessage): ConnectionResult
    data class Error(val message: String): ConnectionResult

}