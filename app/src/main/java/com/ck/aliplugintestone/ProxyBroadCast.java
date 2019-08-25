package com.ck.aliplugintestone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ck.paystander.PayInterfaceBroadcast;

import java.lang.reflect.Constructor;

public class ProxyBroadCast extends BroadcastReceiver {

    private String className;

    private PayInterfaceBroadcast payInterfaceBroadcast;

    public ProxyBroadCast(String name, Context context) {
        this.className = name;
        try {
            Class<?> loadClass = PluginManager.getInstance().getDexClassLoader().loadClass(className);
            Constructor<?> loadClassConstructor = loadClass.getConstructor(new Class[]{});
            Object instance = loadClassConstructor.newInstance(new Object[]{});
            payInterfaceBroadcast = (PayInterfaceBroadcast) instance;
            payInterfaceBroadcast.attach(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        payInterfaceBroadcast.onReceive(context, intent);
    }
}
