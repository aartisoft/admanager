# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Nebojsha\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
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
#			FLURRY
#*********************************************************************************************************
-keep class com.flurry.** { *; }
-dontwarn com.flurry.**
-keepattributes *Annotation*,EnclosingMethod,Signature
-keepclasseswithmembers class * {
  public <init>(android.content.Context, android.util.AttributeSet, int);
}
#/*****************************************************


-dontwarn javax.annotation.Nullable

# ---- PRESAGE - start

-dontnote io.presage.**
-dontwarn shared_presage.**
-dontwarn org.codehaus.**

-keepattributes Signature

-keep class shared_presage.** { *; }
-keep class io.presage.** { *; }
-keepclassmembers class io.presage.** {
 *;
}

-keepattributes *Annotation*
-keepattributes JavascriptInterface
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# ---- OKHTTP
-dontnote okhttp3.**
-dontnote okio.**
-dontwarn okhttp3.**
-dontwarn okio.**

-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault

-dontnote sun.misc.Unsafe
-dontnote android.net.http.*

-dontnote org.apache.commons.codec.**
-dontnote org.apache.http.**

-dontwarn org.apache.commons.collections.BeanMap
-dontwarn java.beans.**

# ---- GOOGLE
-dontnote com.google.gson.**
-dontnote com.google.android.gms.ads.**
-dontnote com.google.android.**
-dontnote com.google.ads.**

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ---- PRESAGE - end

#ONEAUDIENCE
-keep class com.oneaudience.** { *; }
-keep class com.google.android.gms.** {  *; }


#	FABRIC

-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable


#	SUGAR ORM
-keep public class * extends com.orm.SugarRecord {
  public protected *;
}
-keep class com.orm.** { *; }

#*************** AUDIENCE NETWORK

-keep class com.facebook.ads.** { *; }
-dontwarn com.facebook.ads.**

#**************** ADMOB
-keep class com.google.ads.** # Don't proguard AdMob classes
-dontwarn com.google.ads.** # Temporary workaround for v6.2.1. It gives a warning that you can ignore

#***************** FIREBASE
-keep class com.firebase.** { *; }

#*************************EVENTBUS

-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

# Mobiburn
-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*
-keep class com.google.android.gms.** {  *; }
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.Service

# Advanced Web View
-keep class * extends android.webkit.WebChromeClient { *; }
-dontwarn im.delight.android.webview.**

#Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}


-keep class com.truenet.** {
      *;
}

-keepattributes Exceptions, InnerClasses, Signature, Deprecated, SourceFile, LineNumberTable, *Annotation*, EnclosingMethod
-dontwarn android.webkit.JavascriptInterface

-dontwarn org.jetbrains.annotations.**

# OkHttp
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.** { *; }
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }

# GSON
-keep class com.google.gson.** { *; }

# Okio
-dontwarn okio.**
-keep class okio.** { *; }

-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

# ADMOST
-keepattributes Exceptions, InnerClasses
-dontwarn admost.sdk.**
-keep class admost.sdk.** {*;}
-dontwarn admost.adserver.**
-keep class admost.adserver.** { *; }
-dontwarn com.google.android.exoplayer2.**
-keep class com.google.android.exoplayer2.**{ *;}
-keep class android.support.v4.app.DialogFragment { *; }
-keep class android.support.v4.util.LruCache { *; }


# VOLLEY
-keep class com.android.volley.** { *; }
-keep interface com.android.volley.** { *; }
-keep class org.apache.commons.logging.**

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #

# ADCOLONY
-dontwarn com.adcolony.**
-keep class com.adcolony.** { *; }
-keepclassmembers class * { @android.webkit.JavascriptInterface <methods>; }
-keepclassmembers class com.adcolony.sdk.ADCNative** { *; }

# ADGEM
-keep class com.adgem.** { *; }
-dontwarn com.adgem.**

