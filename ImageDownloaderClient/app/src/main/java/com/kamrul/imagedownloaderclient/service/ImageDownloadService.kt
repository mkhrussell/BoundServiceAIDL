package com.kamrul.imagedownloaderclient.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.kamrul.imagedownloaderclient.data.downloader.HttpUrlImageDownloader
import com.kamrul.imagedownloaderclient.data.downloader.ImageFileDownloader
import com.kamrul.imagedownloaderclient.data.service.ServiceDownloadState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ImageDownloadService : Service() {

    private val binder = LocalBinder()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _downloadState = MutableStateFlow<ServiceDownloadState>(ServiceDownloadState.Idle)

    val downloadState: StateFlow<ServiceDownloadState> = _downloadState.asStateFlow()

    private lateinit var imageFileDownloader: ImageFileDownloader

    override fun onCreate() {
        super.onCreate()
        imageFileDownloader = HttpUrlImageDownloader(applicationContext)
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    fun downloadImage(url: String) {
        serviceScope.launch {
            _downloadState.value = ServiceDownloadState.Downloading
            _downloadState.value = runCatching {
                imageFileDownloader.download(url)
            }.fold(
                onSuccess = { file ->
                    ServiceDownloadState.Success(file.absolutePath)
                },
                onFailure = { throwable ->
                    ServiceDownloadState.Error(throwable.message ?: "Download failed")
                }
            )
        }
    }

    inner class LocalBinder : Binder() {
        fun getService(): ImageDownloadService = this@ImageDownloadService
    }
}
