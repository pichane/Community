package com.example.myapp.presentation.screen.social

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.example.myapp.domain.model.Community
import com.example.myapp.domain.model.User

@Composable
fun SocialContent(
    uiState: SocialUiState,
    onAddFriend: (String) -> Unit,
    onJoinCommunity: (String) -> Unit,
    contentPadding: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Your Communities section
        if (uiState.userCommunities.isNotEmpty()) {
            item {
                CommunitiesSection(
                    communities = uiState.userCommunities,
                    onCommunityClick = { /* Navigate to community detail */ }
                )
            }
        }

        // Friends section
        if (uiState.friends.isNotEmpty()) {
            item {
                FriendsSection(
                    friends = uiState.friends
                )
            }
        }

        // Discovery section
        item {
            DiscoverySection(
                users = uiState.discoveryUsers,
                communities = uiState.discoveryCommunities,
                onAddFriend = onAddFriend,
                onJoinCommunity = onJoinCommunity
            )
        }
    }
}

@Composable
private fun CommunitiesSection(
    communities: List<Community>,
    onCommunityClick: (Community) -> Unit
) {
    Column {
        SectionTitle("Your Communities")
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(end = 8.dp)
        ) {
            items(communities) { community ->
                CommunityCard(
                    community = community,
                    onClick = { onCommunityClick(community) }
                )
            }
        }
    }
}

@Composable
private fun FriendsSection(
    friends: List<User>
) {
    Column {
        SectionTitle("Friends")
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(end = 8.dp)
        ) {
            items(friends) { friend ->
                FriendItem(friend = friend)
            }
        }
    }
}

@Composable
private fun DiscoverySection(
    users: List<User>,
    communities: List<Community>,
    onAddFriend: (String) -> Unit,
    onJoinCommunity: (String) -> Unit
) {
    Column {
        SectionTitle("Discovery")
        Spacer(modifier = Modifier.height(16.dp))

        // Show empty state if no discovery items
        if (communities.isEmpty() && users.isEmpty()) {
            EmptyDiscoveryState()
            return
        }

        // Communities to discover
        if (communities.isNotEmpty()) {
            DiscoveryCommunitiesSection(
                communities = communities,
                onJoinCommunity = onJoinCommunity
            )
        }

        // Users to discover
        if (users.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            DiscoveryUsersSection(
                users = users,
                onAddFriend = onAddFriend
            )
        }
    }
}

@Composable
private fun DiscoveryCommunitiesSection(
    communities: List<Community>,
    onJoinCommunity: (String) -> Unit
) {
    Column {
        Text(
            text = "Communities you might like",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            communities.take(3).forEach { community ->
                CommunityRow(
                    name = community.userName,
                    profilePicture = community.profilePictureUrl,
                    buttonText = "Join",
                    onButtonClick = { onJoinCommunity(community.id) }
                )
            }
        }
    }
}

@Composable
private fun DiscoveryUsersSection(
    users: List<User>,
    onAddFriend: (String) -> Unit
) {
    Column {
        Text(
            text = "Invitation",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            users.take(5).forEach { user ->
                UserRow(
                    name = user.userName,
                    profilePicture = user.profilePictureUrl,
                    buttonText = "Accept",
                    onButtonClick = { onAddFriend(user.id) }
                )
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun UserRow(
    name: String,
    profilePicture: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
    cardColors: CardColors = CardDefaults.cardColors()
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        colors = cardColors
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile picture
            UserAvatar(
                profilePicture = profilePicture,
                userName = name,
                size = 48.dp
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Name
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Action button
            Button(onClick = onButtonClick) {
                Text(buttonText)
            }
        }
    }
}

@Composable
fun UserAvatar(
    profilePicture: String,
    userName: String,
    size: Dp,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.size(size)) {
        SubcomposeAsyncImage(
            model = profilePicture,
            contentDescription = "Profile picture of $userName",
            modifier = Modifier
                .size(size)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            error = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )
    }
}

@Composable
fun ColorBackground(
    color: Color,
    icon: ImageVector,
    iconTint: Color
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint
        )
    }
}

@Composable
fun CommunityRow(
    name: String,
    profilePicture: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Community profile picture
            AsyncImage(
                model = profilePicture,
                contentDescription = "Community picture of $name",
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Community info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "members",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // Action button
            JoinButton(text = buttonText, onClick = onButtonClick)
        }
    }
}

@Composable
fun JoinButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick) {
        Icon(
            Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text)
    }
}

@Composable
fun CommunityCard(
    community: Community,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Community image
            Box(
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
            ) {
                AsyncImage(
                    model = community.profilePictureUrl,
                    contentDescription = "Community image for ${community.userName}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Community name and member count
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = community.userName,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "members",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun FriendItem(
    friend: User,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.width(80.dp)
    ) {
        // Friend profile picture
        UserAvatar(
            profilePicture = friend.profilePictureUrl,
            userName = friend.userName,
            size = 64.dp
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Friend name
        Text(
            text = friend.userName,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun EmptyDiscoveryState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No suggestions available",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "We'll notify you when we find communities or people you might like",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}