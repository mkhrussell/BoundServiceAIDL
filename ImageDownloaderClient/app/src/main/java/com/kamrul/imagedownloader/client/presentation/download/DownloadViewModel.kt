package com.kamrul.imagedownloader.client.presentation.download

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kamrul.imagedownloader.client.domain.usecase.ConnectToImageDownloadServiceUseCase
import com.kamrul.imagedownloader.client.domain.usecase.DownloadImageUseCase
import com.kamrul.imagedownloader.client.domain.usecase.ObserveServiceConnectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val connectToImageDownloadService: ConnectToImageDownloadServiceUseCase,
    private val downloadImage: DownloadImageUseCase,
    observeServiceConnection: ObserveServiceConnectionUseCase,
) : ViewModel() {
    private val _viewState = MutableStateFlow(DownloadViewState())
    val viewState: StateFlow<DownloadViewState> = _viewState.asStateFlow()

    private var initialized = false

    init {
        viewModelScope.launch {
            observeServiceConnection().collectLatest { isConnected ->
                reduce(DownloadPartialState.ServiceConnectionChanged(isConnected))
            }
        }
    }

    fun onIntent(intent: DownloadIntent) {
        when (intent) {
            DownloadIntent.Initialize -> initialize()
            is DownloadIntent.ImageUrlChanged -> {
                reduce(DownloadPartialState.ImageUrlChanged(intent.imageUrl))
            }
            DownloadIntent.DownloadImageClicked -> handleDownloadClick()
            DownloadIntent.ErrorDismissed -> reduce(DownloadPartialState.ErrorDismissed)
        }
    }

    private fun initialize() {
        if (initialized) return
        initialized = true

        runCatching { connectToImageDownloadService() }
            .onFailure { throwable ->
                reduce(
                    DownloadPartialState.DownloadFailed(
                        throwable.message ?: "ImageDownloaderService is unavailable.",
                    ),
                )
            }
    }

    private fun handleDownloadClick() {
        val currentState = _viewState.value
        if (currentState.isDownloading) return
        if (!currentState.isServiceConnected) {
            reduce(DownloadPartialState.DownloadFailed("Service is not connected."))
            return
        }

        viewModelScope.launch {
            reduce(DownloadPartialState.DownloadStarted)

            runCatching { downloadImage(currentState.imageUrl) }
                .onSuccess { imageBytes ->
                    if (imageBytes.isEmpty()) {
                        reduce(DownloadPartialState.DownloadFailed("Downloaded image was empty."))
                    } else {
                        reduce(DownloadPartialState.DownloadSucceeded(imageBytes))
                    }
                }
                .onFailure { throwable ->
                    reduce(
                        DownloadPartialState.DownloadFailed(
                            throwable.message ?: "Image download failed.",
                        ),
                    )
                }
        }
    }

    private fun reduce(partialState: DownloadPartialState) {
        _viewState.value = when (partialState) {
            is DownloadPartialState.ServiceConnectionChanged -> {
                _viewState.value.copy(
                    isServiceConnected = partialState.isConnected,
                    isDownloading = if (partialState.isConnected) {
                        _viewState.value.isDownloading
                    } else {
                        false
                    },
                    errorMessage = if (!partialState.isConnected && _viewState.value.isDownloading) {
                        "Service disconnected."
                    } else {
                        _viewState.value.errorMessage
                    },
                )
            }
            is DownloadPartialState.ImageUrlChanged -> {
                _viewState.value.copy(
                    imageUrl = partialState.imageUrl,
                    errorMessage = null,
                )
            }
            DownloadPartialState.DownloadStarted -> {
                _viewState.value.copy(
                    isDownloading = true,
                    errorMessage = null,
                )
            }
            is DownloadPartialState.DownloadSucceeded -> {
                _viewState.value.copy(
                    isDownloading = false,
                    downloadedImage = partialState.imageBytes,
                    errorMessage = null,
                )
            }
            is DownloadPartialState.DownloadFailed -> {
                _viewState.value.copy(
                    isDownloading = false,
                    errorMessage = partialState.message,
                )
            }
            DownloadPartialState.ErrorDismissed -> {
                _viewState.value.copy(errorMessage = null)
            }
        }
    }
}
