package com.example.myapp.di

import com.example.myapp.data.repository.FeedRepositoryImpl
import com.example.myapp.data.repository.PhotoRepositoryImpl
import com.example.myapp.data.repository.SocialRepositoryImpl
import com.example.myapp.domain.repository.FeedRepository
import com.example.myapp.domain.repository.PhotoRepository
import com.example.myapp.domain.repository.SocialRepository
import com.example.myapp.domain.usecase.*
import com.example.myapp.presentation.camera.CameraViewModel
import com.example.myapp.presentation.event.NewCommunityEventViewModel
import com.example.myapp.presentation.home.HomeViewModel
import com.example.myapp.presentation.memory.PhotoCompositionViewModel
import com.example.myapp.presentation.photodetail.PhotoDetailViewModel
import com.example.myapp.presentation.social.SocialViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
   // Repositories
   single<FeedRepository> { FeedRepositoryImpl() }
   single<PhotoRepository> { PhotoRepositoryImpl(get()) }
   single<SocialRepository> { SocialRepositoryImpl() }
   factory { AddMemoryToCommunityUseCase(get()) }

   // Use cases
   factory { GetFeedItemsUseCase(get()) }
   factory { SavePhotoUseCase(get()) }
   factory { GetAllPhotosUseCase(get()) }
   factory { GetPhotoByIdUseCase(get()) }
   factory { DeletePhotoUseCase(get()) }
   factory { GetUserCommunitiesUseCase(get()) }
   factory { GetCommunityPhotosUseCase(get()) }
   factory { GetMissingPhotoInfoUseCase(get()) }
   factory { UpdateMemoryPhotoUseCase(get()) }
   factory { SelectMissingPhotoUseCase(get()) }
   factory { ClearPhotoSelectionUseCase(get()) }
   factory { GetSelectedMissingPhotoUseCase(get()) }
   factory { GetMemoryUseCase(get()) }
   // ViewModels
   viewModel { HomeViewModel(get(), get(), get()) }
   viewModel { CameraViewModel(get()) }
   viewModel { PhotoDetailViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
   viewModel { SocialViewModel(get()) }
   viewModel { NewCommunityEventViewModel(get(), get()) }
   viewModel { PhotoCompositionViewModel(get()) }
}
