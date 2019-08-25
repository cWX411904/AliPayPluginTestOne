package com.ck.aliplugintestone;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ck.paystander.PayInterfaceActivity;

import java.lang.reflect.Constructor;

public class ProxyActivity extends Activity {

    //需要加载淘票票的全类名
    //com.ck.taopiaopiao.MainActivity
    private String className;

    PayInterfaceActivity payInterfaceActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        className = getIntent().getStringExtra("className");

        try {
            Class<?> activityClass = getClassLoader().loadClass(className);
            //实例化一个Activity
            Constructor<?> constructor = activityClass.getConstructor(new Class[]{});
            Object instance = constructor.newInstance(new Object[]{});

            /**
             * 第30行的instance，就是插件中的Activity，当然可以通过反射来调用插件Activity的onCreate之类的方法，
             * 但是这样会有性能上的损耗，
             * 前面我们已经提到过插件的所有Activity已经实现了支付宝定义的一套标准，所以这里我们可以通过强转，
             * 来先获取上层的payInterfaceActivity
             */
            payInterfaceActivity = (PayInterfaceActivity) instance;

            payInterfaceActivity.attach(this);
            /**
             * 这里可以通过bundle来传递宿主的数据到插件中去
             */
            Bundle bundle = new Bundle();
            payInterfaceActivity.onCreate(bundle);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startActivity(Intent intent) {
        String className1 = intent.getStringExtra("className");
        Intent intent1 = new Intent(this, ProxyActivity.class);
        intent1.putExtra("className", className1);
        super.startActivity(intent1);
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        IntentFilter newInterFilter = new IntentFilter();
        for (int i = 0; i < filter.countActions(); i++) {
            newInterFilter.addAction(filter.getAction(i));
        }
        return super.registerReceiver(new ProxyBroadCast(receiver.getClass().getName(), this), newInterFilter);
    }

    @Override
    public ClassLoader getClassLoader() {
        return PluginManager.getInstance().getDexClassLoader();
    }

    @Override
    public Resources getResources() {
        return PluginManager.getInstance().getResources();
    }

    @Override
    protected void onStart() {
        super.onStart();
        /**
         * 给插件的Activity赋值"灵魂"，让插件的Activity有生命周期
         */
        payInterfaceActivity.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**
         * 给插件的Activity赋值"灵魂"，让插件的Activity有生命周期
         */
        payInterfaceActivity.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        /**
         * 给插件的Activity赋值"灵魂"，让插件的Activity有生命周期
         */
        payInterfaceActivity.onPause();
    }
}
