package com.z1.bluetoothconnection.presentation.chat

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.z1.bluetoothconnection.Application
import com.z1.bluetoothconnection.databinding.ActivityChatBinding
import com.z1.bluetoothconnection.presentation.chat.adapter.ChatAdapter
import com.z1.bluetoothconnection.presentation.viewmodel.BluetoothViewModel
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {
    private var _binding: ActivityChatBinding? = null
    private val binding get() = _binding!!

    private val bluetoothViewModel = Application.bluetoothViewModel

    private val chatAdapter by lazy { ChatAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindListener()
        bindObservers()
        bindRecyclerView()
    }

    private fun bindObservers() {
        lifecycleScope.launch {
            bluetoothViewModel.state.collect { state ->
                if (state.isConnecting) {
                    Toast.makeText(this@ChatActivity, "Conectando...", Toast.LENGTH_SHORT).show()
                }

                if (state.isConnected) {
                    binding.tvStatusConnection.text = "Connection status: Connected"
                }

                if (state.bluetoothDevice != null) {
                    binding.tvDeviceConnected.text = "Connected device: ${state.bluetoothDevice.name}"
                } else {
                    binding.tvStatusConnection.text = "Connection status: Disconnected"
                    binding.tvDeviceConnected.text = "Connected device: None"
                }

                if (state.errorMessage != null) {
                    Toast.makeText(this@ChatActivity, state.errorMessage, Toast.LENGTH_SHORT)
                        .show()

                }

                chatAdapter.submitList(state.messages)
            }
        }
    }

    private fun bindListener() {
        binding.apply {
            btSendMessage.setOnClickListener {
                val message = etMessage.text.toString().trim()
                bluetoothViewModel.sendMessage(message)
                etMessage.setText("")
            }
        }
    }

    private fun bindRecyclerView() {
        binding.apply {
            rvChat.adapter = chatAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}