# ADMOB / ADX / GOOGLE
-keep class com.android.vending.billing.**
-keep public class com.google.android.gms.ads.** { public *; }
-keep public class com.google.ads.** { public *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable { public static final *** NULL; }
-keepnames class * implements android.os.Parcelable
-keepclassmembers class * implements android.os.Parcelable { public static final *** CREATOR; }
-keep @interface android.support.annotation.Keep
-keep @android.support.annotation.Keep class *
-keepclasseswithmembers class * { @android.support.annotation.Keep <fields>; }
-keepclasseswithmembers class * { @android.support.annotation.Keep <methods>; }
-keep @interface com.google.android.gms.common.annotation.KeepName
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * { @com.google.android.gms.common.annotation.KeepName *; }
-keep @interface com.google.android.gms.common.util.DynamiteApi
-keep public @com.google.android.gms.common.util.DynamiteApi class * { public <fields>; public <methods>; }
-keep public @com.google.android.gms.common.util.DynamiteApi class * { *; }
-keep class com.google.android.apps.common.proguard.UsedBy*
-keep @com.google.android.apps.common.proguard.UsedBy* class *
-keepclassmembers class * { @com.google.android.apps.common.proguard.UsedBy* *; }
-dontwarn android.security.NetworkSecurityPolicy
-dontwarn android.app.Notification

#ADTIMING
-dontwarn com.aiming.mdt.**.*
-dontoptimize
-dontskipnonpubliclibraryclasses
-keepattributes *Annotation*
#adt
-keep class com.aiming.mdt.**{ *; }
-keepattributes *Annotation*,InnerClasses
-keepnames class * implements android.os.Parcelable {
public static final ** CREATOR;
}
#R
-keepclassmembers class **.R$* {
public static <fields>;
}

# AMAZON
-dontwarn com.amazon.**
-keep class com.amazon.** { *; }
-dontwarn org.apache.http.**
-keepattributes *Annotation*

# APPLOVIN
-dontwarn com.applovin.**
-keep class com.applovin.** { *; }

# APPNEXT
-keep class com.appnext.** { *; }
-dontwarn com.appnext.**

# APPSAMURAI
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# AVOCARROT - GLISPA
-keep class com.avocarrot.** { *; }
-dontwarn com.avocarrot.**
-keep class com.google.android.exoplayer2.SimpleExoPlayer
-dontwarn com.google.android.exoplayer2.SimpleExoPlayer

# CHARTBOOST
-dontwarn org.apache.http.**
-dontwarn com.chartboost.sdk.impl.**
-keep class com.chartboost.sdk.** { *; }
-keepattributes *Annotation*

# CRITEO
-dontwarn com.criteo.**
-keep class com.criteo.** { *; }

# DISPLAYIO
-keep class io.display.sdk.** { *; }
-dontwarn io.display.sdk.**
-keep class com.brandio.ads.** { *;}
-dontwarn com.brandio.ads.**

# FACEBOOK
-dontwarn com.facebook.ads.**
-dontnote com.facebook.ads.**
-keep class com.facebook.** { *; }
-keepattributes Signature
-keep class com.google.android.exoplayer2.** {*;}
-dontwarn com.google.android.exoplayer2.**

# FLURRY
-keep class com.flurry.** { *; }
-dontwarn com.flurry.**
-keepattributes *Annotation*,EnclosingMethod,Signature
-keepclasseswithmembers class * { public <init>(android.content.Context, android.util.AttributeSet, int); }
-keep class * extends java.util.ListResourceBundle { protected Object[][] getContents(); }
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable { public static final *** NULL; }
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * { @com.google.android.gms.common.annotation.KeepName *; }
-keepnames class * implements android.os.Parcelable { public static final ** CREATOR; }

# FREECORP
-keep class com.fly.** { *; }
-dontwarn com.fly.**

# FYBER
-keep class com.fyber.** { *; }
-dontwarn com.fyber.**
-keep class com.fyber.mediation.MediationConfigProvider { public static *; }
-keep class com.fyber.mediation.MediationAdapterStarter { public static *; }
-keepclassmembers class com.fyber.ads.videos.mediation.** { void setValue(java.lang.String); }

# INMOBI
-keepattributes SourceFile,LineNumberTable
-keep class com.inmobi.** {*;}
-dontwarn com.inmobi.**
-keep public class com.google.android.gms.**
-dontwarn com.google.android.gms.**
-dontwarn com.squareup.picasso.**
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient{public *;}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info{public *;}
-keep class com.squareup.picasso.** {*;}
-dontwarn com.squareup.picasso.**
-dontwarn com.squareup.okhttp.**
-keep class com.moat.** {*;}
-dontwarn com.moat.**
-keep class com.integralads.avid.library.* {*;}


# INNERACTIVE
-dontwarn com.inneractive.api.ads.**
-keep class com.inneractive.api.ads.** {*;}
-keepclassmembers class com.inneractive.api.ads.** {*;}
-keepclassmembers class com.inneractive.api.ads.sdk.nativead.** {*;}
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }
-keep class com.google.gson.Gson { *; }
-keep class com.google.gson.GsonBuilder { *; }
-keep class com.google.gson.FieldNamingStrategy { *; }

