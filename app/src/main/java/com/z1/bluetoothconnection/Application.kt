package com.z1.bluetoothconnection

import android.app.Application
import com.z1.bluetoothconnection.presentation.viewmodel.BluetoothViewModel

class Application: Application() {
    companion object {
        lateinit var appContainer: AppContainer
        lateinit var bluetoothViewModel: BluetoothViewModel
    }

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainerImpl(this)
        bluetoothViewModel = BluetoothViewModel(appContainer.bluetoothController)
    }
}