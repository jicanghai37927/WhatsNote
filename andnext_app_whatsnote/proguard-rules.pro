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


#
-optimizationpasses 5                                       #指定代码压缩级别
-dontusemixedcaseclassnames                                 #混淆时不会产生形形色色的类名
-dontskipnonpubliclibraryclasses                            #指定不忽略非公共类库
-dontpreverify                                              #不预校验，如果需要预校验，是-dontoptimize
-ignorewarnings                                             #屏蔽警告
-verbose                                                    #混淆时记录日志
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*    #优化

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.v7.** { *; }
-keep class android.support.v4.** { *; }
-keep class android.support.v8.** {*;}
-keep interface android.support.constraint.** { *; }

# 出现过数据类NullPointer以及type字段丢失问题，因此不混淆数据类
-keep public class * implements java.io.Serializable { *; }

-keep public class * extends club.andnext.base.BaseEntry { *; }
-keep public class * extends app.haiyunshan.whatsnote.chat.entry.ChatEntry { *; }
-keep public class * extends app.haiyunshan.whatsnote.article.entry.ArticleEntry { *; }

# 不混淆BridgeViewHolder子类的构造函数
-keepclasseswithmembers class * extends club.andnext.recyclerview.bridge.BridgeViewHolder {
    <init>(...);
}

#
-keep class app.haiyunshan.whatsnote.song.**{*;}

#这个主要是在layout 中写的onclick方法android:onclick="onClick"，不进行混淆
#表示不混淆Activity中参数是View的方法，因为有这样一种用法，在XML中配置android:onClick=”buttonClick”属性，
#当用户点击该按钮时就会调用Activity中的buttonClick(View view)方法，如果这个方法被混淆的话就找不到了
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

#

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class club.andnext.base.** { *; }

-keep class app.haiyunshan.whatsnote.article.entry.** { *; }

-keep class app.haiyunshan.whatsnote.chat.entry.** { *; }
-keep class app.haiyunshan.whatsnote.chat.extension.** { *; }

-keep class app.haiyunshan.whatsnote.config.** { *; }

-keep class app.haiyunshan.whatsnote.official.database.** { *; }
-keep class app.haiyunshan.whatsnote.official.entry.** { *; }

-keep class app.haiyunshan.whatsnote.preference.dataset.** { *; }

-keep class app.haiyunshan.whatsnote.record.database.** { *; }
-keep class app.haiyunshan.whatsnote.record.entry.** { *; }

-keep class app.haiyunshan.whatsnote.share.database.** { *; }
-keep class app.haiyunshan.whatsnote.share.entry.** { *; }

-keep class app.haiyunshan.whatsnote.song.entry.** { *; }

-keep class app.haiyunshan.whatsnote.tag.database.** { *; }
-keep class app.haiyunshan.whatsnote.tag.entry.** { *; }

-keep class app.haiyunshan.whatsnote.update.** { *; }

##---------------End: proguard configuration for Gson  ----------

#numdump
-dontwarn com.yeamy.ncmdump.**
-keep class com.yeamy.ncmdump.** { *; }
-keep interface com.yeamy.ncmdump.** { *; }
#

# jaudiotagger
-dontwarn org.jaudiotagger.**
-keep class org.jaudiotagger.** { *; }
-keep interface org.jaudiotagger.** { *; }
#

# uCrop
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }
#

# EventBus
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
#

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
