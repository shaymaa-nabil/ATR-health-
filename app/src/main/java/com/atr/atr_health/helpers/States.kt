package com.atr.atr_health.helpers

sealed class UiState {
    object Menu : UiState()
    object Collection : UiState()
}

sealed class SupportedState {
    object Startup : SupportedState()
    object Supported : SupportedState()
    object NotSupported : SupportedState()
}