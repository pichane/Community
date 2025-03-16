package com.example.myapp.presentation.memory

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myapp.domain.model.FromUser
import com.example.myapp.domain.model.Memory
import com.example.myapp.presentation.home.HandlePhotoDisplay
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoCompositionScreen(
    communityId: String,
    memoryId: String,
    viewModel: PhotoCompositionViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val communityName by viewModel.communityName.collectAsState()

    // Load the memory when the screen is first displayed
    LaunchedEffect(communityId, memoryId) {
        viewModel.loadMemory(communityId, memoryId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = when (val state = uiState) {
                            is MemoryUiState.Success -> state.memory.title
                            else -> ""
                        },
                        color = Color.White
                    )
                },
                navigationIcon = {
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
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Display different UI based on the state
            when (val state = uiState) {
                MemoryUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is MemoryUiState.Success -> {
                    // Community name at the top
                    Text(
                        text = communityName,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(16.dp)
                            .background(
                                color = Color.Black.copy(alpha = 0.5f),
                                shape = MaterialTheme.shapes.small
                            )
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    )

                    // Display the memory in the same layout as in HomeScreen but full screen
                    FullScreenPhotoComposition(
                        photos = state.memory.photos,
                        isHorizontal = state.memory.isHorizontal,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is MemoryUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.error
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FullScreenPhotoComposition(
    photos: List<FromUser>,
    isHorizontal: Boolean,
    modifier: Modifier = Modifier,
    mainUserId: String = "user_main"
) {
    Box(modifier = modifier) {
        if (isHorizontal) {
            // Horizontal layout - stacked vertically
            Column(modifier = Modifier.fillMaxSize()) {
                // Display photos from the memory
                photos.take(4).forEachIndexed { index, fromUser ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        HandlePhotoDisplay(photo = fromUser, mainUserId = mainUserId)
                        
                        if (index < photos.size - 1) {
                            HorizontalDivider(
                                thickness = 1.dp, 
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.align(Alignment.BottomCenter)
                            )
                        }
                    }
                }
            }
        } else {
            // Vertical layout - 2x2 grid
            Column(modifier = Modifier.fillMaxSize()) {
                Row(modifier = Modifier.weight(1f)) {
                    // Top left photo
                    Box(modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                    ) {
                        if (photos.isNotEmpty()) {
                            HandlePhotoDisplay(photos[0], mainUserId)
                        }
                    }

                    Divider(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                    )

                    // Top right photo
                    Box(modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                    ) {
                        if (photos.size > 1) {
                            HandlePhotoDisplay(photos[1], mainUserId)
                        }
                    }
                }

                Divider()

                Row(modifier = Modifier.weight(1f)) {
                    // Bottom left photo
                    Box(modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                    ) {
                        if (photos.size > 2) {
                            HandlePhotoDisplay(photos[2], mainUserId)
                        }
                    }
                        
                    Divider(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                    )

                    if (photos.size > 3) {
                        // Bottom right photo
                        Box(modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                        ) {
                            if (photos.size > 3) {
                                HandlePhotoDisplay(photos[3], mainUserId)
                            }
                        }
                    }
                }
            }
        }
    }
}
