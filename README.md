AdManager [![](https://jitpack.io/v/fgustovo/admanager.svg)](https://jitpack.io/#fgustovo/admanager)
========
AdManager helps you to show ads.

Features
--------
AdManager's unique set of features:
* **RemoteConfigHelper:** Helper class for remote configs.
* **Custom Banner:** Helps you to show custom banners via remote config urls.
* **Firebase Builtin Ads:** Helps you to show popup ads.
* **Rate App:** App rating dialog
* **Static Notification**
* **Periodic Notification**
* **Utilities:** Samples in the sample module.
* **Adapters:**

|                |Interstitial|Banner|Native|RecyclerView Adapter for Native Ads
|----------------|------------|------------|------------|------------|
|**Admob:**|OK|OK|OK|OK|
|**Facebook:**|OK|OK|OK|OK|
|**Ironsource:**|OK|OK|||
|**AppLovin:**|OK||||
|**Unity:**|OK||||
|**Inmobi:**||OK|||
|**Mopub:**||OK|||


Add AdManager to your project:
----------------------------
AdManager is available on Jitpack. Please ensure that you are using the latest versions by [checking here](https://jitpack.io/#fgustovo/admanager)

Add the following Gradle configuration to your Android project:
```groovy
// In your root build.gradle file:
buildscript {
    repositories {
        jcenter()
        maven { url 'https://jitpack.io' } // add repository
    }
}

// In your app projects build.gradle file:
dependencies {
    implementation 'com.github.fgustovo.admanager:adapter-admob:1.2.12'
    implementation 'com.github.fgustovo.admanager:adapter-unity:1.2.12'
    implementation 'com.github.fgustovo.admanager:adapter-applovin:1.2.12'
    implementation 'com.github.fgustovo.admanager:adapter-facebook:1.2.12'
    implementation 'com.github.fgustovo.admanager:custom-banner:1.2.12'
    implementation 'com.github.fgustovo.admanager:periodic-notification:1.2.12'
    implementation 'com.github.fgustovo.admanager:popup-ad:1.2.12'
}
```

Initialize libraries in the Application class
```groovy
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        new AdmStaticNotification.Builder(this, R.string.easy_access_title, R.string.easy_access_text)
                .build();

        new PeriodicNotificationApp.Builder(this)
                .build();

        new RemoteConfigApp.Builder(RCUtils.getDefaults())
                .build();
    }
}
```

Create **RCUtils** to use **RemoteConfigHelper** easily
```groovy
public class RCUtils {
    public final static String S1_ADMOB_ENABLED = "s1_admob_enabled";
    public static final String S1_FACEBOOK_ENABLED = "s1_facebook_enabled";

    // todo add other Consts

  private static HashMap<String, Object> defaults = null;

    public static HashMap<String, Object> getDefaults() {
        if (defaults == null) {
            defaults = new HashMap<>();
            defaults.put(S1_ADMOB_ENABLED, true);
            defaults.put(S1_FACEBOOK_ENABLED, true);

            // todo add other Consts
	    }
        return defaults;
    }
}
```


```groovy
// In your Activity class:
AdManager manager = new AdManagerBuilder(this)
        .add(new AdmobAdapter(RCUtils.S1_ADMOB_ENABLED).withRemoteConfigId(RCUtils.S1_ADMOB_ID))
        .add(new FacebookAdapter(RCUtils.S1_FACEBOOK_ENABLED).withRemoteConfigId(RCUtils.S1_FACEBOOK_ID))
        .thenStart(Splash2Activity.class)
        .build();
```

You can check [Sample](sample) project.

 -----
__Displaying Options__

|Method|Description|Suitable For|
|----------------|-------------------------------|-------------------------------|
|`manager.show();` |Shows all ads serially.|Splash 1|
|`manager.showOne();` |Shows only one ad per call|Main menu ads|
|`manager.showOneByTimeCap(long timeCap);` |Shows one ad with given time cap|onResume|
|`manager.showAndFinish();` |Shows all ads and finishes current activity|onExit|



Custom Banner:
----------------------------
Fetches given Remote Config Key values and displayes Custom Banner.
```groovy
<com.admanager.custombanner.view.CustomBanner
  android:layout_width="match_parent"
  android:layout_height="wrap_content"
  app:remoteConfigEnableKey="custom_banner_enabled"
  app:remoteConfigImageUrlKey="custom_banner_image_url"
  app:remoteConfigTargetUrlKey="custom_banner_click_url" />
```


[CHANGELOG](CHANGELOG.md)
------------------------------



