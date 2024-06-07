package com.atr.atr_health.presentation

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import com.atr.atr_health.data.DataService
import com.atr.atr_health.presentation.theme.ATRHealthTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atr.atr_health.PERMISSIONS
import com.atr.atr_health.TAG
import com.atr.atr_health.helpers.SupportedState
import com.atr.atr_health.helpers.UiState
import com.atr.atr_health.presentation.screens.DataScreen
import com.atr.atr_health.presentation.screens.MenuScreen
import com.atr.atr_health.presentation.screens.NotSupportedScreen
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TherapyApp(
    dataService: DataService,
    fileDirPath: String
) {
    ATRHealthTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            timeText = { TimeText() }
        ) {
            val viewModel: DataViewModel = viewModel(
                factory = DataViewModelFactory(
                    fileDirPath = fileDirPath,
                    dataService = dataService
                )
            )

            val uiState by viewModel.uiState.collectAsState()
            val supportedState by viewModel.supportedState
            val hr by viewModel.hr.collectAsState()
            val hrAvailability by viewModel.hrAvailability.collectAsState()

            if (supportedState is SupportedState.Supported) {
                when (uiState) {
                    is UiState.Menu -> {
                        Log.d(TAG, "UI is Menu")
                        MenuScreen(
                            onStartClicked = {
                                Log.d(TAG, "Start Clicked!")
                                viewModel.startCollection()
                            },
                            permissions = PERMISSIONS
                        )
                    }
                    is UiState.Collection -> {
                        Log.d(TAG, "UI is Collection")
                        DataScreen(
                            hr = hr,
                            availability = hrAvailability,
                            onStopClicked = {
                                Log.d(TAG, "Stop Clicked!")
                                viewModel.stopCollection()
                            }
                        )
                    }
                }
            }
            else if (supportedState is SupportedState.NotSupported) {
                NotSupportedScreen()
            }
        }
    }
}