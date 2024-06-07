package com.atr.atr_health

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.atr.atr_health.presentation.TherapyApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            TherapyApp(dataService = (application as MainApplication).dataService, fileDirPath = this.filesDir.path)
        }
    }
}