package com.kamrul.imagedownloader.client.data.datasource

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.kamrul.imagedownloader.service.aidl.IImageDownloadService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@Singleton
class RemoteImageDownloadServiceDataSource @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val appContext = context.applicationContext
    private val remoteService = MutableStateFlow<IImageDownloadService?>(null)
    private val _isServiceConnected = MutableStateFlow(false)
    val isServiceConnected: StateFlow<Boolean> = _isServiceConnected.asStateFlow()

    @Volatile
    private var bindRequested = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            remoteService.value = IImageDownloadService.Stub.asInterface(service)
            _isServiceConnected.value = remoteService.value != null
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            remoteService.value = null
            _isServiceConnected.value = false
        }
    }

    fun connect() {
        if (bindRequested) return

        synchronized(this) {
            if (bindRequested) return
            bindRequested = true
        }

        val isBound = appContext.bindService(
            Intent(BIND_ACTION).apply { `package` = SERVICE_PACKAGE_NAME },
            serviceConnection,
            Context.BIND_AUTO_CREATE,
        )

        if (!isBound) {
            bindRequested = false
            throw IllegalStateException("ImageDownloaderService is unavailable.")
        }
    }

    suspend fun downloadImage(imageUrl: String): ByteArray {
        val service = remoteService.filterNotNull().first()
        return withContext(Dispatchers.IO) {
            service.downloadImage(imageUrl)
        }
    }

    companion object {
        private const val SERVICE_PACKAGE_NAME = "com.kamrul.imagedownloader.service"
        private const val BIND_ACTION =
            "com.kamrul.imagedownloader.service.action.BIND_IMAGE_DOWNLOAD_SERVICE"
    }
}
