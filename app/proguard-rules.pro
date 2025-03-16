# ProGuard rules for the Android app

# Keep the application class
-keep class com.example.myapp.MyApplication { *; }

# Keep Room database classes
-keep class com.example.myapp.data.local.** { *; }
-keep class com.example.myapp.data.local.entity.** { *; }
-keep class com.example.myapp.data.local.dao.** { *; }

# Keep Retrofit service interface
-keep interface com.example.myapp.data.remote.ApiService { *; }

# Keep data models
-keep class com.example.myapp.data.remote.model.** { *; }
-keep class com.example.myapp.domain.model.** { *; }

# Keep repository interfaces and implementations
-keep interface com.example.myapp.domain.repository.** { *; }
-keep class com.example.myapp.data.repository.** { *; }

# Keep use cases
-keep class com.example.myapp.domain.usecase.** { *; }

# Keep ViewModels
-keep class com.example.myapp.presentation.**ViewModel { *; }

# Keep fragments
-keep class com.example.myapp.presentation.items.**Fragment { *; }

# Keep adapters
-keep class com.example.myapp.presentation.items.adapter.** { *; }

# Keep all annotations
-keepattributes *Annotation*