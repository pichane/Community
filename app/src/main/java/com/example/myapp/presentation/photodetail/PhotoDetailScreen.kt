package com.example.myapp.presentation.photodetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailScreen(
    photoId: Int,
    viewModel: PhotoDetailViewModel = koinViewModel(),
    onBack: () -> Unit,
    onDelete: () -> Unit
) {
    // Collect UI state
    val uiState by viewModel.uiState.collectAsState()

    // Load the photo when the screen is first displayed
    LaunchedEffect(photoId) {
        viewModel.onEvent(PhotoDetailEvent.LoadPhoto(photoId))
    }

    // Handle navigation events
    LaunchedEffect(uiState.navigationEvent) {
        when (uiState.navigationEvent) {
            is NavigationEvent.NavigateBack -> onBack()
            is NavigationEvent.PhotoDeleted -> onDelete()
            null -> { /* No action needed */
            }
        }

        // Clear the navigation event once handled
        if (uiState.navigationEvent != null) {
            viewModel.onEvent(PhotoDetailEvent.NavigationHandled)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        uiState.photo?.let { photo ->
            PhotoDetailContent(
                photo = photo,
                missingPhotoInfo = uiState.missingPhotoInfo,
                userCommunities = uiState.userCommunities,
                showCommunitySelection = uiState.showCommunitySelection,
                onBackClick = { onBack() },
                onDeleteClick = {
                    viewModel.onEvent(PhotoDetailEvent.DeletePhoto(photo.id))
                },
                onMissingPhotoClick = { missingPhoto ->
                    viewModel.onEvent(
                        PhotoDetailEvent.UpdateMissingPhoto(
                            missingPhoto,
                            photo.uri.toString()
                        )
                    )
                },
                onAddToAllClick = {
                    viewModel.onEvent(
                        PhotoDetailEvent.UpdateAllMissingPhotos(photo.uri.toString())
                    )
                },
                onCreateEventClick = {
                    viewModel.onEvent(PhotoDetailEvent.ShowCommunitySelector)
                },
                onCommunitySelected = { communityId ->
                    viewModel.onEvent(
                        PhotoDetailEvent.CreateEvent(
                            communityId,
                            photo.uri.toString()
                        )
                    )
                },
                onDismissCommunitySelection = {
                    viewModel.onEvent(PhotoDetailEvent.DismissCommunitySelector)
                }
            )
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
