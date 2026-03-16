package com.kamrul.imagedownloader.client.domain.usecase

import com.kamrul.imagedownloader.client.domain.repository.ImageDownloadRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveServiceConnectionUseCase @Inject constructor(
    private val repository: ImageDownloadRepository,
) {
    operator fun invoke(): Flow<Boolean> = repository.observeServiceConnection()
}
