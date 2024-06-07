package com.atr.atr_health.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataTypeAvailability
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import com.atr.atr_health.R
import com.atr.atr_health.presentation.components.DataLabel
import com.atr.atr_health.presentation.theme.ATRHealthTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@Composable
fun DataScreen(
    hr: Double,
    availability: Availability,
    onStopClicked: () -> Unit
) {
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        val icon =
            if (availability == DataTypeAvailability.AVAILABLE) Icons.Default.Favorite
            else Icons.Default.HeartBroken
        val text =
            if (availability == DataTypeAvailability.AVAILABLE) hr.toString()
            else stringResource(id = R.string.acquiring)

        DataLabel(
            text = text,
            icon = icon,
            color = Color.Red
        )
        Button(
            modifier = Modifier.fillMaxWidth(0.5f),
            onClick = onStopClicked
        ) {
            Text(text = stringResource(id = R.string.stop))
        }
    }
}

@ExperimentalPermissionsApi
@Preview(
    device = Devices.WEAR_OS_SMALL_ROUND,
    showBackground = false,
    showSystemUi = true
)
@Composable
fun DataScreenPreview() {
    ATRHealthTheme {
        DataScreen(hr = 87.0,
            availability = DataTypeAvailability.AVAILABLE,
            onStopClicked = {}
        )
    }
}