package com.admanager.sample

import com.admanager.core.Consts
import java.util.*

/**
 * Created by Gust on 20.11.2018.
 */
object RCUtils {
    const val s1_admob_enabled = "s1_admob_enabled"
    const val s2_admob_enabled = "s2_admob_enabled"
    const val main_admob_enabled = "main_admob_enabled"
    const val s1_admob_id = "s1_admob_id"
    const val s2_admob_id = "s2_admob_id"
    const val main_admob_id = "main_admob_id"
    const val s1_facebook_id = "s1_facebook_id"

    const val native_facebook_enabled = "native_facebook_enabled"
    const val native_facebook_id = "native_facebook_id"
    const val tutorial_native_admob_id = "tutorial_native_admob_id"
    const val tutorial_native_admob_enabled = "tutorial_native_admob_enabled"
    const val native_admob_enabled = "native_admob_enabled"
    const val native_admob_id = "native_admob_id"
    const val main_6sec_facebook_enabled = "main_6sec_facebook_enabled"
    const val main_6sec_facebook_id = "main_6sec_facebook_id"
    const val main_3sec_admob_enabled = "main_3sec_admob_enabled"
    const val main_3sec_admob_id = "main_3sec_admob_id"
    const val main_popup_rewarded_enabled = "main_popup_rewarded_enabled"
    const val main_popup_rewarded_id = "main_popup_rewarded_id"
    const val admob_banner_enabled = "admob_banner_enabled"
    const val admob_banner_id = "admob_banner_id"
    const val admob_libs_banner_enabled = "admob_libs_banner_enabled"
    const val admob_libs_banner_id = "admob_libs_banner_id"
    const val facebook_banner_enabled = "facebook_banner_enabled"
    const val facebook_banner_id = "facebook_banner_id"
    const val custom_banner_enabled = "custom_banner_enabled"
    const val custom_banner_image_url = "custom_banner_image_url"
    const val custom_banner_click_url = "custom_banner_click_url"
    const val opener_delay = "opener_delay"

    const val ADMOST_APP_ID = "admost_app_id"
    const val REWARDED_ZONE_ID = "rewarded_zone_id" //todo


    private var defaults: HashMap<String, Any>? = null

    @JvmStatic
    fun getDefaults(): HashMap<String, Any>? {
        if (defaults == null) {
            defaults = HashMap()
            defaults!![admob_libs_banner_enabled] = true
            defaults!![s1_admob_enabled] = true
            defaults!![s2_admob_enabled] = true
            defaults!![tutorial_native_admob_enabled] = true
            defaults!![main_admob_enabled] = true
            defaults!![main_6sec_facebook_enabled] = true
            defaults!![main_3sec_admob_enabled] = true
            defaults!![main_popup_rewarded_enabled] = true
            defaults!![native_facebook_enabled] = true
            defaults!![native_admob_enabled] = true
            defaults!![facebook_banner_enabled] = true
            defaults!![admob_banner_enabled] = true
            defaults!![custom_banner_enabled] = true

            defaults!![s1_admob_id] = "ca-app-pub-3940256099942544/1033173712" // TODO
            defaults!![s2_admob_id] = "ca-app-pub-3940256099942544/1033173712" // TODO
            defaults!![main_admob_id] = "ca-app-pub-3940256099942544/1033173712" // TODO
            defaults!![admob_banner_id] = "ca-app-pub-3940256099942544/6300978111" // TODO
            defaults!![native_admob_id] = "ca-app-pub-3940256099942544/2247696110" // TODO
            defaults!![tutorial_native_admob_id] = "ca-app-pub-3940256099942544/2247696110" // TODO
            defaults!![main_3sec_admob_id] = "ca-app-pub-3940256099942544/1033173712" // TODO
            defaults!![admob_libs_banner_id] = "ca-app-pub-3940256099942544/1033173712" // TODO
            defaults!![main_popup_rewarded_id] = "ca-app-pub-3940256099942544/5224354917" // TODO

            defaults!![s1_facebook_id] = "YOUR_PLACEMENT_ID" // TODO
            defaults!![native_facebook_id] = "YOUR_PLACEMENT_ID" // TODO
            defaults!![main_6sec_facebook_id] = "YOUR_PLACEMENT_ID" // TODO
            defaults!![facebook_banner_id] = "YOUR_PLACEMENT_ID" // TODO

            defaults!![custom_banner_image_url] = ""
            defaults!![custom_banner_click_url] = ""

            //NOTIFIC
            defaults!![Consts.PeriodicNotification.DEFAULT_ENABLE_KEY] = true
            defaults!![Consts.PeriodicNotification.DEFAULT_DAYS_KEY] = 1
            defaults!![Consts.PeriodicNotification.DEFAULT_TITLE_KEY] = ""
            defaults!![Consts.PeriodicNotification.DEFAULT_TICKER_KEY] = ""
            defaults!![Consts.PeriodicNotification.DEFAULT_CONTENT_KEY] = ""

            defaults!![Consts.PopupPromo.DEFAULT_ENABLE_KEY] = false
            defaults!![Consts.PopupPromo.DEFAULT_MESSAGE_KEY] = ""
            defaults!![Consts.PopupPromo.DEFAULT_NO_KEY] = ""
            defaults!![Consts.PopupPromo.DEFAULT_TITLE_KEY] = ""
            defaults!![Consts.PopupPromo.DEFAULT_URL_KEY] = ""
            defaults!![Consts.PopupPromo.DEFAULT_YES_KEY] = ""
            // defaults!!(Consts.PopupPromo.DEFAULT_LOGO_URL_KEY, "")
            // defaults!!(Consts.PopupPromo.DEFAULT_VIDEO_URL_KEY, "")
            // defaults!!(Consts.PopupPromo.DEFAULT_IMAGE_URL_KEY, "")

            defaults!![ADMOST_APP_ID] = "6cc8e89a-b52a-4e9a-bb8c-579f7ec538fe" // TEST
            defaults!![REWARDED_ZONE_ID] = "88cfcfd0-2f8c-4aba-9f36-cc0ac99ab140" //TEST


            defaults!![Consts.PopupEnjoy.DEFAULT_ENABLE_KEY] = true
            defaults!![Consts.PopupEnjoy.DEFAULT_NO_KEY] = ""
            defaults!![Consts.PopupEnjoy.DEFAULT_YES_KEY] = ""
            defaults!![Consts.PopupEnjoy.DEFAULT_TITLE_KEY] = ""
            defaults!![Consts.PopupEnjoy.DEFAULT_IMAGE_URL_KEY] = ""

            defaults!![Consts.PopupRate.DEFAULT_ENABLE_KEY] = true

            defaults!![opener_delay] = 2000
        }
        return defaults
    }
}