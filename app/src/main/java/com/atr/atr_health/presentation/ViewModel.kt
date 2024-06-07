package com.atr.atr_health.presentation

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.DataTypeAvailability
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.atr.atr_health.TAG
import com.atr.atr_health.data.DataService
import com.atr.atr_health.data.MeasureMessage
import com.atr.atr_health.data.SessionData
import com.atr.atr_health.helpers.SupportedState
import com.atr.atr_health.helpers.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch

class DataViewModel(
    private val fileDirPath: String,
    private val dataService: DataService
) : ViewModel() {
    val supportedState: MutableState<SupportedState> = mutableStateOf(SupportedState.Startup)
    val uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Menu)
    val hr: MutableStateFlow<Double> = MutableStateFlow(0.0)
    val hrAvailability: MutableStateFlow<Availability> = MutableStateFlow(DataTypeAvailability.UNKNOWN)

    private lateinit var sessionData: SessionData
    private lateinit var sessionJob: Job

    init {

        viewModelScope.launch {
            supportedState.value = if (dataService.hasCapabilities()) SupportedState.Supported
            else SupportedState.NotSupported
        }
    }

    fun startCollection() {
        // start a new session
        Log.d(TAG, "Creating new session file")
        sessionData = SessionData(fileDirPath, dataService.data)

        // collect data
        Log.d(TAG, "Starting data collection")
        uiState.value = UiState.Collection
        sessionJob = viewModelScope.launch {
            dataService.measureFlow()
                .takeWhile { uiState.value == UiState.Collection}
                .collect { message ->
                    when (message) {
                        is MeasureMessage.DataMessage -> {
                            Log.d(TAG, "Collected ${message.type} data: ${message.data}")
                            sessionData.appendDatapoint(dataService.data)
                            if (message.type == DataType.HEART_RATE_BPM.name) hr.compareAndSet(hr.value, message.data)
                        }
                        is MeasureMessage.AvailabilityMessage -> {
                            Log.d(TAG, "Collected ${message.type} Availability: ${message.availability}")
                            if (message.type == DataType.HEART_RATE_BPM.name) hrAvailability.compareAndSet(hrAvailability.value, message.availability)
                        }
                    }
                }
        }
    }

    fun stopCollection() {
        if (sessionJob.isActive) sessionJob.cancel()
        uiState.value = UiState.Menu
    }
}

class DataViewModelFactory(
    private val dataService: DataService,
    private val fileDirPath: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DataViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DataViewModel(fileDirPath = fileDirPath, dataService = dataService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}