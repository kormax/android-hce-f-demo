package com.kormax.hcefdemo

import android.content.ComponentName
import android.content.Intent
import android.nfc.cardemulation.HostNfcFService
import android.os.Bundle
import android.util.Log

class FelicaHostNfcFService : HostNfcFService() {
    private val TAG = this::class.java.simpleName

    override fun processNfcFPacket(commandPacket: ByteArray?, extras: Bundle?): ByteArray? {
        sendBroadcast(
            Intent(ACTION_NFC_F_PACKET_RECEIVED).apply {
                putExtra(
                    EXTRA_KEY_DATA,
                    commandPacket,
                )
            }
        )

        if (commandPacket == null) {
            return ByteArray(0)
        }
        Log.i(TAG, "processNfcFPacket(${commandPacket.toHexString(HexFormat.Default)})")
        return ByteArray(0)
    }

    override fun onDeactivated(reason: Int) {
        sendBroadcast(Intent(ACTION_DEACTIVATED))
        Log.i(TAG, "onDeactivated(${reason})")
    }

    companion object {
        val COMPONENT = ComponentName(
            FelicaHostNfcFService::class.java.packageName,
            FelicaHostNfcFService::class.java.name
        )

        val ACTION_DEACTIVATED = "${this::class.java.packageName}.DEACTIVATED"
        val ACTION_NFC_F_PACKET_RECEIVED = "${this::class.java.packageName}.NFC_F_PACKET_RECEIVED"
        val EXTRA_KEY_DATA = "data"
    }
}