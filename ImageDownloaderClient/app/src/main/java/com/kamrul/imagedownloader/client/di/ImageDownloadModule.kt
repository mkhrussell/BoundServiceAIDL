package com.kamrul.imagedownloader.client.di

import com.kamrul.imagedownloader.client.data.repository.ImageDownloadRepositoryImpl
import com.kamrul.imagedownloader.client.domain.repository.ImageDownloadRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ImageDownloadModule {
    @Binds
    @Singleton
    abstract fun bindImageDownloadRepository(
        repository: ImageDownloadRepositoryImpl,
    ): ImageDownloadRepository
}
