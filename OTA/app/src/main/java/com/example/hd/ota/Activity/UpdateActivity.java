package com.example.hd.ota.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.hd.ota.R;
import com.example.hd.ota.Service.DownloadService;
import com.example.hd.ota.Utils.Constants;
import com.example.hd.ota.Utils.UpdateCheckTask;

public class UpdateActivity extends AppCompatActivity implements UpdateCheckTask.OnCheckListener {

    private static final String TAG = "UpdateActivity";

    private LinearLayout mLayout;
    private TextView mTv1;
    private TextView mTv2;
    private Button mDownloadBtn;
    private ProgressBar mProgressBar;
    private TextView mTipTv;

    public static String SERVICE_RECEIVER = "com.example.hd.ota.receiver";
    private Intent mIntent;
    private ProgressReceiver mReceiver;
    private UpdateCheckTask.UpdateInfo mUpdateInfonfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        mLayout = (LinearLayout)findViewById(R.id.updateContent);
        mTv1 = (TextView)findViewById(R.id.textview1);
        mTv2 = (TextView)findViewById(R.id.textview2);

        mDownloadBtn = (Button)findViewById(R.id.downloadBtn);
        mDownloadBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (mUpdateInfonfo == null) {
                    return;
                }
                mIntent = new Intent(getApplicationContext(), DownloadService.class);
                mIntent.putExtra(Constants.APK_DOWNLOAD_URL, mUpdateInfonfo.getUrl());
                mIntent.putExtra(Constants.APK_MD5, mUpdateInfonfo.getMD5());
                mIntent.putExtra(Constants.APK_DIFF_UPDATE, mUpdateInfonfo.isDiffUpdate());
                getApplicationContext().startService(mIntent);

                mDownloadBtn.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
            }
        });

        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        mTipTv = (TextView)findViewById(R.id.tipTV);

        new UpdateCheckTask(UpdateActivity.this, this).execute();

        mReceiver = new ProgressReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SERVICE_RECEIVER);
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        if (mIntent != null) {
            stopService(mIntent);
        }

        unregisterReceiver(mReceiver);
        super.onDestroy();
    }


    public static Intent getUpdateIntent(Context context) {
        Intent updateIntent = new Intent(context, UpdateActivity.class);
        return updateIntent;
    }

    @Override
    public void onSuccess(UpdateCheckTask.UpdateInfo info) {
        if (info != null) {
            mTipTv.setVisibility(View.GONE);
            mLayout.setVisibility(View.VISIBLE);

            String title = getString(R.string.new_version) + info.getVersionName();
            mTv1.setText(title);
            mTv2.setText(info.getMessage());
            mUpdateInfonfo = info;
        }
        else {
            mTipTv.setVisibility(View.VISIBLE);
            mTipTv.setText(getString(R.string.no_update));
        }
    }

    @Override
    public void onFailed() {

    }

    @Override
    public void preCheck() {
        mTipTv.setVisibility(View.VISIBLE);
        mTipTv.setText(getString(R.string.checking_update));
    }

    public class ProgressReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int progress = intent.getIntExtra(Constants.UPDATE_DOWNLOAD_PROGRESS, 0);
            Log.i(TAG, "progress......" + progress);
            mProgressBar.setProgress(progress);

            if (mProgressBar.getVisibility() != View.VISIBLE) {
                mProgressBar.setVisibility(View.VISIBLE);
                mDownloadBtn.setVisibility(View.GONE);
            }
        }
    }
}
