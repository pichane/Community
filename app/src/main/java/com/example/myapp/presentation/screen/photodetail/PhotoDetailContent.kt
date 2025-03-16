package com.example.myapp.presentation.screen.photodetail

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.myapp.R
import com.example.myapp.domain.model.Community
import com.example.myapp.domain.model.MissingPhotoInfo
import com.example.myapp.domain.model.Photo
import com.example.myapp.presentation.common.debouncedClickable
import com.example.myapp.presentation.theme.dimensions
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
    var hasMissingInfo: Boolean by remember { mutableStateOf(missingPhotoInfo.isNotEmpty()) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            PhotoDisplaySection(
                photoUri = photo.uri.toString(),
                onBackClick = onBackClick,
                onDeleteClick = onDeleteClick,
                modifier = Modifier
                    .weight(0.85f)
                    .fillMaxWidth()
            )

            BottomActionSection(
                missingPhotoInfo = missingPhotoInfo,
                showMissingPhotoSection = hasMissingInfo,
                onMissingPhotoClick = onMissingPhotoClick,
                onAddToAllClick = onAddToAllClick,
                onCreateEventClick = onCreateEventClick,
                modifier = Modifier
                    .weight(0.15f)
                    .fillMaxWidth()
            )
        }

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
        PhotoImage(photoUri)

        // Action buttons overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(MaterialTheme.dimensions.iconMediumPlus)
        ) {
            BackButton(onBackClick)
            DeleteButton(onDeleteClick)
        }
    }
}

@Composable
private fun PhotoImage(photoUri: String) {
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
        contentDescription = stringResource(R.string.photo_detail),
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun BoxScope.BackButton(onBackClick: () -> Unit) {
    Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = stringResource(R.string.back),
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
}

@Composable
private fun BoxScope.DeleteButton(onDeleteClick: () -> Unit) {
    Icon(
        imageVector = Icons.Default.Delete,
        contentDescription = stringResource(R.string.delete),
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
        Column(modifier = Modifier.padding(horizontal = MaterialTheme.dimensions.iconSmall, vertical = MaterialTheme.dimensions.spaceSmall)) {
            if (showMissingPhotoSection) {
                MissingPhotosSection(missingPhotoInfo, onMissingPhotoClick, onAddToAllClick)
            } else {
                CreateEventButton(onCreateEventClick)
            }
        }
    }
}

@Composable
private fun CreateEventButton(onCreateEventClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.create_new_event),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = MaterialTheme.dimensions.spaceSmall)
        )

        Button(
            onClick = onCreateEventClick,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.padding(end = MaterialTheme.dimensions.spaceSmall)
            )
            Text(stringResource(R.string.create_community_event))
        }
    }
}

@Composable
private fun MissingPhotosSection(
    missingPhotoInfo: List<MissingPhotoInfo>,
    onMissingPhotoClick: (MissingPhotoInfo) -> Unit,
    onAddToAllClick: () -> Unit
) {
    Text(
        text = stringResource(R.string.use_this_photo_for),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = MaterialTheme.dimensions.spaceSmall)
    )

    MissingPhotoRow(
        missingPhotos = missingPhotoInfo,
        onTakePhotoClick = onMissingPhotoClick,
        onAddToAllClick = onAddToAllClick
    )
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
    Column(modifier = Modifier.padding(horizontal = MaterialTheme.dimensions.iconSmall, vertical = MaterialTheme.dimensions.spaceSmall)) {
        Text(
            text = stringResource(R.string.select_community),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = MaterialTheme.dimensions.iconSmall, top = MaterialTheme.dimensions.spaceSmall)
        )

        if (communities.isEmpty()) {
            Text(
                text = stringResource(R.string.no_communities_found),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = MaterialTheme.dimensions.iconSmall)
            )
        } else {
            communities.forEach { community ->
                CommunityItem(
                    community = community,
                    onClick = { onCommunitySelected(community) }
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.dimensions.iconLarge))
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
            .padding(vertical = MaterialTheme.dimensions.spaceExtraSmall)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.dimensions.iconSmall),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.iconSmall)
        ) {
            AsyncImage(
                model = community.profilePictureUrl,
                contentDescription = stringResource(R.string.community_profile),
                modifier = Modifier
                    .size(MaterialTheme.dimensions.iconExtraLargePlus)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Text(
                text = community.userName,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.create_event),
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
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.spaceSmall)
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
            name = stringResource(R.string.add_to_all_communities),
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
                .padding(MaterialTheme.dimensions.spaceSmall),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.add_photo),
                modifier = Modifier.size(MaterialTheme.dimensions.iconMedium)
            )

            Spacer(modifier = Modifier.height(MaterialTheme.dimensions.spaceExtraSmall))

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
