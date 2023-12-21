package com.z1.bluetoothconnection.presentation.connectingdevice.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.z1.bluetoothconnection.databinding.ItemBluetoothBinding
import com.z1.bluetoothconnection.domain.model.BluetoothDevice

class BluetoothDevicesAdapter() :
    ListAdapter<BluetoothDevice, BluetoothDevicesAdapter.BluetoothDevicesViewHolder>(DiffCallback) {

    var onBluetoothDeviceClickListener: (bluetoothDevice: BluetoothDevice) -> Unit = {}

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BluetoothDevicesViewHolder {
        return BluetoothDevicesViewHolder(
            ItemBluetoothBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: BluetoothDevicesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BluetoothDevicesViewHolder(private var binding: ItemBluetoothBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(bluetoothDevice: BluetoothDevice) {

            binding.apply {
                tvBluetoothDeviceName.text = bluetoothDevice.name
                tvBluetoothDeviceAddress.text = bluetoothDevice.address
            }

            itemView.setOnClickListener {
                onBluetoothDeviceClickListener(bluetoothDevice)
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<BluetoothDevice>() {
            override fun areItemsTheSame(
                oldItem: BluetoothDevice,
                newItem: BluetoothDevice
            ): Boolean {
                return (oldItem.address == newItem.address ||
                        oldItem.name == newItem.name)
            }

            override fun areContentsTheSame(
                oldItem: BluetoothDevice,
                newItem: BluetoothDevice
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
