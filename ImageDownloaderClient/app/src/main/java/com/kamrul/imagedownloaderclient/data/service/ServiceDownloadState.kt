package com.kamrul.imagedownloaderclient.data.service

sealed interface ServiceDownloadState {
    data object Idle : ServiceDownloadState
    data object Connecting : ServiceDownloadState
    data object Downloading : ServiceDownloadState
    data class Success(val imagePath: String) : ServiceDownloadState
    data class Error(val message: String) : ServiceDownloadState
}
