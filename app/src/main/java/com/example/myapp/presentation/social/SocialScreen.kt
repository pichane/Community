package com.example.myapp.presentation.social

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.myapp.domain.model.User
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SocialScreen(
    viewModel: SocialViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val discoveryUsers by viewModel.discoveryUsers.collectAsState()
    val userCommunities by viewModel.userCommunities.collectAsState()
    val discoveryCommunities by viewModel.discoveryCommunities.collectAsState()
    val friends by viewModel.friends.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Social") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Discovery section
            item {
                SectionTitle("Discovery")
                Spacer(modifier = Modifier.height(8.dp))

                // Vertical list of discovery communities
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    discoveryCommunities.forEach { community ->
                        UserRow(
                            name = community.userName,
                            profilePicture = community.profilePictureUrl,
                            userId = community.id,
                            buttonText = "Join",
                            onButtonClick = { viewModel.joinCommunity(community.id) },
                            shape = RectangleShape,
                            cardColors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )
                    }
                }
            }

            // User's Communities Section
            item {
                SectionTitle("My Communities")
                Spacer(modifier = Modifier.height(8.dp))

                if (userCommunities.isEmpty()) {
                    Text(
                        text = "You haven't joined any communities yet",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    // Horizontal row of user's communities
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(userCommunities) { community ->
                            CommunityProfilePicture(community.userName, community.profilePictureUrl)
                        }
                    }
                }
            }

            // Friends Section
            item {
                SectionTitle("All Your Friends")
                Spacer(modifier = Modifier.height(8.dp))

                // Horizontal row of friend profile pictures
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(friends) { friend ->
                        FriendProfilePicture(user = friend)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (discoveryUsers.isEmpty()) {
                    Text(
                        text = "No more invitation!",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    // Use Column with AnimatedVisibility for each item
                    Column(
                        modifier = Modifier.animateContentSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        discoveryUsers.forEach { user ->
                            UserRow(
                                name = user.userName,
                                profilePicture = user.profilePictureUrl,
                                userId = user.id,
                                buttonText = "Accept Invitation",
                                onButtonClick = { viewModel.addFriend(user.id) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium
    )
}

@Composable
fun UserRow(
    name: String,
    profilePicture: String,
    userId: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = CircleShape,
    cardColors: CardColors = CardDefaults.cardColors() // Default card colors
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = cardColors
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Picture - applies custom shape here
            Image(
                painter = rememberAsyncImagePainter(model = profilePicture),
                contentDescription = "Profile picture of ${name}",
                modifier = Modifier
                    .size(60.dp)
                    .clip(shape),
                contentScale = ContentScale.Crop
            )

            // User Info
            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .weight(1f)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "ID: ${userId}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Action Button
            Button(onClick = onButtonClick) {
                Text(buttonText)
            }
        }
    }
}

@Composable
fun CommunityProfilePicture(name: String, profilePicture: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = profilePicture),
            contentDescription = "Profile picture of ${name}",
            modifier = Modifier
                .size(70.dp)
                .clip(RectangleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1
        )
    }
}

@Composable
fun FriendProfilePicture(user: User) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = user.profilePictureUrl),
            contentDescription = "Profile picture of ${user.userName}",
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = user.userName,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1
        )
    }
}
