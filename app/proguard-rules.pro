# Proguard rules for NoCallCenter

# Keep Jetpack Compose
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}

# Keep Kotlin Serialization
-keepattributes *Annotation*,InnerClasses
-dontnote kotlinx.serialization.SerializationKt

# Keep Android entry points and CallScreeningService
-keep public class com.nocallcenter.app.MainActivity { *; }
-keep public class com.nocallcenter.app.NoCallCenterApplication { *; }
-keep public class com.nocallcenter.app.service.NoCallCenterScreeningService { *; }
