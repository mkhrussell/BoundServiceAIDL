package com.kamrul.imagedownloaderclient.domain.usecase

import com.kamrul.imagedownloaderclient.domain.repository.ImageDownloadRepository
import javax.inject.Inject

class DownloadImageUseCase @Inject constructor(
    private val repository: ImageDownloadRepository
) {
    suspend operator fun invoke(url: String) {
        repository.downloadImage(url)
    }
}
