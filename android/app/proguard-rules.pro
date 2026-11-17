# Proguard rules for Smart Expense Pro
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Room
-keepclassmembers class * {
    @androidx.room.* <methods>;
}

# Retrofit & Gson models
-keep class com.smart.expense.data.remote.dto.** { *; }
-keep class com.smart.expense.data.local.entities.** { *; }
-keep class com.smart.expense.ui.components.** { *; }
-keep class com.smart.expense.util.** { *; }

# Google AdMob
-keep class com.google.android.gms.ads.** { *; }
-dontwarn com.google.android.gms.ads.**
