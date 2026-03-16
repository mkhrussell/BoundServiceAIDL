package com.kamrul.imagedownloader.client.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.kamrul.imagedownloader.client.presentation.download.DownloadIntent
import com.kamrul.imagedownloader.client.presentation.download.DownloadViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<DownloadViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewState by viewModel.viewState.collectAsState()

            DisposableEffect(Unit) {
                viewModel.onIntent(DownloadIntent.Initialize)
                onDispose { }
            }

            MaterialTheme {
                DownloadScreen(
                    viewState = viewState,
                    onIntent = viewModel::onIntent,
                )
            }
        }
    }
}
