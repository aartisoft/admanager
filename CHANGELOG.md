# Changelog
All notable changes to this project will be documented in this file.

## [1.6.0] - 2019-07-28
### Changed
- PopupAd: renamed as PopupPromo, and also dependency name "popup-ad" renamed as "popup-promo"

## [1.5.0] - 2019-07-24
### Changed
- Tutorial: now back button closes the app

## [1.3.8] - 2019-07-04
### Changed
- Tutorial: default tutorial button and bg color set as native Bg color

## [1.3.7] - 2019-06-28
### Changed
- AdmobNative: NATIVE_XL button size increased, amd added padding

## [1.3.6] - 2019-06-27
### Changed
- bugfix

## [1.3.5] - 2019-06-21
### Changed
- WAStickers: added back arrow to toolbar

## [1.3.4] - 2019-06-19
### Changed
- WAStickers: minor changes

## [1.3.3] - 2019-06-18
### Changed
- WAStickers: minor changes

## [1.3.2] - 2019-06-18
### Added
- WAStickers: new sticker library added

### Changed
- Remote Config: sdk version updated


## [1.3.1] - 2019-06-14
### Changed
- AppLocker: bugfix

## [1.3.0] - 2019-06-12
### Changed
- Admob SDK and Facebook SDK versions upgraded
- You need to add `<meta-data android:name="com.google.android.gms.ads.APPLICATION_ID" android:value="@string/admob_app_id" />` to AndroidManifest.xml now.

## [1.2.13] - 2019-06-11
### Changed
- Admob Native Loader: bugfix

## [1.2.12] - 2019-05-22
### Changed
- Ironsource Interstitial: added default 5 secs timeout

## [1.2.11] - 2019-05-02
### Changed
- Admob Native: `NATIVE_BANNER` is a bit higher then before. If you still want to use a small one, use the `NATIVE_BANNER_XS` parameter.
- Admob Native Banner: Enhanced design

## [1.2.10] - 2019-04-30
### Changed
- Test Ids became public
- Facebook native button color now green
- Booster Notif: Removed app label 
- Removed unnecessary app_name resources
- BannerLoader/NativeLoader enhanced logging


## [1.2.9] - 2019-04-22
### Changed
- App Lock: bug fix

## [1.2.8] - 2019-04-22
### Added
- App Lock: added `AppLockerApp.configureMenu(NavigationView navView, int menuId)` method in order to hide AppLocker menu if device not suitable for this feature.
- App Lock: added `AppLockerApp.isDeviceSuitable()` method to check is the device suitable for this feature.
- Booster Notification: added `appLauncherIcon(int)` method for custom icon

### Changed
- `iconBig(int)` methods are deprecated. Use `iconLarge(int)` instead.

### Removed
- `allowBackup` parameter from Manifest files

## [1.2.7] - 2019-04-18
### Changed
- App Lock: bug fix self unlocked apps 

## [1.2.7-rc1] - 2019-04-18
### Changed
- App Lock startForeground crash fix attempt 

## [1.2.6] - 2019-04-17
### Changed
- Tutorial activity: bug fix

## [1.2.5] - 2019-04-17
### Changed
- Tutorial activity: Added space between banners and center native   
- Ironsource Adapter: enhanced proguard 

## [1.2.4] - 2019-04-17
### Changed
- Ironsource Adapter: version upgraded 

## [1.2.3] - 2019-04-15
### Added
- Tutorial: Configuration for Center Ad to move top of the page

## [1.2.2] - 2019-04-15
### Changed
- AppLock: back button added 
- Proguard enhancements 
- Logging TAG change to "ADM"

## [1.2.1] - 2019-04-12
### Changed
- Android 8 bug fix

## [1.2.0-rc5] - 2019-04-11
### Changed
- Popup Ad opens play store only if clicked yes button.
- Bug fix

## [1.2.0-rc4] - 2019-04-11
### Changed
- Bug fix

## [1.2.0-rc3] - 2019-04-11
### Changed
- Bug fix

## [1.2.0-rc2] - 2019-04-11
### Changed
- Bug fix

## [1.2.0-rc1] - 2019-04-11
### Added
- App Lock Module
- Booster Notification Module

## [1.1.6-RC2] - 2019-04-07
### Changed
- Bug fix

## [1.1.6-RC1] - 2019-04-07
### Added
- Timeout feature to Adapters
- UnityAdapter default timeout value is 5000ms.

## [1.1.5] - 2019-04-06
### Added
- Multiple background color support added to AdmTutorialActivity

### Deprecated
- Rate dialog deprecated


## [1.1.4] - 2019-04-02
### Added
- Admob n sec interstitial


## [1.1.1] - 2019-03-29
### Added
- "Ad" label for Popup Ads

### Changed
- cancelled outside clicks for Popup Ads.

