package com.example.myapp.presentation.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.myapp.R
import com.example.myapp.domain.model.Community
import com.example.myapp.domain.model.FromUser
import com.example.myapp.domain.model.Photo
import com.example.myapp.presentation.common.debouncedClickable
import com.example.myapp.presentation.theme.dimensions
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeContent(
    uiState: HomeUiState,
    onTakePhotoClick: () -> Unit,
    onPhotoClick: (Int) -> Unit,
    onMemoryClick: (String, String) -> Unit,
    paddingValues: PaddingValues
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        when {
            uiState.isLoading && uiState.photos.isEmpty() -> {
                LoadingIndicator()
            }

            uiState.photos.isEmpty() -> {
                EmptyHomeContent(onTakePhotoClick)
            }

            else -> {
                HomeDataContent(
                    photos = uiState.photos,
                    communities = uiState.communities,
                    onPhotoClick = onPhotoClick,
                    onTakePhotoClick = onTakePhotoClick,
                    onMemoryClick = onMemoryClick
                )
            }
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyHomeContent(onTakePhotoClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.no_photos_yet),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.iconSmall))

        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(R.string.take_photo),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(MaterialTheme.dimensions.iconVeryLarge)
                .debouncedClickable { onTakePhotoClick() }
        )

        Text(
            text = stringResource(R.string.take_your_first_photo),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = MaterialTheme.dimensions.iconSmall)
        )
    }
}

@Composable
private fun HomeDataContent(
    photos: List<Photo>,
    communities: List<Community>,
    onPhotoClick: (Int) -> Unit,
    onTakePhotoClick: () -> Unit,
    onMemoryClick: (String, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(MaterialTheme.dimensions.iconSmall),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.iconSmall)
    ) {
        item {
            PhotosSection(
                photos = photos,
                onPhotoClick = onPhotoClick,
                onTakePhotoClick = onTakePhotoClick
            )
        }

        if (communities.isNotEmpty()) {
            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = MaterialTheme.dimensions.spaceSmall))

                Text(
                    text = stringResource(R.string.my_communities),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(top = MaterialTheme.dimensions.spaceSmall, bottom = MaterialTheme.dimensions.iconSmall)
                )
            }

            items(communities) { community ->
                CommunitiesSection(
                    community = community,
                    onMemoryClick = onMemoryClick
                )
            }
        }
    }
}

@Composable
private fun PhotosSection(
    photos: List<Photo>,
    onPhotoClick: (Int) -> Unit,
    onTakePhotoClick: () -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.your_photos),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = MaterialTheme.dimensions.spaceSmall)
        )

        MyPhotosRow(
            photos = photos,
            onPhotoClick = onPhotoClick,
            onTakePhotoClick = onTakePhotoClick
        )
    }
}

@Composable
fun MyPhotosRow(
    photos: List<Photo>,
    onPhotoClick: (Int) -> Unit,
    onTakePhotoClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.dimensions.spaceSmall),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.spaceSmall)
    ) {
        // Get the first 3 photos, or fewer if there aren't 3 photos yet
        val photoItems = photos.take(3)
        for (i in 0 until 3) {
            if (i < photoItems.size) {
                PhotoItem(
                    photo = photoItems[i],
                    onClick = { onPhotoClick(photoItems[i].id) }
                )
            } else {
                PhotoPlaceholder(onClick = onTakePhotoClick)
            }
        }
    }
}

@Composable
fun PhotoItem(
    photo: Photo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(width = 120.dp, height = 160.dp)
            .debouncedClickable { onClick() }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = photo.uri,
                    onError = {
                        // Log error but continue
                    }
                ),
                contentDescription = stringResource(R.string.photo, photo.id),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier.padding(MaterialTheme.dimensions.spaceSmall)
            ) {
                val dateFormat = SimpleDateFormat(stringResource(R.string.format_mm_dd_yy), Locale.getDefault())
                val date = Date(photo.timestamp)
                Text(
                    text = dateFormat.format(date),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun PhotoPlaceholder(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(width = 120.dp, height = 160.dp)
            .debouncedClickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Center
        ) {
            Column(
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_photo),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(MaterialTheme.dimensions.iconLarge)
                )

                Spacer(modifier = Modifier.height(MaterialTheme.dimensions.spaceSmall))

                Text(
                    text = stringResource(R.string.add_photo),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CommunitiesSection(
    community: Community,
    onMemoryClick: (String, String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = community.userName,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = MaterialTheme.dimensions.iconSmall, bottom = MaterialTheme.dimensions.spaceSmall)
        )

        HorizontalDivider(thickness = 0.5.dp)

        RowWithMemories(community, onMemoryClick)
    }
}

@Composable
private fun RowWithMemories(
    community: Community,
    onMemoryClick: (String, String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(vertical = MaterialTheme.dimensions.spaceSmall),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.spaceSmall)
    ) {
        val memories = community.memories.take(2)
        memories.forEach { memory ->
            PhotoComposition(
                photos = memory.photos,
                isHorizontal = memory.isHorizontal,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                onClick = {
                    onMemoryClick(community.id, memory.id)
                }
            )
        }

        // Add placeholders if needed
        repeat(2 - memories.size) {
            EmptyMemoryPlaceholder(Modifier.weight(1f))
        }
    }
}

