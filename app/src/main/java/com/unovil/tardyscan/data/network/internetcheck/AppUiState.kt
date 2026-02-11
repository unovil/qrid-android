package com.unovil.tardyscan.data.network.internetcheck

sealed interface AppUiState {
    data object NoInternet : AppUiState
    data object Loading : AppUiState
    data object NotAuthenticated : AppUiState
    data object Authenticated : AppUiState
}
