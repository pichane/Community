package com.example.myapp.presentation.photodetail

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.myapp.domain.model.MissingPhotoInfo
import org.koin.androidx.compose.koinViewModel
import kotlin.math.min
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.example.myapp.domain.model.Community


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailScreen(
    photoId: Int,
    viewModel: PhotoDetailViewModel = koinViewModel(),
    onBack: () -> Unit,
    onDelete: () -> Unit
) {
    val photoState by viewModel.photo.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val missingPhotoInfo by viewModel.missingPhotoInfo.collectAsState()
    val userCommunities by viewModel.userCommunities.collectAsState()

    // Bottom sheet state
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    // Load the photo when the screen is first displayed
    LaunchedEffect(photoId) {
        viewModel.loadPhoto(photoId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        photoState?.let { photo ->
            Column(modifier = Modifier.fillMaxSize()) {
                // Photo takes up most of the screen - no top bar
                Box(
                    modifier = Modifier
                        .weight(0.85f)
                        .fillMaxWidth()
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = photo.uri,
                            onLoading = { Log.d("PhotoDetail", "Loading image: ${photo.uri}") },
                            onError = { Log.e("PhotoDetail", "Error loading image: ${photo.uri}", it.result.throwable) }
                        ),
                        contentDescription = "Photo Detail",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Action buttons as floating buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Back button
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Delete button
                        IconButton(
                            onClick = {
                                viewModel.deletePhoto(photo.id)
                                onDelete()
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete"
                            )
                        }
                    }
                }

                // Bottom section - contains either missing photos or create event button
                Surface(
                    modifier = Modifier
                        .weight(0.15f)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        if (missingPhotoInfo.isNotEmpty()) {
                            // Missing photos section
                            Text(
                                text = "Use This Photo For",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            MissingPhotoRow(
                                missingPhotos = missingPhotoInfo,
                                onTakePhotoClick = { selectedMissingPhoto ->
                                    // Use current photo to update the missing photo
                                    viewModel.updateMissingPhotoWithCurrentPhoto(
                                        missingPhotoInfo = selectedMissingPhoto,
                                        currentPhotoUri = photo.uri.toString()
                                    )
                                    // Navigate back
                                    onBack()
                                },
                                onAddToAllClick = {
                                    // Update all missing photos with current photo
                                    viewModel.updateAllMissingPhotos(photo.uri.toString())
                                    // Navigate back
                                    onBack()
                                }
                            )
                        } else {
                            // Create Event Button
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Create New Event",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Button(
                                    onClick = { showBottomSheet = true },
                                    modifier = Modifier.fillMaxWidth(0.8f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text("Create Community Event")
                                }
                            }
                        }
                    }
                }
            }
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }

    // Bottom Sheet for selecting community
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            CommunitySelectionContent(
                communities = userCommunities,
                onCommunitySelected = { community ->
                    viewModel.createNewEventWithPhoto(community.id, photoState?.uri.toString())
                    showBottomSheet = false
                    onBack()
                }
            )
        }
    }
}

@Composable
private fun CommunitySelectionContent(
    communities: List<Community>,
    onCommunitySelected: (Community) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Select a Community",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(communities.size) { index ->
                val community = communities[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCommunitySelected(community) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Community profile picture
                        Image(
                            painter = rememberAsyncImagePainter(model = community.profilePictureUrl),
                            contentDescription = "Community profile",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        // Community name
                        Text(
                            text = community.userName,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )

                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Create event",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Add some padding at the bottom for better UX
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun MissingPhotoRow(
    missingPhotos: List<MissingPhotoInfo>,
    onTakePhotoClick: (MissingPhotoInfo) -> Unit,
    onAddToAllClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        // Now the row of individual community options
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Show max 3 items
            for (i in 0 until min(3, missingPhotos.size)) {
                MissingPhotoItem(
                    name = missingPhotos[i].communityName,
                    modifier = Modifier.weight(1f),
                    onClick = { onTakePhotoClick(missingPhotos[i]) },
                )
            }

            // Fill remaining space if less than 3 items
            repeat(2 - min(2, missingPhotos.size)) {
                Box(modifier = Modifier.weight(1f))
            }
            if (missingPhotos.isNotEmpty()) {
                MissingPhotoItem(
                    name = "Add to all communities",
                    modifier = Modifier.weight(1f),
                    onClick = { onAddToAllClick() },
                    color = MaterialTheme.colorScheme.primaryContainer
                )
            }
        }
    }
}

@Composable
fun MissingPhotoItem(
    name: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = color
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Photo",
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = name,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
