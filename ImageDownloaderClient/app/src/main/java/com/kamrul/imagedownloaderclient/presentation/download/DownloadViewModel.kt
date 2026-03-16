package com.kamrul.imagedownloaderclient.presentation.download

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kamrul.imagedownloaderclient.domain.model.DownloadStatus
import com.kamrul.imagedownloaderclient.domain.repository.ImageDownloadRepository
import com.kamrul.imagedownloaderclient.domain.usecase.DownloadImageUseCase
import com.kamrul.imagedownloaderclient.domain.usecase.ObserveDownloadStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val observeDownloadStatusUseCase: ObserveDownloadStatusUseCase,
    private val downloadImageUseCase: DownloadImageUseCase,
    private val repository: ImageDownloadRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DownloadUiState())
    val uiState: StateFlow<DownloadUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeDownloadStatusUseCase().collect(::reduceStatus)
        }
    }

    fun onIntent(intent: DownloadIntent) {
        when (intent) {
            is DownloadIntent.UrlChanged -> {
                _uiState.update { currentState ->
                    currentState.copy(imageUrl = intent.value)
                }
            }

            DownloadIntent.DownloadClicked -> submitDownload()
        }
    }

    override fun onCleared() {
        repository.close()
        super.onCleared()
    }

    private fun submitDownload() {
        val url = uiState.value.imageUrl.trim()
        if (url.isBlank()) {
            _uiState.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    statusMessage = "Please enter an image URL"
                )
            }
            return
        }

        viewModelScope.launch {
            downloadImageUseCase(url)
        }
    }

    private fun reduceStatus(status: DownloadStatus) {
        _uiState.update { currentState ->
            when (status) {
                DownloadStatus.Idle -> currentState.copy(
                    isLoading = false,
                    statusMessage = "Idle"
                )

                DownloadStatus.Connecting -> currentState.copy(
                    isLoading = true,
                    statusMessage = "Binding to service...",
                    imagePath = null
                )

                DownloadStatus.Downloading -> currentState.copy(
                    isLoading = true,
                    statusMessage = "Downloading...",
                    imagePath = null
                )

                is DownloadStatus.Success -> currentState.copy(
                    isLoading = false,
                    statusMessage = "Download successful",
                    imagePath = status.imagePath
                )

                is DownloadStatus.Error -> currentState.copy(
                    isLoading = false,
                    statusMessage = status.message,
                    imagePath = null
                )
            }
        }
    }
}
