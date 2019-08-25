package com.ck.taopiaopiao;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.img).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(that, "---->", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(that, SecondActivity.class));

//                        IntentFilter intentFilter = new IntentFilter();
//                        intentFilter.addAction("com.ck.taopiaopiao.MainActivity");
//                        registerReceiver(new MyReceiver(), intentFilter);
                    }
                }
        );

//        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setAction("com.ck.taopiaopiao.MainActivity");
//                sendBroadcast(intent);
//            }
//        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
