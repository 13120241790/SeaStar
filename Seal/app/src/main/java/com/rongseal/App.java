package com.rongseal;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.bugtags.library.Bugtags;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.rongseal.message.AgreedFriendRequestMessage;
import com.rongseal.message.ContactNotificationMessageProvider;
import com.sd.core.utils.NLog;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot1.event.EventBus;
import io.rong.imkit.RongIM;

/**
 * Created by AMing on 15/11/1.
 * Company RongCloud
 */
public class App extends Application {

    private static final String BUGTAGS_APPKEY = "9b333294d099c492a26d8fe33171bd1e";

    private static DisplayImageOptions options;

    @Override
    public void onCreate() {
        super.onCreate();
        // rongcloud 初始化
        RongIM.init(this);
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {
            RongCloudEvent.init(this);
            RongIM.registerMessageType(AgreedFriendRequestMessage.class);
            RongIM.registerMessageTemplate(new ContactNotificationMessageProvider());
        }
        // BugTags 初始化
        Bugtags.start(BUGTAGS_APPKEY, this, Bugtags.BTGInvocationEventBubble);
        //友盟session时间间隔
        MobclickAgent.setSessionContinueMillis(30 * 60 * 1000);

        NLog.setDebug(true); //oneCore 打印参数


        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.rp_default_head)
                .showImageOnFail(R.drawable.rp_default_head)
                .showImageOnLoading(R.drawable.rp_default_head)
                .displayer(new FadeInBitmapDisplayer(300))
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        //初始化图片下载组件
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(200)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .defaultDisplayImageOptions(options)
                .build();

        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }


    /**
     * 获取当前进程 name
     *
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

    public static DisplayImageOptions getOptions() {
        return options;
    }

    public static void setOptions(DisplayImageOptions options) {
        App.options = options;
    }
}
