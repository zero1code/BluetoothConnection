package com.z1.bluetoothconnection.presentation.chat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.z1.bluetoothconnection.databinding.ChatItemBinding
import com.z1.bluetoothconnection.domain.message.BluetoothMessage

class ChatAdapter() :
    ListAdapter<BluetoothMessage, ChatAdapter.ChatViewHolder>(DiffCallback) {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatViewHolder {
        return ChatViewHolder(
            ChatItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ChatViewHolder(private var binding: ChatItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(bluetoothMessage: BluetoothMessage) {

            binding.apply {
               if (bluetoothMessage.isFromLocalUser) {
                   tvChat2.visibility = View.GONE
                   tvSentChat2.visibility = View.GONE
                   tvChat1.text = bluetoothMessage.message
               } else {
                   tvChat1.visibility = View.GONE
                   tvSentChat1.visibility = View.GONE
                   tvChat2.text = bluetoothMessage.message
               }
                tvSentChat1.text = bluetoothMessage.senderName
                tvSentChat2.text = bluetoothMessage.senderName
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<BluetoothMessage>() {
            override fun areItemsTheSame(
                oldItem: BluetoothMessage,
                newItem: BluetoothMessage
            ): Boolean {
                return (oldItem.message == newItem.message)
            }

            override fun areContentsTheSame(
                oldItem: BluetoothMessage,
                newItem: BluetoothMessage
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