@Composable
fun PhotoComposition(
    photos: List<FromUser>,
    isHorizontal: Boolean,
    modifier: Modifier = Modifier,
    mainUserId: String = stringResource(R.string.user_main),
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.debouncedClickable { onClick() }
    ) {
        if (isHorizontal) {
            HorizontalPhotoComposition(photos, mainUserId)
        } else {
            VerticalPhotoComposition(photos, mainUserId)
        }
    }
}

@Composable
private fun HorizontalPhotoComposition(
    photos: List<FromUser>,
    mainUserId: String
) {
    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalDivider(thickness = MaterialTheme.dimensions.dividerThickness)

        photos.take(4).forEachIndexed { index, fromUser ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when {
                    fromUser.url.isNotBlank() -> {
                        Image(
                            painter = rememberAsyncImagePainter(model = fromUser.url),
                            contentDescription = stringResource(R.string.community_photo),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    fromUser.userId == mainUserId -> {
                        ShowAddButton()
                    }

                    else -> {
                        ShowMissingFriend()
                    }
                }
            }
            if (index < 2) {
                HorizontalDivider(
                    thickness = MaterialTheme.dimensions.dividerThickness,
                )
            }
        }
    }
}

@Composable
private fun ShowAddButton() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Center
    ) {
        Column(
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.add_photo),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.add_photo),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun VerticalPhotoComposition(
    photos: List<FromUser>,
    mainUserId: String
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Top row
        Row(modifier = Modifier.weight(1f)) {
            // Top left photo
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                if (photos.isNotEmpty()) {
                    HandlePhotoDisplay(photos[0], mainUserId)
                } else {
                    EmptyPhotoPlaceholder()
                }
            }

            VerticalDivider(
                modifier = Modifier
                    .width(MaterialTheme.dimensions.dividerThickness)
                    .fillMaxHeight()
            )

            // Top right photo
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                if (photos.size > 1) {
                    HandlePhotoDisplay(photos[1], mainUserId)
                } else {
                    EmptyPhotoPlaceholder()
                }
            }
        }

        HorizontalDivider(thickness = MaterialTheme.dimensions.dividerThickness)

        // Bottom row
        Row(modifier = Modifier.weight(1f)) {
            // Bottom left photo
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                if (photos.size > 2) {
                    HandlePhotoDisplay(photos[2], mainUserId)
                } else {
                    EmptyPhotoPlaceholder()
                }
            }

            VerticalDivider(
                modifier = Modifier
                    .width(MaterialTheme.dimensions.dividerThickness)
                    .fillMaxHeight()
            )

            if (photos.size > 3) {
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

@Composable
fun HandlePhotoDisplay(photo: FromUser, mainUserId: String) {
    when {
        photo.url.isNotBlank() -> {
            // Normal image display
            Image(
                painter = rememberAsyncImagePainter(model = photo.url),
                contentDescription = stringResource(R.string.community_photo),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        photo.userId == mainUserId -> {
            ShowAddButton()
        }

        else -> {
            ShowMissingFriend()
        }
    }
}

@Composable
private fun ShowMissingFriend() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Center
    ) {
        Text(
            text = stringResource(R.string.missing_friend),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EmptyPhotoPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(MaterialTheme.dimensions.iconMedium)
        )
    }
}

@Composable
private fun EmptyMemoryPlaceholder(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxHeight(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Center
        ) {
            Column(
                horizontalAlignment = CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.create_memory),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(MaterialTheme.dimensions.iconLarge)
                )

                Spacer(modifier = Modifier.height(MaterialTheme.dimensions.spaceSmall))

                Text(
                    text = stringResource(R.string.create_memory),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}