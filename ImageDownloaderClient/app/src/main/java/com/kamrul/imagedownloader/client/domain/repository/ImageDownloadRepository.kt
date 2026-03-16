package com.kamrul.imagedownloader.client.domain.repository

import kotlinx.coroutines.flow.Flow

interface ImageDownloadRepository {
    fun observeServiceConnection(): Flow<Boolean>

    fun connectToService()

    suspend fun downloadImage(imageUrl: String): ByteArray
}
