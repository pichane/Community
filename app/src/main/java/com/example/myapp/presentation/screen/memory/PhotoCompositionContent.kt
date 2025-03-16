package com.example.myapp.presentation.screen.memory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myapp.R
import com.example.myapp.domain.model.FromUser
import com.example.myapp.domain.model.Memory
import com.example.myapp.presentation.screen.home.HandlePhotoDisplay
import com.example.myapp.presentation.theme.dimensions

@Composable
fun PhotoCompositionContent(
    uiState: PhotoCompositionUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        when {
            uiState.isLoading -> {
                LoadingState(modifier = Modifier.align(Alignment.Center))
            }

            uiState.memory != null -> {
                SuccessState(
                    memory = uiState.memory,
                    title = uiState.memory.title,
                    modifier = Modifier.fillMaxSize()
                )
            }

            uiState.error != null -> {
                ErrorState(
                    message = uiState.error,
                    onRetry = onRetry,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    CircularProgressIndicator(modifier = modifier)
}

@Composable
private fun SuccessState(
    memory: Memory,
    title: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // Display the memory in the same layout as in HomeScreen but full screen
        FullScreenPhotoComposition(
            photos = memory.photos,
            isHorizontal = memory.isHorizontal,
            modifier = Modifier.fillMaxSize()
        )

        // Community name overlay
        CommunityNameOverlay(
            name = title,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(MaterialTheme.dimensions.iconSmall)
        )
    }
}

@Composable
private fun CommunityNameOverlay(
    name: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = name,
        style = MaterialTheme.typography.titleLarge,
        color = Color.White,
        modifier = modifier
            .background(
                color = Color.Black.copy(alpha = 0.5f),
                shape = MaterialTheme.shapes.small
            )
            .padding(horizontal = MaterialTheme.dimensions.iconSmall, vertical = MaterialTheme.dimensions.spaceExtraSmall)
    )
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(MaterialTheme.dimensions.iconSmall),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(MaterialTheme.dimensions.iconExtraLargePlus),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.iconSmall))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.iconSmall))

        Button(onClick = onRetry) {
            Text(stringResource(R.string.retry))
        }
    }
}

@Composable
fun FullScreenPhotoComposition(
    photos: List<FromUser>,
    isHorizontal: Boolean,
    modifier: Modifier = Modifier,
    mainUserId: String = stringResource(R.string.user_main)
) {
    Box(modifier = modifier) {
        if (isHorizontal) {
            HorizontalPhotoLayout(
                photos = photos,
                mainUserId = mainUserId,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            VerticalPhotoLayout(
                photos = photos,
                mainUserId = mainUserId,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun HorizontalPhotoLayout(
    photos: List<FromUser>,
    mainUserId: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        photos.take(4).forEachIndexed { index, fromUser ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                HandlePhotoDisplay(
                    photo = fromUser,
                    mainUserId = mainUserId
                )
            }

            if (index < photos.size - 1 && index < 3) {
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun VerticalPhotoLayout(
    photos: List<FromUser>,
    mainUserId: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Upper half
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // Top left photo
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                if (photos.isNotEmpty()) {
                    HandlePhotoDisplay(photos[0], mainUserId)
                }
            }

            VerticalDivider()

            // Top right photo
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                if (photos.size > 1) {
                    HandlePhotoDisplay(photos[1], mainUserId)
                }
            }
        }

        HorizontalDivider()

        // Lower half
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // Bottom left photo
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                if (photos.size > 2) {
                    HandlePhotoDisplay(photos[2], mainUserId)
                }
            }

            if (photos.size > 3) {
                VerticalDivider()

                // Bottom right photo
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    HandlePhotoDisplay(photos[3], mainUserId)
                }
            }
        }
    }
}
