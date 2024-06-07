package com.atr.atr_health.data

import android.content.Context
import android.util.Log
import androidx.concurrent.futures.await
import androidx.health.services.client.HealthServices
import androidx.health.services.client.MeasureCallback
import androidx.health.services.client.MeasureClient
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataPoint
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataTypeAvailability
import androidx.health.services.client.data.DeltaDataType
import androidx.health.services.client.data.IntervalDataPoint
import androidx.health.services.client.data.SampleDataPoint
import com.atr.atr_health.TAG
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import java.util.*

class DataService(
    context: Context,
    dataTypes: List<DeltaDataType<Double, out DataPoint<Double>>>
) {
    private val context: Context
    private val measureClient: MeasureClient
    private val dataTypes: MutableList<DeltaDataType<Double, out DataPoint<Double>>>
    val data: HashMap<String, Double>
    val availability: HashMap<String, Availability>

    // Firebase Database reference
    private val database = Firebase.database
    private val dataRef = database.reference.child("data")

    init {
        this.context = context
        measureClient = HealthServices.getClient(context).measureClient

        this.dataTypes = dataTypes.toMutableList()

        data = HashMap()
        availability = HashMap()
        for (type in dataTypes) {
            data[type.name] = 0.0
            availability[type.name] = DataTypeAvailability.UNKNOWN
        }
    }

    suspend fun hasCapabilities(): Boolean {
        val capabilities = measureClient.getCapabilitiesAsync().await()
        for (item in dataTypes.toTypedArray()) {
            if (item !in capabilities.supportedDataTypesMeasure) {
                Log.d(TAG, "${item.name} is not capable... removing")
                dataTypes.remove(item)
                data.remove(item.name)
            }
            else Log.d(TAG, "${item.name} is capable")
        }
        Log.d(TAG, dataTypes.toString())
        return dataTypes.size > 0
    }
    private fun sendDataToFirebase() {
        val timestamp = Date().time // Get current timestamp
        val dataMap = HashMap<String, Any>()
        dataMap["timestamp"] = timestamp
        data.forEach { (key, value) ->
            dataMap[key] = value
        }
        // Push data to Firebase
        dataRef.push().setValue(dataMap)
            .addOnSuccessListener {
                Log.i(TAG, "Data sent to Firebase successfully")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error sending data to Firebase: $e")
            }
    }


    fun measureFlow() = callbackFlow {
        val callbacks: HashMap<DeltaDataType<Double, out DataPoint<Double>>, MeasureCallback> = HashMap()
        for (type in dataTypes) {
            val callback = object : MeasureCallback {
                var isAvailable = false

                override fun onAvailabilityChanged(
                    dataType: DeltaDataType<*, *>,
                    availability: Availability
                ) {
                    this@DataService.availability[type.name] = availability
                    isAvailable = availability == DataTypeAvailability.AVAILABLE
                    trySendBlocking(MeasureMessage.AvailabilityMessage(type.name, availability))
                }

                override fun onDataReceived(data: DataPointContainer) {
                    val value = when (val dataPoint = data.getData(type).last()) {
                        is SampleDataPoint -> dataPoint.value
                        is IntervalDataPoint -> dataPoint.value
                        else -> 0.0
                    }

                    if (isAvailable && isActive) {
                        this@DataService.data[type.name] = value
                        trySendBlocking(MeasureMessage.DataMessage(type.name, value))
                        sendDataToFirebase() // Send data to Firebase
                    }
                }
            }

            callbacks[type] = callback
        }

        runBlocking {
            for (item in callbacks) {
                Log.d(TAG, "Registering ${item.key.name} callback.")
                measureClient.registerMeasureCallback(item.key, item.value)
            }
        }

        awaitClose {
            runBlocking {
                for (item in callbacks) {
                    Log.d(TAG, "Unregistering ${item.key.name} callback.")
                    measureClient.unregisterMeasureCallbackAsync(item.key, item.value).await()
                }
                channel.close()
                Log.d(TAG, "Cleaned up measure flow")
            }
        }
    }

}

sealed class MeasureMessage {
    class DataMessage(val type: String, val data: Double) : MeasureMessage()
    class AvailabilityMessage(val type: String, val availability: Availability) : MeasureMessage ()
}
