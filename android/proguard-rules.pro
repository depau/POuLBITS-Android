# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

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

-dontobfuscate

-keepattributes Signature
-keepattributes *Annotation*

-keep class * implements android.os.Parcelable {
 public static final android.os.Parcelable$Creator *;
}

-keepnames class * implements android.os.Parcelable {
 public static final ** CREATOR;
}
#-keep public class com.fasterxml.jackson.** { *; }
-keep public class org.springframework.http.converter.json.MappingJackson2HttpMessageConverter.** { *; }
-keep class com.fasterxml.jackson.annotation.** { *; }

-keep class org.poul.bits.android.lib.controllers.bitsclient.** { *; }
-keep class org.poul.bits.android.lib.model.** { *; }

#-keep class com.google.gson.*
-keep class com.google.gson.stream.** { *; }
-dontwarn sun.misc.**
-keep class sun.misc.Unsafe { *; }

-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

-keep class org.poul.bits.addon.mqtt.** { *; }