# Android Clean Architecture App

This project is an Android application that follows Clean Architecture principles. It fetches data from a REST API and stores it in a local database using Room.

## Project Structure

```
android-clean-architecture-app
├── app
│   ├── src
│   │   ├── main
│   │   │   ├── java
│   │   │   │   └── com
│   │   │   │       └── example
│   │   │   │           └── myapp
│   │   │   │               ├── MyApplication.kt
│   │   │   │               ├── data
│   │   │   │               │   ├── local
│   │   │   │               │   │   ├── AppDatabase.kt
│   │   │   │               │   │   ├── dao
│   │   │   │               │   │   │   └── ItemDao.kt
│   │   │   │               │   │   └── entity
│   │   │   │               │   │       └── ItemEntity.kt
│   │   │   │               │   ├── remote
│   │   │   │               │   │   ├── ApiService.kt
│   │   │   │               │   │   └── model
│   │   │   │               │   │       └── ItemResponse.kt
│   │   │   │               │   └── repository
│   │   │   │               │       └── ItemRepositoryImpl.kt
│   │   │   │               ├── di
│   │   │   │               │   ├── AppModule.kt
│   │   │   │               │   ├── DatabaseModule.kt
│   │   │   │               │   └── NetworkModule.kt
│   │   │   │               ├── domain
│   │   │   │               │   ├── model
│   │   │   │               │   │   └── Item.kt
│   │   │   │               │   ├── repository
│   │   │   │               │   │   └── ItemRepository.kt
│   │   │   │               │   └── usecase
│   │   │   │               │       ├── GetItemsUseCase.kt
│   │   │   │               │       └── UpdateItemsUseCase.kt
│   │   │   │               └── presentation
│   │   │   │                   ├── MainActivity.kt
│   │   │   │                   └── items
│   │   │   │                       ├── ItemsFragment.kt
│   │   │   │                       ├── ItemsViewModel.kt
│   │   │   │                       └── adapter
│   │   │   │                           └── ItemAdapter.kt
│   │   │   ├── res
│   │   │   │   ├── layout
│   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   └── fragment_items.xml
│   │   │   │   └── values
│   │   │   │       ├── colors.xml
│   │   │   │       └── strings.xml
│   │   │   └── AndroidManifest.xml
│   │   └── test
│   │       ├── java
│   │       │   └── com
│   │       │       └── example
│   │       │           └── myapp
│   │       │               ├── data
│   │       │               │   └── repository
│   │       │               │       └── ItemRepositoryImplTest.kt
│   │       │               └── domain
│   │       │                   └── usecase
│   │       │                       └── GetItemsUseCaseTest.kt
│   │       └── resources
│   ├── build.gradle
│   └── proguard-rules.pro
├── build.gradle
├── gradle.properties
└── README.md
```

## Features

- Fetches data from a REST API using Retrofit.
- Stores data locally using Room database.
- Implements Clean Architecture principles, separating concerns into different layers (data, domain, presentation).
- Dependency injection is handled using a DI framework.

## Getting Started

1. Clone the repository.
2. Open the project in Android Studio.
3. Build the project and run it on an emulator or device.

## Dependencies

- Retrofit for network operations.
- Room for local database management.
- Dagger/Hilt for dependency injection.
- ViewModel and LiveData for managing UI-related data.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.