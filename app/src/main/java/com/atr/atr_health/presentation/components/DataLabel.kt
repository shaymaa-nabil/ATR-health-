package com.atr.atr_health.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.atr.atr_health.R
import com.atr.atr_health.presentation.theme.ATRHealthTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@Composable
fun DataLabel(
    text: String,
    icon: ImageVector,
    color: Color,
) {
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon (
            imageVector = icon,
            contentDescription = stringResource(id = R.string.icon),
            tint = color
        )
        Text (
            modifier = Modifier.padding(8.dp),
            text = text,
            style = MaterialTheme.typography.display1,
            fontSize = 24.sp
        )
    }
}

@ExperimentalPermissionsApi
@Preview(
    device = Devices.WEAR_OS_SMALL_ROUND,
    showBackground = false,
    showSystemUi = true
)
@Composable
fun DataLabelPreview() {
    ATRHealthTheme {
        DataLabel(
            text = "87.3",
            icon = Icons.Default.Favorite,
            color = Color.Red
        )
    }
}