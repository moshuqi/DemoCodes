package com.example.hd.ota.Utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**
 * Created by HD on 17/3/29.
 */

public class InfoUtils {

    public static int getVersionCode(Context context) {
        if (context != null) {
            try {
                return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            } catch (PackageManager.NameNotFoundException ignored) {
            }
        }
        return 0;
    }

    public static String getVersionName(Context context) {
        if (context != null) {
            try {
                return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException ignored) {
            }
        }

        return "";
    }

    public static String getVersionDescription() {
        String desc = "1、修复已知问题；\n2、优化用户体验；\n3、新增了XX功能。";
        return desc;
    }

    public static String getBaseApkPath(Context context) {
        String pkName = context.getPackageName();
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(pkName, 0);
            String path = appInfo.sourceDir;
            return path;
        } catch (PackageManager.NameNotFoundException e) {

        }

        return null;
    }
}
