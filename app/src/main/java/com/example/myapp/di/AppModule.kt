package com.example.myapp.di

import com.example.myapp.data.repository.CameraRepositoryImpl
import com.example.myapp.data.repository.PhotoRepositoryImpl
import com.example.myapp.data.repository.SocialRepositoryImpl
import com.example.myapp.domain.repository.CameraRepository
import com.example.myapp.domain.repository.PhotoRepository
import com.example.myapp.domain.repository.SocialRepository
import com.example.myapp.domain.usecase.AddMemoryToCommunityUseCase
import com.example.myapp.domain.usecase.DeletePhotoUseCase
import com.example.myapp.domain.usecase.GetAllPhotosUseCase
import com.example.myapp.domain.usecase.GetMemoryUseCase
import com.example.myapp.domain.usecase.GetMissingPhotoInfoUseCase
import com.example.myapp.domain.usecase.GetPhotoByIdUseCase
import com.example.myapp.domain.usecase.GetUserCommunitiesUseCase
import com.example.myapp.domain.usecase.SavePhotoUseCase
import com.example.myapp.domain.usecase.UpdateMemoryPhotoUseCase
import com.example.myapp.presentation.screen.camera.CameraViewModel
import com.example.myapp.presentation.screen.home.HomeViewModel
import com.example.myapp.presentation.screen.memory.PhotoCompositionViewModel
import com.example.myapp.presentation.screen.photodetail.PhotoDetailViewModel
import com.example.myapp.presentation.screen.social.SocialViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import com.example.myapp.domain.usecase.*
import org.koin.android.ext.koin.androidContext

val appModule = module {
    // Repositories:
    single<PhotoRepository> { PhotoRepositoryImpl(get()) }
    single<SocialRepository> { SocialRepositoryImpl() }
    single<CameraRepository> { CameraRepositoryImpl( androidContext()) }

    // Use cases :
    factory { SavePhotoUseCase(get()) }
    factory { GetAllPhotosUseCase(get()) }
    factory { GetPhotoByIdUseCase(get()) }
    factory { DeletePhotoUseCase(get()) }
    factory { GetUserCommunitiesUseCase(get()) }
    factory { GetMissingPhotoInfoUseCase(get()) }
    factory { UpdateMemoryPhotoUseCase(get()) }
    factory { GetMemoryUseCase(get()) }
    factory { AddMemoryToCommunityUseCase(get()) }

    // Camera
    factory { CapturePhotoUseCase(get()) }

    // Social
    factory { GetDiscoveryUsersUseCase(get()) }
    factory { GetFriendsUseCase(get()) }
    factory { GetDiscoveryCommunitiesUseCase(get()) }
    factory { AddFriendUseCase(get()) }
    factory { JoinCommunityUseCase(get()) }

    // ViewModels
    viewModel { HomeViewModel(get(), get()) }
    viewModel { CameraViewModel(get(), get()) }
    viewModel { PhotoDetailViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { SocialViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { PhotoCompositionViewModel(get()) }

}