# IRONSOURCE
-dontwarn com.ironsource.**
-keep class com.ironsource.** { *; }
-keepclassmembers class * implements android.os.Parcelable { public static final android.os.Parcelable$Creator *; }
-keep public class com.google.android.gms.ads.** { public *; }
-keep class com.ironsource.adapters.** { *; }
-dontwarn com.moat.**
-keep class com.moat.** { public protected private *; }

# LOOPME
-dontwarn com.loopme.**
-keep class com.loopme.** { *; }

# MEDIABRIX
-keep class com.mediabrix.** { *; }
-keep class com.moat.** { *; }
-keep class mdos.** { *; }
-dontwarn com.mediabrix.**
-dontwarn com.moat.**
-dontwarn mdos.**

# MILLENNIAL & NEXAGE
-keep class com.millennialmedia.** { *; }
-dontwarn com.millennialmedia.**

# MINTEGRAL
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.mintegral.** {*; }
-keep interface com.mintegral.** {*; }
-keep class android.support.v4.** { *; }
-dontwarn com.mintegral.**
-keep class **.R$* { public static final int mintegral*; }
-keep class com.alphab.** {*; }
-keep interface com.alphab.** {*; }

# MOBFOX
-dontwarn com.mobfox.**
-keep class com.mobfox.** { *; }
-keep class com.mobfox.adapter.** { *; }
-keep class com.mobfox.sdk.** { *; }

# MOPUB
-keepclassmembers class com.mopub.** { public *; }
-dontnote com.mopub.**
-dontwarn com.mopub.**
-keep public class com.mopub.**
-keep class com.mopub.mobileads.** { *; }
-keep class * extends com.mopub.mobileads.CustomEventBanner {}
-keep class * extends com.mopub.mobileads.CustomEventInterstitial {}
-keep class * extends com.mopub.nativeads.CustomEventNative {}
-keep class * extends com.mopub.nativeads.CustomEventRewardedAd {}
-keepclassmembers class ** { @com.mopub.common.util.ReflectionTarget *; }
-keepclassmembers class com.integralads.avid.library.mopub.** { public *; }
-keep public class com.integralads.avid.library.mopub.**
-keepclassmembers class com.moat.analytics.mobile.mpub.** { public *; }
-keep public class com.moat.analytics.mobile.mpub.**
-keep class com.google.android.gms.common.GooglePlayServicesUtil { *; }
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient { *; }
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info { *; }
-keep class * extends java.util.ListResourceBundle { protected Object[][] getContents(); }
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable { public static final *** NULL; }
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * { @com.google.android.gms.common.annotation.KeepName *; }
-keepnames class * implements android.os.Parcelable { public static final ** CREATOR; }
-keep public class android.webkit.JavascriptInterface {}

