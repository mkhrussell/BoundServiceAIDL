package com.kamrul.imagedownloaderclient.presentation.download

sealed interface DownloadIntent {
    data class UrlChanged(val value: String) : DownloadIntent
    data object DownloadClicked : DownloadIntent
}

data class DownloadUiState(
    val imageUrl: String = "",
    val isLoading: Boolean = false,
    val statusMessage: String = "Idle",
    val imagePath: String? = null
)
