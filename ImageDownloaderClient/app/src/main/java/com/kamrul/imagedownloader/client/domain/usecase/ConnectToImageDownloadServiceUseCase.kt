package com.kamrul.imagedownloader.client.domain.usecase

import com.kamrul.imagedownloader.client.domain.repository.ImageDownloadRepository
import javax.inject.Inject

class ConnectToImageDownloadServiceUseCase @Inject constructor(
    private val repository: ImageDownloadRepository,
) {
    operator fun invoke() {
        repository.connectToService()
    }
}
