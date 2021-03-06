# https://developer.android.com/studio/build/shrink-code.html
# https://www.guardsquare.com/en/proguard/manual/usage

# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\aStraube\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# public static int e(...);
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
    public static int wtf(...);
}

-keep class android.widget.IconTextView { *; }

-keep class cn.pedant.SweetAlert.Rotate3dAnimation {
   public <init>(...);
}

#-keep class com.crashlytics.** { *; }
#-dontwarn com.crashlytics.**

-keep class com.epson.** { *; }
-dontwarn com.epson.**

-keep public class org.jsoup.** {
    public *;
}

-keep class org.apache.http.**
-keep interface org.apache.http.**
-dontwarn org.apache.**

-dontwarn okio.**
-dontwarn javax.annotation.**

-dontwarn okio.**
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault

-dontnote retrofit2.Platform
-dontwarn retrofit2.Platform$Java8
-keepattributes Signature
-keepattributes Exceptions