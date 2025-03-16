package com.example.myapp.data.repository

import com.example.myapp.domain.model.Community
import com.example.myapp.domain.model.FromUser
import com.example.myapp.domain.model.Memory
import com.example.myapp.domain.model.MissingPhotoInfo
import com.example.myapp.domain.model.User
import com.example.myapp.domain.repository.SocialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class SocialRepositoryImpl : SocialRepository {

    // Add this property to store the selected missing photo
    private var selectedMissingPhoto: MissingPhotoInfo? = null

    private val discoveryUsers = MutableStateFlow(
        listOf(
            User("user0", "John Smith", "https://randomuser.me/api/portraits/men/1.jpg"),
            User("user2", "Sarah Johnson", "https://randomuser.me/api/portraits/women/2.jpg"),
            User("user3", "Michael Brown", "https://randomuser.me/api/portraits/men/3.jpg"),
            User("user4", "Emma Davis", "https://randomuser.me/api/portraits/women/4.jpg"),
            User("user5", "James Wilson", "https://randomuser.me/api/portraits/men/5.jpg")
        )
    )

    private val userCommunities = MutableStateFlow(
        listOf(
            Community(
                id = "community1",
                userName = "Photography Club",
                profilePictureUrl = "https://randomuser.me/api/portraits/men/10.jpg",
                isCommunityMember = true,
                memories = listOf(
                    Memory(
                        id = "memory1",
                        communityId = "community1",
                        title = "Nature Trip",
                        isHorizontal = false,
                        photos = listOf(
                            FromUser(
                                id = "photo1",
                                userId = "friend3", // Main user's photo
                                url = "https://picsum.photos/seed/photo1/900/900"
                            ),
                            FromUser(
                                id = "photo2",
                                userId = "friend1",
                                url = "https://picsum.photos/seed/photo2/900/900"
                            ),
                            FromUser(
                                id = "photo3",
                                userId = "user_main", // Main user's photo but empty
                                url = ""
                            ),
                            FromUser(
                                id = "photo4",
                                userId = "friend2", // Friend's photo but empty
                                url = ""
                            )
                        )
                    ),
                    Memory(
                        id = "memory2",
                        communityId = "community1",
                        title = "Wildlife",
                        isHorizontal = true,
                        photos = listOf(
                            FromUser(
                                id = "photo5",
                                userId = "user_main",
                                url = "https://picsum.photos/seed/photo5/900/600"
                            ),
                            FromUser(
                                id = "photo6",
                                userId = "friend2",
                                url = "https://picsum.photos/seed/photo6/900/600"
                            ),
                            FromUser(
                                id = "photo7",
                                userId = "friend1",
                                url = "https://picsum.photos/seed/photo7/900/600"
                            ),
                            FromUser(
                                id = "photo7",
                                userId = "friend1",
                                url = "https://picsum.photos/seed/photo8/900/600",
                            )
                        )
                    )
                )
            ),
            Community(
                id = "community2",
                userName = "Hiking Group",
                profilePictureUrl = "https://randomuser.me/api/portraits/women/11.jpg",
                isCommunityMember = true,
                memories = listOf(
                    Memory(
                        id = "memory3",
                        communityId = "community2",
                        title = "Mountain Trip",
                        isHorizontal = true,
                        photos = listOf(
                            FromUser(
                                id = "photo8",
                                userId = "friend10",
                                url = "https://picsum.photos/seed/hike1/900/900"
                            ),
                            FromUser(
                                id = "photo9",
                                userId = "user_main",
                                url = ""
                            ),
                            FromUser(
                                id = "photo10",
                                userId = "friend1",
                                url = "https://picsum.photos/seed/hike3/900/900"
                            ),
                            FromUser(
                                id = "photo11",
                                userId = "friend2",
                                url = ""  // Empty photo from friend2
                            )
                        )
                    ),
                    Memory(
                        id = "memory4",
                        communityId = "community2",
                        title = "Forest Walk",
                        isHorizontal = false,
                        photos = listOf(
                            FromUser(
                                id = "photo12",
                                userId = "friend99",
                                url = "https://picsum.photos/seed/hike5/900/600"
                            ),
                            FromUser(
                                id = "photo13",
                                userId = "friend3",
                                url = "https://picsum.photos/seed/hike7/900/600",
                            ),
                            FromUser(
                                id = "photo14",
                                userId = "user_main",
                                url = "https://picsum.photos/seed/hike8/900/600"
                            )
                        )
                    )
                )
            )
        )
    )

    private val discoveryCommunities = MutableStateFlow(
        listOf(
            Community(
                "community3", "Book Club", "https://randomuser.me/api/portraits/men/12.jpg",
                memories = listOf(
                    Memory(
                        id = "memory3",
                        communityId = "community2",
                        title = "Mountain Trip",
                        isHorizontal = false,
                        photos = listOf(
                            FromUser(
                                id = "photo8",
                                userId = "user_main",
                                url = "https://picsum.photos/seed/hike1/900/900"
                            ),
                            FromUser(
                                id = "photo9",
                                userId = "friend3",
                                url = "https://picsum.photos/seed/hike2/900/900"
                            ),
                            FromUser(
                                id = "photo10",
                                userId = "friend1",
                                url = "https://picsum.photos/seed/hike3/900/900"
                            ),
                            FromUser(
                                id = "photo11",
                                userId = "friend2",
                                url = ""  // Empty photo from friend2
                            )
                        )
                    ),
                    Memory(
                        id = "memory4",
                        communityId = "community2",
                        title = "Forest Walk",
                        isHorizontal = true,
                        photos = listOf(
                            FromUser(
                                id = "photo8",
                                userId = "user_main",
                                url = "https://picsum.photos/seed/hike1/900/900"
                            ),
                            FromUser(
                                id = "photo9",
                                userId = "friend3",
                                url = "https://picsum.photos/seed/hike2/900/900"
                            ),
                            FromUser(
                                id = "photo10",
                                userId = "friend1",
                                url = "https://picsum.photos/seed/hike3/900/900"
                            ),
                            FromUser(
                                id = "photo11",
                                userId = "friend2",
                                url = ""  // Empty photo from friend2
                            )
                        )
                    )
                )
            ),
            Community(
                "community4", "Cooking Class", "https://randomuser.me/api/portraits/women/13.jpg",
                memories = listOf(
                    Memory(
                        id = "memory3",
                        communityId = "community2",
                        title = "Mountain Trip",
                        isHorizontal = false,
                        photos = listOf(
                            FromUser(
                                id = "photo8",
                                userId = "user_main",
                                url = "https://picsum.photos/seed/hike1/900/900"
                            ),
                            FromUser(
                                id = "photo9",
                                userId = "friend3",
                                url = "https://picsum.photos/seed/hike2/900/900"
                            ),
                            FromUser(
                                id = "photo10",
                                userId = "friend1",
                                url = "https://picsum.photos/seed/hike3/900/900"
                            ),
                            FromUser(
                                id = "photo11",
                                userId = "friend2",
                                url = ""  // Empty photo from friend2
                            )
                        )
                    ),
                    Memory(
                        id = "memory4",
                        communityId = "community2",
                        title = "Forest Walk",
                        isHorizontal = true,
                        photos = listOf(
                            FromUser(
                                id = "photo8",
                                userId = "user_main",
                                url = "https://picsum.photos/seed/hike1/900/900"
                            ),
                            FromUser(
                                id = "photo9",
                                userId = "friend3",
                                url = "https://picsum.photos/seed/hike2/900/900"
                            ),
                            FromUser(
                                id = "photo10",
                                userId = "friend1",
                                url = "https://picsum.photos/seed/hike3/900/900"
                            ),
                            FromUser(
                                id = "photo11",
                                userId = "friend2",
                                url = ""  // Empty photo from friend2
                            )
                        )
                    )
                )
            ),
            Community(
                "community5", "Tech Community", "https://randomuser.me/api/portraits/men/14.jpg",
                memories = listOf(
                    Memory(
                        id = "memory3",
                        communityId = "community2",
                        title = "Mountain Trip",
                        isHorizontal = false,
                        photos = listOf(
                            FromUser(
                                id = "photo8",
                                userId = "user_main",
                                url = "https://picsum.photos/seed/hike1/900/900"
                            ),
                            FromUser(
                                id = "photo9",
                                userId = "friend3",
                                url = "https://picsum.photos/seed/hike2/900/900"
                            ),
                            FromUser(
                                id = "photo10",
                                userId = "friend1",
                                url = "https://picsum.photos/seed/hike3/900/900"
                            ),
                            FromUser(
                                id = "photo11",
                                userId = "friend2",
                                url = ""  // Empty photo from friend2
                            )
                        )
                    ),
                    Memory(
                        id = "memory4",
                        communityId = "community2",
                        title = "Forest Walk",
                        isHorizontal = true,
                        photos = listOf(
                            FromUser(
                                id = "photo8",
                                userId = "user_main",
                                url = "https://picsum.photos/seed/hike1/900/900"
                            ),
                            FromUser(
                                id = "photo9",
                                userId = "friend3",
                                url = "https://picsum.photos/seed/hike2/900/900"
                            ),
                            FromUser(
                                id = "photo10",
                                userId = "friend1",
                                url = "https://picsum.photos/seed/hike3/900/900"
                            ),
                            FromUser(
                                id = "photo11",
                                userId = "friend2",
                                url = ""  // Empty photo from friend2
                            )
                        )
                    )
                )
            ),
            Community(
                "community6", "Art Group", "https://randomuser.me/api/portraits/women/15.jpg",
                memories = listOf(
                    Memory(
                        id = "memory3",
                        communityId = "community2",
                        title = "Mountain Trip",
                        isHorizontal = false,
                        photos = listOf(
                            FromUser(
                                id = "photo8",
                                userId = "user_main",
                                url = "https://picsum.photos/seed/hike1/900/900"
                            ),
                            FromUser(
                                id = "photo9",
                                userId = "friend3",
                                url = "https://picsum.photos/seed/hike2/900/900"
                            ),
                            FromUser(
                                id = "photo10",
                                userId = "friend1",
                                url = "https://picsum.photos/seed/hike3/900/900"
                            ),
                            FromUser(
                                id = "photo11",
                                userId = "friend2",
                                url = ""  // Empty photo from friend2
                            )
                        )
                    ),
                    Memory(
                        id = "memory4",
                        communityId = "community2",
                        title = "Forest Walk",
                        isHorizontal = true,
                        photos = listOf(
                            FromUser(
                                id = "photo8",
                                userId = "user_main",
                                url = "https://picsum.photos/seed/hike1/900/900"
                            ),
                            FromUser(
                                id = "photo9",
                                userId = "friend3",
                                url = "https://picsum.photos/seed/hike2/900/900"
                            ),
                            FromUser(
                                id = "photo10",
                                userId = "friend1",
                                url = "https://picsum.photos/seed/hike3/900/900"
                            ),
                            FromUser(
                                id = "photo11",
                                userId = "friend2",
                                url = ""  // Empty photo from friend2
                            )
                        )
                    )
                )
            )
        )
    )

    private val friendUsers = MutableStateFlow(
        listOf(
            User("friend1", "David Miller", "https://randomuser.me/api/portraits/men/21.jpg", true),
            User(
                "friend2",
                "Jennifer Taylor",
                "https://randomuser.me/api/portraits/women/22.jpg",
                true
            ),
            User(
                "friend3",
                "Robert Anderson",
                "https://randomuser.me/api/portraits/men/23.jpg",
                true
            )
        )
    )

    override fun getDiscoveryUsers(): Flow<List<User>> = discoveryUsers

    override fun getUserCommunities(): Flow<List<Community>> = userCommunities

    override fun getDiscoveryCommunities(): Flow<List<Community>> = discoveryCommunities

    override fun getFriends(): Flow<List<User>> = friendUsers

    override suspend fun addFriend(userId: String) {
        // Find and remove user from discovery list and add to friends
        val userToAdd = discoveryUsers.value.find { it.id == userId }

        userToAdd?.let { user ->
            // Add to friends list with isFriend flag
            val updatedUser = user.copy(isFriend = true)
            friendUsers.update { currentFriends -> currentFriends + updatedUser }

            // Remove from discovery list
            discoveryUsers.update { currentList ->
                currentList.filter { it.id != userId }
            }
        }
    }

    override suspend fun removeFriend(userId: String) {
        // Remove from friends list
        friendUsers.update { currentList ->
            currentList.filter { it.id != userId }
        }
    }

    override suspend fun joinCommunity(communityId: String) {
        val communityToJoin = discoveryCommunities.value.find { it.id == communityId }

        communityToJoin?.let { community ->
            // Update with membership flag and add to user communities
            val updatedCommunity = community.copy(isCommunityMember = true)
            userCommunities.update { current -> current + updatedCommunity }

            // Remove from discovery communities
            discoveryCommunities.update { current ->
                current.filter { it.id != communityId }
            }
        }
    }

    override suspend fun leaveCommunity(communityId: String) {
        val communityToLeave = userCommunities.value.find { it.id == communityId }

        communityToLeave?.let { community ->
            // Remove membership flag and move back to discovery
            val updatedCommunity = community.copy(isCommunityMember = false)
            discoveryCommunities.update { current -> current + updatedCommunity }

            // Remove from user communities
            userCommunities.update { current ->
                current.filter { it.id != communityId }
            }
        }
    }


    override suspend fun selectMissingPhotoForCapture(missingPhotoInfo: MissingPhotoInfo) {
        selectedMissingPhoto = missingPhotoInfo
    }

    override suspend fun getSelectedMissingPhoto(): MissingPhotoInfo? {
        return selectedMissingPhoto
    }

    override suspend fun clearPhotoSelection() {
        selectedMissingPhoto = null
    }

    override suspend fun updateMemoryPhoto(
        communityId: String,
        memoryId: String,
        photoId: String,
        newPhotoUrl: String
    ) {
        // Find the community with the given ID
        val communityIndex = userCommunities.value.indexOfFirst { it.id == communityId }
        if (communityIndex == -1) return

        // Get all communities
        val communities = userCommunities.value.toMutableList()

        // Get the community we need to update
        val community = communities[communityIndex]

        // Find the memory with the given ID
        val memoryIndex = community.memories.indexOfFirst { it.id == memoryId }
        if (memoryIndex == -1) return

        // Get the memory we need to update
        val memory = community.memories[memoryIndex]

        // Find the photo with the given ID
        val photoIndex = memory.photos.indexOfFirst { it.id == photoId }
        if (photoIndex == -1) return

        // Create updated versions of objects (immutable approach)
        val updatedFromUser = memory.photos[photoIndex].copy(url = newPhotoUrl)

        // Create new photos list with updated FromUser
        val updatedPhotos = memory.photos.toMutableList().apply {
            set(photoIndex, updatedFromUser)
        }

        // Create new memory with updated photos
        val updatedMemory = memory.copy(photos = updatedPhotos)

        // Create new memories list with updated memory
        val updatedMemories = community.memories.toMutableList().apply {
            set(memoryIndex, updatedMemory)

            // Also clean up the first memory's photos if it exists
            if (size > 1 && this[1].photos.isNotEmpty()) {
                // Filter out any FromUser with empty URLs
                val filteredPhotos = this[1].photos.filter { fromUser ->
                    fromUser.url.isNotBlank()
                }

                // Update the first memory with filtered photos
                if (filteredPhotos.size != this[1].photos.size) {
                    this[1] = this[1].copy(photos = filteredPhotos)
                }
            }
        }

        // Create new community with updated memories
        val updatedCommunity = community.copy(memories = updatedMemories)

        // Update the communities list
        communities[communityIndex] = updatedCommunity

        // Update the flow with new immutable list
        userCommunities.value = communities
    }

    override suspend fun addMemoryToCommunity(communityId: String, memory: Memory) {
        // Find the community
        val communityIndex = userCommunities.value.indexOfFirst { it.id == communityId }
        if (communityIndex == -1) return

        // Get all communities
        val communities = userCommunities.value.toMutableList()

        // Get the community we need to update
        val community = communities[communityIndex]

        val updatedCommunity = community.copy(
            memories = buildList {
                add(memory)
                addAll(community.memories)
            }
        )

        // Update the communities list
        communities[communityIndex] = updatedCommunity

        // Update the flow with new immutable list
        userCommunities.value = communities
    }
}