package com.example.myapp.presentation.screen.photodetail

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.myapp.domain.model.Community
import com.example.myapp.domain.model.MissingPhotoInfo
import com.example.myapp.domain.model.Photo
import com.example.myapp.presentation.common.debouncedClickable
import kotlin.math.min

@Composable
fun PhotoDetailContent(
    photo: Photo,
    missingPhotoInfo: List<MissingPhotoInfo>,
    userCommunities: List<Community>,
    showCommunitySelection: Boolean,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onMissingPhotoClick: (MissingPhotoInfo) -> Unit,
    onAddToAllClick: () -> Unit,
    onCreateEventClick: () -> Unit,
    onCommunitySelected: (communityId: String) -> Unit,
    onDismissCommunitySelection: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Photo display section (takes up most of the screen)
            PhotoDisplaySection(
                photoUri = photo.uri.toString(),
                onBackClick = onBackClick,
                onDeleteClick = onDeleteClick,
                modifier = Modifier
                    .weight(0.85f)
                    .fillMaxWidth()
            )

            // Bottom action section
            BottomActionSection(
                missingPhotoInfo = missingPhotoInfo,
                showMissingPhotoSection = missingPhotoInfo.isNotEmpty(),
                onMissingPhotoClick = onMissingPhotoClick,
                onAddToAllClick = onAddToAllClick,
                onCreateEventClick = onCreateEventClick,
                modifier = Modifier
                    .weight(0.15f)
                    .fillMaxWidth()
            )
        }

        // Community selection bottom sheet
        if (showCommunitySelection) {
            CommunitySelectionSheet(
                communities = userCommunities,
                onCommunitySelected = onCommunitySelected,
                onDismiss = onDismissCommunitySelection
            )
        }
    }
}

@Composable
fun PhotoDisplaySection(
    photoUri: String,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // Photo image
        Image(
            painter = rememberAsyncImagePainter(
                model = photoUri,
                onLoading = { Log.d("PhotoDetail", "Loading image: $photoUri") },
                onError = {
                    Log.e(
                        "PhotoDetail",
                        "Error loading image: $photoUri",
                        it.result.throwable
                    )
                }
            ),
            contentDescription = "Photo Detail",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Action buttons overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp)
        ) {
            // Back button (top left)
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(42.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        shape = CircleShape
                    )
                    .padding(10.dp)
                    .debouncedClickable { onBackClick() }
            )

            // Delete button (top right)
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(42.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        shape = CircleShape
                    )
                    .padding(10.dp)
                    .debouncedClickable { onDeleteClick() }
            )
        }
    }
}

@Composable
fun ActionIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(40.dp)
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                shape = CircleShape
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription
        )
    }
}

@Composable
fun BottomActionSection(
    missingPhotoInfo: List<MissingPhotoInfo>,
    showMissingPhotoSection: Boolean,
    onMissingPhotoClick: (MissingPhotoInfo) -> Unit,
    onAddToAllClick: () -> Unit,
    onCreateEventClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            if (showMissingPhotoSection) {
                // Missing photos section
                Text(
                    text = "Use This Photo For",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                MissingPhotoRow(
                    missingPhotos = missingPhotoInfo,
                    onTakePhotoClick = onMissingPhotoClick,
                    onAddToAllClick = onAddToAllClick
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
                        onClick = onCreateEventClick,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunitySelectionSheet(
    communities: List<Community>,
    onCommunitySelected: (communityId: String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        CommunitySelectionContent(
            communities = communities,
            onCommunitySelected = { community ->
                onCommunitySelected(community.id)
            }
        )
    }
}

@Composable
private fun CommunitySelectionContent(
    communities: List<Community>,
    onCommunitySelected: (Community) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = "Select Community",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp, top = 8.dp)
        )

        if (communities.isEmpty()) {
            Text(
                text = "No communities found",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            communities.forEach { community ->
                CommunityItem(
                    community = community,
                    onClick = { onCommunitySelected(community) }
                )
            }

            // Add some padding at the bottom for better UX
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun CommunityItem(
    community: Community,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
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
            AsyncImage(
                model = community.profilePictureUrl,
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

@Composable
fun MissingPhotoRow(
    missingPhotos: List<MissingPhotoInfo>,
    onTakePhotoClick: (MissingPhotoInfo) -> Unit,
    onAddToAllClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Show max 3 items
        for (i in 0 until min(2, missingPhotos.size)) {
            MissingPhotoItem(
                name = missingPhotos[i].communityName,
                modifier = Modifier.weight(1f),
                onClick = { onTakePhotoClick(missingPhotos[i]) }
            )
        }

        // Fill remaining space if less than 2 items
        repeat(2 - min(2, missingPhotos.size)) {
            Box(modifier = Modifier.weight(1f))
        }

        // Add to all button always takes one slot
        MissingPhotoItem(
            name = "Add to all communities",
            modifier = Modifier.weight(1f),
            onClick = onAddToAllClick,
            color = MaterialTheme.colorScheme.primaryContainer
        )
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