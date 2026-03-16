package com.kamrul.imagedownloader.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.kamrul.imagedownloader.service.aidl.IImageDownloadService
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking

class ImageDownloadService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val binder = object : IImageDownloadService.Stub() {
        override fun downloadImage(imageUrl: String): ByteArray = runBlocking(Dispatchers.IO) {
            downloadImageBytes(imageUrl)
        }
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun downloadImageBytes(imageUrl: String): ByteArray {
        val connection = (URL(imageUrl).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 15_000
            readTimeout = 15_000
            doInput = true
        }

        return connection.useAndDisconnect {
            if (responseCode !in 200..299) {
                throw IllegalStateException("HTTP $responseCode while downloading image.")
            }

            inputStream.use { input ->
                ByteArrayOutputStream().use { output ->
                    input.copyTo(output)
                    output.toByteArray()
                }
            }
        }
    }
}

private inline fun HttpURLConnection.useAndDisconnect(block: HttpURLConnection.() -> ByteArray): ByteArray {
    return try {
        connect()
        block()
    } finally {
        disconnect()
    }
}
