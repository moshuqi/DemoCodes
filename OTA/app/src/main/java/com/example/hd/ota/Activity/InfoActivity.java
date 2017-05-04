package com.example.hd.ota.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.hd.ota.R;
import com.example.hd.ota.Utils.InfoUtils;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        TextView versionTv = (TextView)findViewById(R.id.versionTV);
        String versionName = "当前版本：" + InfoUtils.getVersionName(getApplicationContext());
        versionTv.setText(versionName);

        TextView descTv = (TextView)findViewById(R.id.descTV);
        String desc = InfoUtils.getVersionDescription();
        descTv.setText(desc);
    }

    public static Intent getInfoIntent(Context context) {
        Intent infoIntent = new Intent(context, InfoActivity.class);
        return infoIntent;
    }
}
