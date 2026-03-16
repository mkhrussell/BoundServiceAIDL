package com.kamrul.imagedownloaderclient.domain.model

sealed interface DownloadStatus {
    data object Idle : DownloadStatus
    data object Connecting : DownloadStatus
    data object Downloading : DownloadStatus
    data class Success(val imagePath: String) : DownloadStatus
    data class Error(val message: String) : DownloadStatus
}
