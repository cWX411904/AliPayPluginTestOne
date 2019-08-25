package com.ck.aliplugintestone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import dalvik.system.DexClassLoader;

public class PluginManager {

    private PackageInfo packageInfo;
    private Resources resources;
    private Context context;
    private DexClassLoader dexClassLoader;

    private static final PluginManager ourInstance = new PluginManager();

    public static PluginManager getInstance() {
        return ourInstance;
    }

    private PluginManager() {
    }

    public void loadPath(Context context) {
        File fileDir = context.getDir("plugin", Context.MODE_PRIVATE);
        String name = "pluginb.apk";
        String path = new File(fileDir, name).getAbsolutePath();

        PackageManager packageManager = context.getPackageManager();
        packageInfo = packageManager.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);

        File dexOutFile = context.getDir("dex", Context.MODE_PRIVATE);
        dexClassLoader = new DexClassLoader(path, dexOutFile.getAbsolutePath(), null,
                context.getClassLoader());


        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = AssetManager.class.getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, path);
            resources = new Resources(assetManager, context.getResources().getDisplayMetrics(),
                    context.getResources().getConfiguration());


        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        parseReceiver(context, path);

    }

    private void parseReceiver(Context context, String path) {
        //系统能将apk路径转换为package对象， 我们也能模拟系统的过程
        //将插件的apk路径转换为package
        try {
            //反射拿到PackageParser类
            Class<?> packageParserClass = Class.forName("android.content.pm.PackageParser");
            //反射获取parsePackage方法
            Method parsePackageMethod = packageParserClass.getDeclaredMethod("parsePackage", File.class, int.class);
            Object packageParser = packageParserClass.newInstance();
            //反射执行parsePackage方法
            Object packageObj = parsePackageMethod.invoke(packageParser, new File(path), PackageManager.GET_ACTIVITIES);
            //parsePackage这个方法的返回值就是Package，然后通过Package拿到成员变量receivers，源码中是个ArrayList
            Field receiverFiled = packageObj.getClass().getDeclaredField("receivers");
            //拿到广播集合，app存在多个广播集合
            List receivers = (List) receiverFiled.get(packageObj);

            //反射拿到PackageParser的内部类Component
            Class<?> componentClass = Class.forName("android.content.pm.PackageParser$Component");
            //拿到里面的成员对象
            Field intentsFiled = componentClass.getDeclaredField("intents");


            //反射拿到PackageParser内部类Activity
            Class<?> packageParser$ActivityClass = Class.forName("android.content.pm.PackageParser$Activity");
            //反射拿到PackageUserState类
            Class<?> packageUserStateClass = Class.forName("android.content.pm.PackageUserState");

            //反射拿到PackageParser的generateActivityInfo方法
            Method generateReceiverInfo = packageParserClass.getDeclaredMethod("generateActivityInfo",
                    packageParser$ActivityClass, int.class, packageUserStateClass, int.class);

            Object defaultUserState = packageUserStateClass.newInstance();

            Class<?> userHandler = Class.forName("android.os.UserHandle");
            Method getCallingUserIdMethod = userHandler.getDeclaredMethod("getCallingUserId");
            int userId = (int) getCallingUserIdMethod.invoke(null);

            for (Object activity : receivers) {
                ActivityInfo info= (ActivityInfo) generateReceiverInfo.invoke(packageParser,
                        activity,0, defaultUserState, userId);
                BroadcastReceiver broadcastReceiver = (BroadcastReceiver) dexClassLoader.loadClass(info.name).newInstance();
                List<? extends IntentFilter> intents = (List<? extends IntentFilter>) intentsFiled.get(activity);
                for (IntentFilter intentFilter : intents) {
                    context.registerReceiver(broadcastReceiver, intentFilter);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    /**
     * 对插件，生成插件的resource
     * @return
     */
    public Resources getResources() {
        return resources;
    }

    /**
     * 对插件，生成插件的classLoader
     * @return
     */
    public DexClassLoader getDexClassLoader() {
        return dexClassLoader;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
