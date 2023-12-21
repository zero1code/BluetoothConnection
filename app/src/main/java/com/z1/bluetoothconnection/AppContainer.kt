package com.z1.bluetoothconnection

import android.content.Context
import com.z1.bluetoothconnection.domain.bluetooth.AndroidBluetoothController
import com.z1.bluetoothconnection.domain.bluetooth.BluetoothController

interface AppContainer {
    val bluetoothController: BluetoothController
}

class AppContainerImpl(
    private val appContext: Context
): AppContainer {
    override val bluetoothController: BluetoothController by lazy {
        AndroidBluetoothController(appContext)
    }
}