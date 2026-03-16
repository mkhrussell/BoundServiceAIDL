package com.kamrul.imagedownloaderclient.di

import com.kamrul.imagedownloaderclient.data.repository.ImageDownloadRepositoryImpl
import com.kamrul.imagedownloaderclient.domain.repository.ImageDownloadRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class DownloadModule {

    @Binds
    @ViewModelScoped
    abstract fun bindImageDownloadRepository(
        repository: ImageDownloadRepositoryImpl
    ): ImageDownloadRepository
}
