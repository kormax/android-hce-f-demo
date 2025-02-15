package com.kormax.hcefdemo

import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import android.nfc.cardemulation.NfcFCardEmulation
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kormax.hcefdemo.ui.theme.HcefdemoTheme

class MainActivity : ComponentActivity() {
    private val TAG = this::class.java.simpleName
    private var logs: List<String> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var logs: List<String> by remember { mutableStateOf(this.logs) }

            SystemBroadcastReceiver(FelicaHostNfcFService.ACTION_DEACTIVATED) { intent ->
                logs += "Deactivated"
            }

            SystemBroadcastReceiver(FelicaHostNfcFService.ACTION_NFC_F_PACKET_RECEIVED) { intent ->
                val data = intent?.getByteArrayExtra(FelicaHostNfcFService.EXTRA_KEY_DATA)
                if (data == null) {
                    return@SystemBroadcastReceiver
                }
                logs += "Packet received ${data.toHexString(HexFormat.Default)}"
            }

            HcefdemoTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            colors =
                                TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    titleContentColor = MaterialTheme.colorScheme.primary,
                                ),
                            title = { Text("NFC F Demo") },
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        userScrollEnabled = true,
                    ) {
                        items(logs.size) { index ->
                            val log = logs[index]
                            Text(
                                text = log,
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        logs = mutableListOf()

        var result = false;

        val nfcAdapter: NfcAdapter? =
            try {
                NfcAdapter.getDefaultAdapter(this)
            } catch (e: Exception) {
                logs += "Could not get NfcAdapter due to $e"
                null
            }

        val nfcFCardEmulation =
            try {
                NfcFCardEmulation.getInstance(nfcAdapter)
            } catch (e: Exception) {
                logs += "Could not get NfcFCardEmulation due to $e"
                return
            }

        logs += "HostNfcFService component ${FelicaHostNfcFService.COMPONENT.packageName} ${FelicaHostNfcFService.COMPONENT.className}"

        val hceFSupported = this.packageManager.hasSystemFeature(
            PackageManager.FEATURE_NFC_HOST_CARD_EMULATION_NFCF
        )
        logs += "Got HCE-F supported $hceFSupported"

        val nfcid2 = nfcFCardEmulation.getNfcid2ForService(FelicaHostNfcFService.COMPONENT)
        logs += "Got NFCID2 $nfcid2"
        val systemCode = nfcFCardEmulation.getSystemCodeForService(FelicaHostNfcFService.COMPONENT)
        logs += "Got System Code $systemCode"

        result = nfcFCardEmulation.disableService(this)
        logs += "Disabled service $result"

        result = nfcFCardEmulation.setNfcid2ForService(
            FelicaHostNfcFService.COMPONENT,
            "02FE010203040506"
        )
        logs += "Set NFCID2 for service $result"
        result = nfcFCardEmulation.registerSystemCodeForService(
            FelicaHostNfcFService.COMPONENT,
            "4000"
        )
        logs += "Set system code for service $result"

        result = nfcFCardEmulation.enableService(this, FelicaHostNfcFService.COMPONENT)
        logs += "Enabled service $result"
    }
}
