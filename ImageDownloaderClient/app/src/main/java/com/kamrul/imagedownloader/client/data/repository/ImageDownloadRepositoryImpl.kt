package com.kamrul.imagedownloader.client.data.repository

import com.kamrul.imagedownloader.client.data.datasource.RemoteImageDownloadServiceDataSource
import com.kamrul.imagedownloader.client.domain.repository.ImageDownloadRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class ImageDownloadRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteImageDownloadServiceDataSource,
) : ImageDownloadRepository {
    override fun observeServiceConnection(): Flow<Boolean> = remoteDataSource.isServiceConnected

    override fun connectToService() {
        remoteDataSource.connect()
    }

    override suspend fun downloadImage(imageUrl: String): ByteArray {
        return remoteDataSource.downloadImage(imageUrl)
    }
}
