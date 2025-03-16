package com.example.myapp.presentation.event

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.myapp.domain.model.Community
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCommunityEventScreen(
    viewModel: NewCommunityEventViewModel = koinViewModel(),
    onBack: () -> Unit,
    onEventCreated: () -> Unit
) {
    val userCommunities by viewModel.userCommunities.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val currentDate = remember { Date() }
    val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
    val formattedDate = remember { dateFormat.format(currentDate) }

    // Navigate back on success
    LaunchedEffect(uiState.isSuccessful) {
        if (uiState.isSuccessful) {
            onEventCreated()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Event") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Event Title Input
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = { viewModel.updateTitle(it) },
                    label = { Text("Event Title") },
                    placeholder = { Text("Enter event title") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.isTitleError,
                    supportingText = {
                        if (uiState.isTitleError) {
                            Text("Title is required", color = MaterialTheme.colorScheme.error)
                        }
                    }
                )

                // Date Display
                Text(
                    text = "Date: $formattedDate",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Divider
                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Instructions
                Text(
                    text = "Select a community to add this event",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Error message if any
                uiState.errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Communities List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(userCommunities.size) { index ->
                        val community = userCommunities[index]
                        CommunityItem(
                            community = community,
                            onClick = {
                                viewModel.createNewMemory(community.id)
                            }
                        )
                    }
                }
            }

            // Loading indicator
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun CommunityItem(community: Community, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
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
        }
    }
}