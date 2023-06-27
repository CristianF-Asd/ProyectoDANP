package com.example.proyectodanp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyectodanp.ui.theme.ProyectoDANPTheme

import androidx.lifecycle.lifecycleScope
import androidx.compose.runtime.mutableStateOf
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback
import com.amazonaws.regions.Regions
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class MainActivity : ComponentActivity() {


    private val clientId = "android-client"
    private val region = Regions.YOUR_REGION
    private val endpoint = "YOUR_AWS_IOT_ENDPOINT"
    private val keyStorePath = "YOUR_KEYSTORE_PATH"
    private val keyStorePassword = "YOUR_KEYSTORE_PASSWORD"
    private val topic = "YOUR_TOPIC"

    private val mqttManager by lazy { AWSIotMqttManager(clientId, endpoint) }
    private val dataState = mutableStateOf("No data")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectoDANPTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    setContent {
                        Column {
                            Text(text = dataState.value)
                        }
                    }
                    connectToAwsIoT()
                }
            }
        }
    }

    private fun connectToAwsIoT() {
        mqttManager.connect(keyStorePath, keyStorePassword) { status, throwable ->
            if (status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connected) {
                subscribeToTopic()
            } else if (throwable != null) {
                throwable.printStackTrace()
            }
        }
    }

    private fun subscribeToTopic() {
        mqttManager.subscribeToTopic(topic, AWSIotMqttQos.QOS0, AWSIotMqttNewMessageCallback { _, data ->
            val message = String(data)
            updateDataState(message)
        })
    }

    private fun updateDataState(message: String) {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                dataState.value = message
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewMainActivity() {
    Column {
        Text(text = "Preview data")
    }
}



