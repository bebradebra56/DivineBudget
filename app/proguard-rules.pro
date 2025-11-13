# Add project specific ProGuard rules here.

-keep,allowobfuscation class ** { *; }
-keep class com.appsflyer.** { *; }
-keep class kotlin.jvm.internal.** { *; }
-keep public class com.android.installreferrer.** { *; }

-keep class com.divinebudget.app.data.** { *; }
-keepclassmembers class com.divinebudget.app.data.** { *; }
