package com.github.nrudenko.orm;

import android.content.ContentProvider;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.os.Bundle;

public class MetaDataParser {

    static <T> T getMetaData(Context context, String name) {
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            Bundle metaData = info.metaData;
            if (metaData != null)
                return (T) metaData.get(name);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo info = packageManager.getApplicationInfo(context.getPackageName(), 0);
            return (String) packageManager.getApplicationLabel(info);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getAuthority(Context context, Class<? extends ContentProvider> providerClass) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_PROVIDERS);
            ProviderInfo[] providers = packageInfo.providers;
            if (providers.length > 0) {
                for (int i = 0; i < providers.length; i++) {
                    ProviderInfo provider = providers[i];
                    if (provider.name.contains(providerClass.getSimpleName())) {
                        return provider.authority;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getFirstAuthority(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_PROVIDERS);
            ProviderInfo[] providers = packageInfo.providers;
            if (providers.length > 0) {
                for (int i = 0; i < providers.length; i++) {
                    ProviderInfo provider = providers[i];
                    return provider.authority;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
