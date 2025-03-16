package com.example.myapp.presentation.screen.social

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.example.myapp.R
import com.example.myapp.domain.model.Community
import com.example.myapp.domain.model.User
import com.example.myapp.presentation.theme.dimensions

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
        contentPadding = PaddingValues(MaterialTheme.dimensions.iconSmall),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.iconMedium)
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
        SectionTitle(stringResource(R.string.your_communities))
        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.spaceSmall))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(end = MaterialTheme.dimensions.spaceSmall)
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
        SectionTitle(stringResource(R.string.friends))
        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.spaceSmall))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.iconSmall),
            contentPadding = PaddingValues(end = MaterialTheme.dimensions.spaceSmall)
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
        SectionTitle(stringResource(R.string.discovery))
        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.iconSmall))

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
            Spacer(modifier = Modifier.height(MaterialTheme.dimensions.iconSmall))
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
            text = stringResource(R.string.communities_you_might_like),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = MaterialTheme.dimensions.spaceSmall)
        )

        Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.spaceSmall)) {
            communities.take(3).forEach { community ->
                CommunityRow(
                    name = community.userName,
                    profilePicture = community.profilePictureUrl,
                    buttonText = stringResource(R.string.join),
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
            text = stringResource(R.string.invitation),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = MaterialTheme.dimensions.spaceSmall)
        )

        Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.spaceSmall)) {
            users.take(5).forEach { user ->
                UserRow(
                    name = user.userName,
                    profilePicture = user.profilePictureUrl,
                    buttonText = stringResource(R.string.accept),
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
    shape: Shape = RoundedCornerShape(MaterialTheme.dimensions.spaceSmall),
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
                size = MaterialTheme.dimensions.iconExtraLargePlus
            )

            Spacer(modifier = Modifier.width(MaterialTheme.dimensions.iconSmall))

            // Name
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.width(MaterialTheme.dimensions.spaceSmall))

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
            contentDescription = stringResource(R.string.profile_picture_of, userName),
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
        shape = RoundedCornerShape(MaterialTheme.dimensions.spaceSmall)
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
                contentDescription = stringResource(R.string.community_picture_of, name),
                modifier = Modifier
                    .size(MaterialTheme.dimensions.avatarSize)
                    .clip(RoundedCornerShape(MaterialTheme.dimensions.spaceSmall)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(MaterialTheme.dimensions.iconSmall))

            // Community info
            CommunityInfo(name)

            JoinButton(text = buttonText, onClick = onButtonClick)
        }
    }
}

@Composable
private fun RowScope.CommunityInfo(name: String) {
    Column(modifier = Modifier.weight(1f)) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = stringResource(R.string.members),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun JoinButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick) {
        Icon(
            Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(MaterialTheme.dimensions.iconSmallPlus)
        )
        Spacer(modifier = Modifier.width(MaterialTheme.dimensions.spaceExtraSmall))
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
        shape = RoundedCornerShape(MaterialTheme.dimensions.iconSmall)
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
                    contentDescription = stringResource(
                        R.string.community_image_for,
                        community.userName
                    ),
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
                    text = stringResource(R.string.members),
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
            size = MaterialTheme.dimensions.iconVeryLarge
        )

        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.spaceExtraSmall))

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
            .padding(vertical = MaterialTheme.dimensions.iconLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(MaterialTheme.dimensions.iconExtraLargePlus),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.iconSmall))

        Text(
            text = stringResource(R.string.no_suggestions_available),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.spaceSmall))

        Text(
            text = "We'll notify you when we find communities or people you might like",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = MaterialTheme.dimensions.iconLarge),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}