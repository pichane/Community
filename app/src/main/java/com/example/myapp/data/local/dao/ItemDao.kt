// package com.example.myapp.data.local.dao

// import androidx.room.Dao
// import androidx.room.Insert
// import androidx.room.OnConflictStrategy
// import androidx.room.Query
// import com.example.myapp.data.local.entity.ItemEntity

// @Dao
// interface ItemDao {
//     @Insert(onConflict = OnConflictStrategy.REPLACE)
//     suspend fun insertItem(item: ItemEntity)

//     @Query("SELECT * FROM items")
//     suspend fun getAllItems(): List<ItemEntity>

//     @Query("DELETE FROM items")
//     suspend fun deleteAllItems()
// }