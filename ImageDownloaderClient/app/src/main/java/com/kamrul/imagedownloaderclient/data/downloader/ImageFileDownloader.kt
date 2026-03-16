package com.kamrul.imagedownloaderclient.data.downloader

import java.io.File

interface ImageFileDownloader {
    suspend fun download(url: String): File
}
