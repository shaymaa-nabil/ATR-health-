package com.atr.atr_health.presentation.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.atr.atr_health.R
import com.atr.atr_health.TAG
import com.atr.atr_health.presentation.theme.ATRHealthTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MenuScreen(
    onStartClicked: () -> Unit,
    permissions: List<String>
) {
    val permissionState = rememberMultiplePermissionsState(
        permissions = permissions,
        onPermissionsResult = { results ->
            val allPermissionsGranted = results.all { it.value }
            if (allPermissionsGranted) {
                onStartClicked()
            }
        }
    )

    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            text = stringResource(id = R.string.app_name),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.display1,
            fontSize = 24.sp
        )
        Button(
            modifier = Modifier.fillMaxWidth(0.5f),
            onClick = {
                Log.d(TAG, "Permissions Granted? ${permissionState.allPermissionsGranted}")
                if (permissionState.allPermissionsGranted) {
                    onStartClicked()
                }
                else {
                    for (permission in permissionState.revokedPermissions) {
                        Log.d(TAG, "Revoked: ${permission.permission}")
                    }
                    permissionState.launchMultiplePermissionRequest()
                }
            }
        ) {
            Text(text = stringResource(id = R.string.start))
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
fun MenuScreenPreview() {
    ATRHealthTheme {
        MenuScreen(
            onStartClicked = {},
            permissions = listOf()
        )
    }
}