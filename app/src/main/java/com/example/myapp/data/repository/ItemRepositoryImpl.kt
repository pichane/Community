/*package com.example.myapp.data.repository

import com.example.myapp.data.local.dao.ItemDao
import com.example.myapp.data.remote.ApiService
import com.example.myapp.data.remote.model.ItemResponse
import com.example.myapp.domain.model.Item
import com.example.myapp.domain.repository.ItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ItemRepositoryImpl(
    private val itemDao: ItemDao,
    private val apiService: ApiService
) : ItemRepository {

    override suspend fun getItems(): List<Item> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getItems()
            if (response.isSuccessful) {
                response.body()?.let { itemResponse ->
                    itemDao.insertItems(itemResponse.items.map { it.toEntity() })
                    itemResponse.items.map { it.toDomain() }
                } ?: emptyList()
            } else {
                itemDao.getAllItems().map { it.toDomain() }
            }
        }
    }

    override suspend fun updateItems(items: List<Item>) {
        withContext(Dispatchers.IO) {
            itemDao.insertItems(items.map { it.toEntity() })
        }
    }
}

// Extension functions to convert between domain model and entity
private fun ItemResponse.Item.toEntity(): ItemEntity {
    return ItemEntity(id = this.id, name = this.name, description = this.description)
}

private fun ItemEntity.toDomain(): Item {
    return Item(id = this.id, name = this.name, description = this.description)
}

private fun ItemResponse.Item.toDomain(): Item {
    return Item(id = this.id, name = this.name, description = this.description)
}

 */