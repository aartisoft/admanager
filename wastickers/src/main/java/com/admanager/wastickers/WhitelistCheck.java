package com.admanager.wastickers;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.admanager.core.AdmUtils;
import com.admanager.wastickers.utils.WAStickerHelper;

@SuppressWarnings("FieldCanBeLocal")
public class WhitelistCheck {
    private static final String AUTHORITY_QUERY_PARAM = "authority";
    private static final String IDENTIFIER_QUERY_PARAM = "identifier";
    public static String CONSUMER_WHATSAPP_PACKAGE_NAME = "com.whatsapp";
    public static String SMB_WHATSAPP_PACKAGE_NAME = "com.whatsapp.w4b";
    private static String CONTENT_PROVIDER = ".provider.sticker_whitelist_check";
    private static String QUERY_PATH = "is_whitelisted";
    private static String QUERY_RESULT_COLUMN_NAME = "result";

    public static boolean isWhitelisted(Context context, @NonNull String identifier) {
        if (AdmUtils.isContextInvalid(context)) {
            return false;
        }
        try {
            boolean consumerResult = isWhitelistedFromProvider(context, identifier, CONSUMER_WHATSAPP_PACKAGE_NAME);
            boolean smbResult = isWhitelistedFromProvider(context, identifier, SMB_WHATSAPP_PACKAGE_NAME);
            return consumerResult && smbResult;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isWhitelistedFromProvider(@NonNull Context context, @NonNull String identifier, String whatsappPackageName) {
        final PackageManager packageManager = context.getPackageManager();
        if (isPackageInstalled(whatsappPackageName, packageManager)) {
            final String whatsappProviderAuthority = whatsappPackageName + CONTENT_PROVIDER;
            final ProviderInfo providerInfo = packageManager.resolveContentProvider(whatsappProviderAuthority, PackageManager.GET_META_DATA);
            // provider is not there.
            if (providerInfo == null) {
                return false;
            }
            String STICKER_APP_AUTHORITY = WAStickerHelper.getContentProviderAuthority(context);
            final Uri queryUri = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(whatsappProviderAuthority).appendPath(QUERY_PATH).appendQueryParameter(AUTHORITY_QUERY_PARAM, STICKER_APP_AUTHORITY).appendQueryParameter(IDENTIFIER_QUERY_PARAM, identifier).build();
            try {
                final Cursor cursor = context.getContentResolver().query(queryUri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int whiteListResult = cursor.getInt(cursor.getColumnIndexOrThrow(QUERY_RESULT_COLUMN_NAME));
                    return whiteListResult == 1;
                }
            } catch (Throwable ignore) {

            }
        } else {
            //if app is not installed, then don't need to take into its whitelist info into account.
            return true;
        }
        return false;
    }

    public static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            final ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            //noinspection SimplifiableIfStatement
            if (applicationInfo != null) {
                return applicationInfo.enabled;
            } else {
                return false;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
