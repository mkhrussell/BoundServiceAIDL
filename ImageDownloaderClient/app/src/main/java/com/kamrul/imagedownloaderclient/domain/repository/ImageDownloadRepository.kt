package com.kamrul.imagedownloaderclient.domain.repository

import com.kamrul.imagedownloaderclient.domain.model.DownloadStatus
import kotlinx.coroutines.flow.Flow

interface ImageDownloadRepository {
    val downloadStatuses: Flow<DownloadStatus>

    suspend fun downloadImage(url: String)

    fun close()
}
