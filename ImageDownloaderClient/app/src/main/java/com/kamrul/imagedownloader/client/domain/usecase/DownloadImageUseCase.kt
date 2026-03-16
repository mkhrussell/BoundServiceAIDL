package com.kamrul.imagedownloader.client.domain.usecase

import com.kamrul.imagedownloader.client.domain.repository.ImageDownloadRepository
import javax.inject.Inject

class DownloadImageUseCase @Inject constructor(
    private val repository: ImageDownloadRepository,
) {
    suspend operator fun invoke(imageUrl: String): ByteArray {
        val trimmedUrl = imageUrl.trim()
        require(trimmedUrl.isNotEmpty()) { "Enter an image URL." }
        return repository.downloadImage(trimmedUrl)
    }
}
