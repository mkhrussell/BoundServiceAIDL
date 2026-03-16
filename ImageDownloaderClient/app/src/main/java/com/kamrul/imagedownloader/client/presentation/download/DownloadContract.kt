package com.kamrul.imagedownloader.client.presentation.download

const val DEFAULT_IMAGE_URL =
    "https://developer.android.com/static/images/brand/Android_Robot.png"

data class DownloadViewState(
    val imageUrl: String = DEFAULT_IMAGE_URL,
    val isServiceConnected: Boolean = false,
    val isDownloading: Boolean = false,
    val downloadedImage: ByteArray? = null,
    val errorMessage: String? = null,
)

sealed interface DownloadIntent {
    data object Initialize : DownloadIntent

    data class ImageUrlChanged(val imageUrl: String) : DownloadIntent

    data object DownloadImageClicked : DownloadIntent

    data object ErrorDismissed : DownloadIntent
}

sealed interface DownloadPartialState {
    data class ServiceConnectionChanged(val isConnected: Boolean) : DownloadPartialState

    data class ImageUrlChanged(val imageUrl: String) : DownloadPartialState

    data object DownloadStarted : DownloadPartialState

    data class DownloadSucceeded(val imageBytes: ByteArray) : DownloadPartialState

    data class DownloadFailed(val message: String) : DownloadPartialState

    data object ErrorDismissed : DownloadPartialState
}
