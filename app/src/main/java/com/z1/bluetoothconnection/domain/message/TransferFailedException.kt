package com.z1.bluetoothconnection.domain.message

import java.io.IOException

class TransferFailedException: IOException("Reading incoming data failed")