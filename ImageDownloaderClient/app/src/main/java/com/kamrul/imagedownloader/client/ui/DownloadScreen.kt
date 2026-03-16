package com.kamrul.imagedownloader.client.ui

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kamrul.imagedownloader.client.R
import com.kamrul.imagedownloader.client.presentation.download.DownloadIntent
import com.kamrul.imagedownloader.client.presentation.download.DownloadViewState

@Composable
fun DownloadScreen(
    viewState: DownloadViewState,
    onIntent: (DownloadIntent) -> Unit,
) {
    val previewBitmap = remember(viewState.downloadedImage) {
        viewState.downloadedImage?.let { bytes ->
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = if (viewState.isServiceConnected) {
                "Connected to ImageDownloaderService"
            } else {
                "Waiting for ImageDownloaderService"
            },
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewState.imageUrl,
            onValueChange = { onIntent(DownloadIntent.ImageUrlChanged(it)) },
            label = { Text(text = stringResource(R.string.hint_image_url)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onIntent(DownloadIntent.DownloadImageClicked) },
            enabled = viewState.isServiceConnected && !viewState.isDownloading,
        ) {
            Text(text = stringResource(R.string.action_download_image))
        }

        Spacer(modifier = Modifier.height(16.dp))

        viewState.errorMessage?.let { errorMessage ->
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    viewState.isDownloading -> CircularProgressIndicator()
                    previewBitmap != null -> Image(
                        bitmap = previewBitmap,
                        contentDescription = stringResource(R.string.content_description_downloaded_image),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                    )
                    else -> Text(
                        text = stringResource(R.string.placeholder_no_image),
                        modifier = Modifier.padding(24.dp),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
