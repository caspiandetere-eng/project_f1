# Add project specific ProGuard rules here.

# Retrofit
-keepattributes Signature, Exceptions, *Annotation*
-keep class retrofit2.** { *; }
-keepclassmembernames interface * {
    @retrofit2.http.* <methods>;
}

# Gson — keep all model classes used for JSON deserialization
-keep class com.example.project_f1.models.** { *; }
-keepclassmembers class com.example.project_f1.models.** { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Keep line numbers for crash traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
