# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


-keep class com.systematics.buggee.Chat.Chat_GetSet
-keep class com.systematics.buggee.Comments.Comment_Get_Set
-keep class com.systematics.buggee.Comments.LiveCommentData
-keep class com.systematics.buggee.Discover.Discover_Get_Set
-keep class com.systematics.buggee.Following.Following_Get_Set
-keep class com.systematics.buggee.Home.Home_Get_Set
-keep class com.systematics.buggee.Home.LiveData
-keep class com.systematics.buggee.Home.ImageStoryData
-keep class com.systematics.buggee.Inbox.Inbox_Get_Set
-keep class com.systematics.buggee.Notifications.Notification_Get_Set
-keep class com.systematics.buggee.Profile.MyVideos_Get_Set
-keep class com.systematics.buggee.Search.Users_Model
-keep class com.systematics.buggee.SoundLists.Sounds_GetSet
-keep class com.systematics.buggee.SoundLists.Sound_catagory_Get_Set
-keep class com.systematics.buggee.Video_Recording.Merge_Video_Audio
-keep class com.systematics.buggee.Video_Recording.GalleryVideos.GalleryVideo_Get_Set
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-keepnames class org.jsoup.nodes.Entities
-keepattributes SourceFile, LineNumberTable
-keepattributes LocalVariableTable, LocalVariableTypeTable
-keepclassmembers class ai.deepar.ar.DeepAR { *; }
-keep class io.agora.**{*;}
-keep class com.github.MasayukiSuda.**{*;}
-keep class com.daasuu.gpuv.composer.GPUMp4Composer
-keep class com.daasuu.gpuv.** {*;}
-keep class com.systematics.buggee.utils.Utility
-keep class com.coremedia.iso.** {*;}
-keep class com.googlecode.mp4parser.** {*;}
-keep class com.mp4parser.** {*;}
-dontwarn com.coremedia.**
-dontwarn com.googlecode.mp4parser.**


-keep interface org.parceler.Parcel
-keep @org.parceler.Parcel class * { *; }
-keep class **$$Parcelable { *; }
-dontwarn java.lang.invoke.*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontnote okhttp3.**

# Okio
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}