# NEND
-keep class net.nend.android.** { *; }
-dontwarn net.nend.android.**

# NATIVEX
-dontwarn com.nativex.**
-keep class com.nativex.** { *; }

# OUTBID
-dontwarn outbid.com.outbidsdk.**
-keep class outbid.com.outbidsdk.** { *; }

# QUMPARA
-dontwarn com.qumpara.analytics.**
-keep class com.qumpara.analytics.** { *; }
-dontwarn com.qumpara.offerwall.sdk.**
-keep class com.qumpara.offerwall.sdk.** { *; }

# PUBNATIVE
-keepattributes Signature
-keep class net.pubnative.** { *; }
-dontwarn net.pubnative.**
-keep class com.squareup.picasso.** { *; }
-dontwarn com.squareup.picasso.**

# REVMOB
-dontwarn rm.com.android.sdk.**
-keep class rm.com.android.sdk.** { public *; }

# RETROFIT
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# SMAATO
-dontwarn com.smaato.**
-keep class com.smaato.** { public *; }
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
-keep public class com.smaato.soma.internal.connector.OrmmaBridge { public *; }
-keepattributes *Annotation*
-dontwarn com.smaato.soma.SomaUnityPlugin*
-dontwarn com.millennialmedia**
-dontwarn com.facebook.**

# STARTAPP
-keepattributes Exceptions, InnerClasses, Signature, Deprecated, SourceFile, LineNumberTable, *Annotation*, EnclosingMethod
-dontwarn android.webkit.JavascriptInterface
-keep class com.startapp.** { *; }
-dontwarn com.startapp.**

# TAPJOY
-keep class com.tapjoy.** {*;}
-keep class com.moat.** {*;}
-keepattributes JavascriptInterface
-keepattributes *Annotation*
-keep class * extends java.util.ListResourceBundle {protected Object[][] getContents();}
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {public static final *** NULL;}
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {@com.google.android.gms.common.annotation.KeepName *;}
-keepnames class * implements android.os.Parcelable {public static final ** CREATOR;}
-keep class com.google.android.gms.ads.identifier.** {*;}
-dontwarn com.tapjoy.**

# TAPPX
-keepattributes *Annotation*
-keepclassmembers class com.google.**.R$* { public static <fields>; }
-keep public class com.google.ads.** {*;}
-keep public class com.google.android.gms.** {*;}
-keep public class com.tappx.** { *; }
-dontwarn com.tappx.**

# UNITY
-dontwarn com.unity3d.**
-keep class com.unity3d.ads.** { *; }

# Vungle
-dontwarn com.vungle.**
-dontnote com.vungle.**
-keep class com.vungle.** { *; }
-dontwarn com.vungle.warren.error.VungleError$ErrorCode
-keep class com.moat.** { *; }
-dontwarn com.moat.**
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**
-dontwarn retrofit2.Platform$Java8
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.examples.android.model.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keep class com.google.android.gms.internal.** { *; }
-dontwarn com.google.android.gms.ads.identifier.**

-keepclassmembers enum com.vungle.warren.** { *; }
-dontwarn kotlin.Unit
-dontwarn retrofit2.-KotlinExtensions
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn okhttp3.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-keepclassmembers class * extends com.vungle.warren.persistence.Memorable {
   public <init>(byte[]);
}


# YOUAPPI
-keep class com.google.gson.**{ *;}
-keep class com.google.android.gms.**{*;}
-keep class com.youappi.sdk.**{*;}
-keep interface com.youappi.sdk.**{*;}
-keep enum com.youappi.sdk.**{*;}
-keepclassmembers class * {
   @android.webkit.JavascriptInterface <methods>;
}

# VERIZON
-keepclassmembers class com.verizon.ads** {
public *;
}
-keep class com.verizon.ads**