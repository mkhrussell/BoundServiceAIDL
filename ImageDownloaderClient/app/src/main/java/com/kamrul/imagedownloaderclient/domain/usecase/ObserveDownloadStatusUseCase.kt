package com.kamrul.imagedownloaderclient.domain.usecase

import com.kamrul.imagedownloaderclient.domain.model.DownloadStatus
import com.kamrul.imagedownloaderclient.domain.repository.ImageDownloadRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveDownloadStatusUseCase @Inject constructor(
    private val repository: ImageDownloadRepository
) {
    operator fun invoke(): Flow<DownloadStatus> = repository.downloadStatuses
}
