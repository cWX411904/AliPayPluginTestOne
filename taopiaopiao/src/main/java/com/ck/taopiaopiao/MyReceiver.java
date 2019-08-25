package com.ck.taopiaopiao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.ck.paystander.PayInterfaceBroadcast;

public class MyReceiver extends BroadcastReceiver implements PayInterfaceBroadcast {
    @Override
    public void attach(Context context) {
        Toast.makeText(context, "---绑定广播上下文成功---》", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "---插件接收到广播---》", Toast.LENGTH_SHORT).show();

    }
}
