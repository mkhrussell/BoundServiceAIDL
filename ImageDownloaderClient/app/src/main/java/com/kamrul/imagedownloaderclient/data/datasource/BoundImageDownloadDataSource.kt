package com.kamrul.imagedownloaderclient.data.datasource

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.kamrul.imagedownloaderclient.data.service.ServiceDownloadState
import com.kamrul.imagedownloaderclient.service.ImageDownloadService
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@ViewModelScoped
class BoundImageDownloadDataSource @Inject constructor(
    @ApplicationContext context: Context
) {

    private val appContext = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val _downloadStates = MutableStateFlow<ServiceDownloadState>(ServiceDownloadState.Idle)

    val downloadStates: StateFlow<ServiceDownloadState> = _downloadStates.asStateFlow()

    private var boundService: ImageDownloadService? = null
    private var stateCollectionJob: Job? = null
    private var pendingConnection = CompletableDeferred<ImageDownloadService>()
    private var isBound = false
    private var isBinding = false

    suspend fun requestDownload(url: String) {
        _downloadStates.value = ServiceDownloadState.Connecting
        val service = awaitService()
        service.downloadImage(url)
    }

    fun close() {
        stateCollectionJob?.cancel()
        stateCollectionJob = null

        if (isBound) {
            appContext.unbindService(serviceConnection)
        }

        isBound = false
        isBinding = false
        boundService = null

        if (!pendingConnection.isCompleted) {
            pendingConnection.cancel()
        }

        scope.cancel()
    }

    private suspend fun awaitService(): ImageDownloadService {
        boundService?.let { return it }
        bindServiceIfNeeded()
        return pendingConnection.await()
    }

    private fun bindServiceIfNeeded() {
        if (isBound || isBinding) {
            return
        }

        _downloadStates.value = ServiceDownloadState.Connecting
        if (pendingConnection.isCompleted || pendingConnection.isCancelled) {
            pendingConnection = CompletableDeferred()
        }

        isBinding = true
        val didBind = appContext.bindService(
            Intent(appContext, ImageDownloadService::class.java),
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )

        if (!didBind) {
            isBinding = false
            _downloadStates.value = ServiceDownloadState.Error("Unable to bind to the download service")
            pendingConnection.completeExceptionally(
                IllegalStateException("Unable to bind to the download service")
            )
        }
    }

    private fun attachToService(service: ImageDownloadService) {
        stateCollectionJob?.cancel()
        stateCollectionJob = scope.launch {
            service.downloadState.collect { state ->
                _downloadStates.value = state
            }
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as? ImageDownloadService.LocalBinder ?: return
            boundService = binder.getService()
            isBound = true
            isBinding = false

            val connectedService = requireNotNull(boundService)
            attachToService(connectedService)
            if (!pendingConnection.isCompleted) {
                pendingConnection.complete(connectedService)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            stateCollectionJob?.cancel()
            stateCollectionJob = null
            boundService = null
            isBound = false
            isBinding = false
            _downloadStates.value = ServiceDownloadState.Error("Download service disconnected")

            if (pendingConnection.isCompleted) {
                pendingConnection = CompletableDeferred()
            }
        }
    }
}
