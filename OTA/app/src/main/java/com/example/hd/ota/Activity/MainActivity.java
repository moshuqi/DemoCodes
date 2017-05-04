package com.example.hd.ota.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.hd.ota.Activity.InfoActivity;
import com.example.hd.ota.Activity.UpdateActivity;
import com.example.hd.ota.R;
import com.example.hd.ota.Utils.InfoUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private ArrayList<String> mList;
    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView)findViewById(R.id.list);

        mList = new ArrayList<String>();
        mList.add("版本信息");
        mList.add("版本更新");

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mList);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                Intent intent = null;
                switch (position) {
                    case 0:
                        intent = InfoActivity.getInfoIntent(getApplicationContext());
                        break;

                    case 1:
                        intent = UpdateActivity.getUpdateIntent(getApplicationContext());
                        break;

                    default:
                }

                if (intent != null) {
                    startActivity(intent);
                }

                Log.i(TAG, "click on: " + String.valueOf(position));


                String versionName = InfoUtils.getVersionName(getApplicationContext());
                int versionCode = InfoUtils.getVersionCode(getApplicationContext());
                Log.i(TAG, "name = " + versionName + "; code = " + versionCode);
            }
        });
    }
}
