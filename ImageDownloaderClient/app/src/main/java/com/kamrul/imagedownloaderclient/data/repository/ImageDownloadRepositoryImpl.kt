package com.kamrul.imagedownloaderclient.data.repository

import com.kamrul.imagedownloaderclient.data.datasource.BoundImageDownloadDataSource
import com.kamrul.imagedownloaderclient.data.service.ServiceDownloadState
import com.kamrul.imagedownloaderclient.domain.model.DownloadStatus
import com.kamrul.imagedownloaderclient.domain.repository.ImageDownloadRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@ViewModelScoped
class ImageDownloadRepositoryImpl @Inject constructor(
    private val dataSource: BoundImageDownloadDataSource
) : ImageDownloadRepository {

    override val downloadStatuses: Flow<DownloadStatus> =
        dataSource.downloadStates
            .map { state -> state.toDomainModel() }
            .distinctUntilChanged()

    override suspend fun downloadImage(url: String) {
        dataSource.requestDownload(url)
    }

    override fun close() {
        dataSource.close()
    }

    private fun ServiceDownloadState.toDomainModel(): DownloadStatus = when (this) {
        ServiceDownloadState.Idle -> DownloadStatus.Idle
        ServiceDownloadState.Connecting -> DownloadStatus.Connecting
        ServiceDownloadState.Downloading -> DownloadStatus.Downloading
        is ServiceDownloadState.Success -> DownloadStatus.Success(imagePath)
        is ServiceDownloadState.Error -> DownloadStatus.Error(message)
    }
}
