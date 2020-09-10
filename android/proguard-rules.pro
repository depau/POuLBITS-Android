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

-keep public class org.poul.bits.android.** { *; }

-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp3.** { *; }
-dontwarn okio.
-dontwarn okio.**
-dontwarn okhttp3.**

-keep class * implements android.os.Parcelable {
 public static final android.os.Parcelable$Creator *;
}
-keepclassmembers public class org.springframework {
 public *;
}
-keepnames class * implements android.os.Parcelable {
 public static final ** CREATOR;
}

-dontwarn org.springframework.http.client.**
-dontwarn org.springframework.http.converter.feed.**
-dontwarn org.springframework.http.converter.json.**
-dontwarn org.springframework.http.converter.xml.**

-keep public class com.fasterxml.jackson.** { *; }
-dontwarn com.google.code.rome.android.repackaged.**
-dontwarn com.fasterxml.jackson.databind.**
-dontwarn org.simpleframework.xml.**
-dontwarn org.apache.commons.**
-keep public class org.springframework.http.converter.json.MappingJackson2HttpMessageConverter.** { *; }

-keep class org.springframework.core.convert.support.** { *; }

-keep class com.fasterxml.jackson.annotation.** { *; }
-dontwarn org.springframework.**
-dontnote org.simpleframework.**

-keep public class org.objectweb.asm.** { *; }
-dontwarn org.objectweb.asm.**

-keep public class org.osgi.framework.** { *; }
-dontwarn org.osgi.framework.**
-keep class javax.management.** { *; }
-dontwarn org.osgi.framework.**
-keep public class java.beans.** { *; }
-keep class java.beans.** { *; }
-dontwarn java.beans.**
-keep public class org.fusesource.hawtdispatch.** { *; }
-dontwarn org.fusesource.hawtdispatch.**
