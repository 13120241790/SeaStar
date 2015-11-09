package com.rupeng;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.bugtags.library.Bugtags;
import com.umeng.analytics.MobclickAgent;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

/**
 * Created by AMing on 15/11/1.
 * Company RongCloud
 */
public class App extends Application {

    private static final String BUGTAGS_APPKEY = "9b333294d099c492a26d8fe33171bd1e";

    @Override
    public void onCreate() {
        super.onCreate();
        // rongcloud 初始化
        RongIM.init(this);
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {
            RongCloudEvent.init(this);
        }
        // BugTags 初始化
        Bugtags.start(BUGTAGS_APPKEY, this, Bugtags.BTGInvocationEventBubble);
        //友盟session时间间隔
        MobclickAgent.setSessionContinueMillis(30 * 60 * 1000);
    }


    /**
     * 获取当前进程 name
     * @param context
     * @return
     */
    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}
