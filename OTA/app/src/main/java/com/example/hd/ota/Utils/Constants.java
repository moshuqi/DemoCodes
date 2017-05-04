package com.example.hd.ota.Utils;

/**
 * Created by HD on 17/3/29.
 */

public class Constants {

    public static final String APK_DOWNLOAD_URL = "url";
    public static final String APK_UPDATE_CONTENT = "updateMessage";
    public static final String APK_VERSION_NAME = "versionName";
    public static final String APK_MD5 = "md5";
    public static final String APK_DIFF_UPDATE = "diffUpdate";

    public static final String OTA_SERVER_IP = "http://192.168.1.137:3000";
    public static final String UPDATE_REQUEST_URL = OTA_SERVER_IP + "/update_info";
    public static final String UPDATE_DOWNLOAD_PROGRESS = "progress";
}
