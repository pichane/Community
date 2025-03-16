package com.example.myapp.presentation.screen.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.myapp.R
import com.example.myapp.domain.model.FromUser
import com.example.myapp.domain.model.Photo
import com.example.myapp.presentation.common.debouncedClickable
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onTakePhotoClick: () -> Unit,
    onPhotoClick: (Int) -> Unit,
    onSocialClick: () -> Unit,
    onMemoryClick: (String, String) -> Unit
) {
    val photos by viewModel.photos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_app_icon),
                            contentDescription = "App Logo",
                            modifier = Modifier
                                .size(40.dp)
                                .padding(4.dp)
                        )
                    }
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Social",
                        Modifier.debouncedClickable { onSocialClick() }
                    )
                }
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "Your Photos",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        MyPhotosRow(photos, onPhotoClick, onTakePhotoClick)

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    }

                    // Community section
                    item {
                        Text(
                            text = "My Communities",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                        )


                        Spacer(modifier = Modifier.height(8.dp))

                        // For each community the user has joined
                        viewModel.userCommunities.collectAsState().value.forEach { community ->
                            // Community title
                            Text(
                                text = community.userName,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                            )

                            HorizontalDivider(thickness = 0.5.dp)

                            // Row with memories - limited to 2
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Take only the first 2 memories
                                val memories = community.memories.take(2)

                                // Display each memory (max 2)
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

                                // If we have less than 2 memories, add placeholders to fill the space
                                repeat(2 - memories.size) {
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                    ) {
                                        EmptyPhotoPlaceholder()
                                    }
                                }
                            }

                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
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
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Get the first 3 photos, or fewer if there aren't 3 photos yet
        val photoItems = photos.take(3)
        for (i in 0 until 3) {
            PhotoItem(i, photoItems, onPhotoClick, onTakePhotoClick)
        }
    }
}

@Composable
private fun RowScope.PhotoItem(
    i: Int,
    photoItems: List<Photo>,
    onPhotoClick: (Int) -> Unit,
    onTakePhotoClick: () -> Unit
) {
    Box(modifier = Modifier.weight(1f)) {
        if (i < photoItems.size) {
            // Show an existing photo
            PhotoItem(
                photo = photoItems[i],
                onClick = { onPhotoClick(photoItems[i].id) }
            )
        } else {
            PhotoPlaceholder(
                onClick = onTakePhotoClick
            )
        }
    }
}

@Composable
fun PhotoItem(photo: Photo, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(width = 120.dp, height = 160.dp)
            .debouncedClickable { onClick() }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = photo.uri,
                        onLoading = { Log.d("PhotoItem", "Loading image: ${photo.uri}") },
                        onError = {
                            Log.e(
                                "PhotoItem",
                                "Error loading image: ${photo.uri}",
                                it.result.throwable
                            )
                        }
                    ),
                    contentDescription = "Photo ${photo.id}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                val dateFormat = SimpleDateFormat("MM/dd/yy", Locale.getDefault())
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
                    contentDescription = "Take Photo",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Add Photo",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun PhotoComposition(
    photos: List<FromUser>,
    isHorizontal: Boolean,
    modifier: Modifier = Modifier,
    mainUserId: String = "user_main",
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.debouncedClickable { onClick() }
    ) {
        if (isHorizontal) {
            // Horizontal layout - stacked vertically
            Column(modifier = Modifier.fillMaxSize()) {
                HorizontalDivider(thickness = 1.dp)

                // Display photos from the memory
                photos.take(4).forEachIndexed { index, fromUser ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        when {
                            fromUser.url.isNotBlank() -> {
                                // Normal image display
                                Image(
                                    painter = rememberAsyncImagePainter(model = fromUser.url),
                                    contentDescription = "Community photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            fromUser.userId == mainUserId -> {
                                // Empty photo from main user - show "ADD" button
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
                                            contentDescription = "Add Photo",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "Add Photo",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }

                            else -> {
                                // Empty photo from another user - show "Missing friend"
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Center
                                ) {
                                    Text(
                                        text = "Missing Friend",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                    if (index < 2) { // Don't add divider after last item
                        HorizontalDivider(
                            thickness = 1.dp,
                        )
                    }
                }
            }
        } else {
            // Vertical layout - 2x2 grid
            Column(modifier = Modifier.fillMaxSize()) {
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
                            .width(1.dp)
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

                HorizontalDivider(
                    thickness = 1.dp,
                )

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
                            .width(1.dp)
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
    }
}

@Composable
fun HandlePhotoDisplay(photo: FromUser, mainUserId: String) {
    when {
        photo.url.isNotBlank() -> {
            // Normal image display
            Image(
                painter = rememberAsyncImagePainter(model = photo.url),
                contentDescription = "Community photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        photo.userId == mainUserId -> {
            // Empty photo from main user - show "ADD" button
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
                        contentDescription = "Add Photo",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Add Photo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        else -> {
            // Empty photo from another user - show "Missing friend"
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Center
            ) {
                Text(
                    text = "Missing Friend",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
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
            modifier = Modifier.size(24.dp)
        )
    }
}
