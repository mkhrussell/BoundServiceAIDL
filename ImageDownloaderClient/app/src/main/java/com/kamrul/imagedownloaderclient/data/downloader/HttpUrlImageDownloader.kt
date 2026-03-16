package com.kamrul.imagedownloaderclient.data.downloader

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HttpUrlImageDownloader(
    private val context: Context
) : ImageFileDownloader {

    override suspend fun download(url: String): File = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null

        try {
            connection = (URL(url).openConnection() as HttpURLConnection).apply {
                connectTimeout = 10_000
                readTimeout = 10_000
                doInput = true
                connect()
            }

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                throw IllegalStateException("HTTP error code: ${connection.responseCode}")
            }

            val outputFile = File(
                context.cacheDir,
                "downloaded_image_${System.currentTimeMillis()}.jpg"
            )

            connection.inputStream.use { inputStream ->
                FileOutputStream(outputFile).use { outputStream ->
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    while (true) {
                        val bytesRead = inputStream.read(buffer)
                        if (bytesRead == -1) {
                            break
                        }
                        outputStream.write(buffer, 0, bytesRead)
                    }
                    outputStream.flush()
                }
            }

            outputFile
        } finally {
            connection?.disconnect()
        }
    }
}
