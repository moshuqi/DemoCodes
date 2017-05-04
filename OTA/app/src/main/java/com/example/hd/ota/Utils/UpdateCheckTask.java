package com.example.hd.ota.Utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by HD on 17/3/29.
 */

public class UpdateCheckTask extends AsyncTask<Void, Void, String> {

    private final String TAG = "UpdateCheckTask";
    Context mContext;
    OnCheckListener mListener = null;

    public UpdateCheckTask(Context context, OnCheckListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        Log.i(TAG, "onPreExecute()");
        if (mListener != null) {
            mListener.preCheck();
        }
    }


    @Override
    protected void onPostExecute(String result) {
        Log.i(TAG, "onPostExecute()");
        UpdateInfo info = parseJson(result);
        if (this.mListener != null) {
            this.mListener.onSuccess(info);
        }
    }

    @Override
    protected String doInBackground(Void... voids) {
        HttpURLConnection uRLConnection = null;
        InputStream is = null;
        BufferedReader buffer = null;
        String result = null;
        String urlStr = Constants.UPDATE_REQUEST_URL + "?" + Constants.APK_VERSION_NAME + "=" + InfoUtils.getVersionName(this.mContext);

        try {
            URL url = new URL(urlStr);
            uRLConnection = (HttpURLConnection) url.openConnection();
            uRLConnection.setRequestMethod("GET");

            is = uRLConnection.getInputStream();
            buffer = new BufferedReader(new InputStreamReader(is));
            StringBuilder strBuilder = new StringBuilder();
            String line;
            while ((line = buffer.readLine()) != null) {
                strBuilder.append(line);
            }
            result = strBuilder.toString();
        } catch (Exception e) {
            Log.e(TAG, "http post error");
        } finally {
            if (buffer != null) {
                try {
                    buffer.close();
                } catch (IOException ignored) {

                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {

                }
            }
            if (uRLConnection != null) {
                uRLConnection.disconnect();
            }
        }

        if (result != null) {
            Log.i(TAG, result);
        }

        return result;
    }

    private UpdateInfo parseJson(String result) {
        UpdateInfo info = null;
        try {

            JSONObject obj = new JSONObject(result);
            String updateMessage = obj.getString(Constants.APK_UPDATE_CONTENT);
            String apkUrl = obj.getString(Constants.APK_DOWNLOAD_URL);
            String versionName = obj.getString(Constants.APK_VERSION_NAME);
            String md5 = obj.getString(Constants.APK_MD5);
            boolean diffUpdate = obj.getBoolean(Constants.APK_DIFF_UPDATE);

            String current = InfoUtils.getVersionName(mContext);
            if (!current.equals(versionName)) {
                info = new UpdateInfo(updateMessage, apkUrl, versionName, md5, diffUpdate);
            }

        } catch (JSONException e) {
            Log.e(TAG, "parse json error");
        }

        return info;
    }

    public interface OnCheckListener {
        void preCheck();
        void onSuccess(UpdateInfo info);
        void onFailed();
    }

    public class UpdateInfo {

        private String mMessage;
        private String mUrl;
        private String mVersionName;
        private String mMD5;
        private boolean mDiffUpdate;

        UpdateInfo(String msg, String url, String vn, String md5, boolean b) {
            this.mMessage = msg;
            this.mUrl = url;
            this.mVersionName = vn;
            this.mMD5 = md5;
            this.mDiffUpdate = b;
        }

        public String getMessage() {
            return mMessage;
        }

        public String getUrl() {
            return mUrl;
        }

        public String getVersionName() {
            return mVersionName;
        }

        public String getMD5() {
            return mMD5;
        }

        public boolean isDiffUpdate() {
            return mDiffUpdate;
        }

    }
}
