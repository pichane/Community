// package com.example.myapp.data.remote

// import retrofit2.http.GET
// import retrofit2.http.Path
// import com.example.myapp.data.remote.model.ItemResponse

// interface ApiService {
//     @GET("items")
//     suspend fun getItems(): List<ItemResponse>

//     @GET("items/{id}")
//     suspend fun getItemById(@Path("id") id: String): ItemResponse
// }