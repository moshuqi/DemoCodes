package com.example.hd.ota.Service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.cundong.utils.PatchUtils;
import com.example.hd.ota.Activity.UpdateActivity;
import com.example.hd.ota.R;
import com.example.hd.ota.Utils.Constants;
import com.example.hd.ota.Utils.InfoUtils;
import com.example.hd.ota.Utils.SignUtils;
import com.example.hd.ota.Utils.StorageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadService extends IntentService {

    private static final int BUFFER_SIZE = 1024 * 1024; // 8k ~ 32K
    private static final String TAG = "DownloadService";

    private static final int NOTIFICATION_ID = 0;

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

//        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        mBuilder = new NotificationCompat.Builder(this);
//
//        String appName = getString(getApplicationInfo().labelRes);
//        int icon = getApplicationInfo().icon;
//
//        mBuilder.setContentTitle(appName).setSmallIcon(icon);
        String urlStr = Constants.OTA_SERVER_IP + intent.getStringExtra(Constants.APK_DOWNLOAD_URL);
        String md5 = intent.getStringExtra(Constants.APK_MD5);
        boolean isDiff = intent.getBooleanExtra(Constants.APK_DIFF_UPDATE, false);
        InputStream in = null;
        FileOutputStream out = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(false);
            urlConnection.setConnectTimeout(10 * 1000);
            urlConnection.setReadTimeout(10 * 1000);
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Charset", "UTF-8");
            urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");

            urlConnection.connect();
            long bytetotal = urlConnection.getContentLength();
            long bytesum = 0;
            int byteread = 0;
            in = urlConnection.getInputStream();
            File dir = StorageUtils.getCacheDirectory(this);

            String downloadName = urlStr.substring(urlStr.lastIndexOf("/") + 1, urlStr.length());
            File downloadFile = new File(dir, downloadName);
            out = new FileOutputStream(downloadFile);
            byte[] buffer = new byte[BUFFER_SIZE];

            int oldProgress = 0;
            Intent sendIntent = new Intent(UpdateActivity.SERVICE_RECEIVER);
            while ((byteread = in.read(buffer)) != -1) {
                bytesum += byteread;
                out.write(buffer, 0, byteread);

                int progress = (int) (bytesum * 100L / bytetotal);
                // 如果进度与之前进度相等，则不更新，如果更新太频繁，否则会造成界面卡顿
                if (progress != oldProgress) {
//                    updateProgress(progress);
                    sendIntent.putExtra(Constants.UPDATE_DOWNLOAD_PROGRESS, progress);
                    getApplicationContext().sendBroadcast(sendIntent);
                }
                oldProgress = progress;
            }
            // 下载完成
            Log.i(TAG, "Download finished!!..");

            File apkFile = downloadFile;
            if (isDiff) {
                // 增量式升级，先将patch合成新apk
                String oldApkPath = InfoUtils.getBaseApkPath(getApplicationContext());
                String newApkName = "update.apk";
                String newApkPath = dir.getPath() + "/" + newApkName;
                String patchPath = downloadFile.getPath();

                Log.i(TAG, "MD5:");
                Log.i(TAG, "old apk md5: " + SignUtils.getMd5ByFile(new File(oldApkPath)));
                Log.i(TAG, "new apk md5: " + SignUtils.getMd5ByFile(new File(newApkPath)));
                Log.i(TAG, "patch md5: " + SignUtils.getMd5ByFile(new File(patchPath)));

                Log.i(TAG, "Patch diff...");
                int patchResult = PatchUtils.patch(oldApkPath, newApkPath, patchPath);
                if (patchResult == 0) {
                    apkFile = new File(newApkPath);
                }
            }

            installAPk(apkFile);
//
//            mNotifyManager.cancel(NOTIFICATION_ID);

        } catch (Exception e) {
            Log.e(TAG, "download apk file error");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignored) {

                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {

                }
            }
        }
    }

    private void updateProgress(int progress) {
        //"正在下载:" + progress + "%"
        mBuilder.setContentText(this.getString(R.string.android_auto_update_download_progress, progress)).setProgress(100, progress, false);
        //setContentInent如果不设置在4.0+上没有问题，在4.0以下会报异常
        PendingIntent pendingintent = PendingIntent.getActivity(this, 0, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(pendingintent);
        mNotifyManager.notify(NOTIFICATION_ID, mBuilder.build());
    }


    private void installAPk(File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //如果没有设置SDCard写权限，或者没有sdcard,apk文件保存在内存中，需要授予权限才能安装
        try {
            String[] command = {"chmod", "777", apkFile.toString()};
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.start();
        } catch (IOException ignored) {
        }
        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    static {
        System.loadLibrary("ApkPatchLibrary");
    }


}
