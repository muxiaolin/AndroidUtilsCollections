package core.colin.basic.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPStaticUtils;
import com.blankj.utilcode.util.SPUtils;


/**
 * 工具类初始化配置统一入口
 */
public class Utils {
    private static boolean sLogDebug;

    public static void init(@NonNull Application context, boolean debug) {
        init(context, debug, "CoreTag", context.getPackageName() + "_appPref");
    }

    public static void init(@NonNull Application context, boolean debug, String logTag, String spFileName) {
        sLogDebug = debug;
        com.blankj.utilcode.util.Utils.init(context);
        SPStaticUtils.setDefaultSPUtils(SPUtils.getInstance(spFileName));
        LogUtils.getConfig().setGlobalTag(logTag);
        LogUtils.getConfig().setStackDeep(2);
        LogUtils.getConfig().setLogSwitch(debug);
        LogUtils.getConfig().setLog2FileSwitch(false);
        LogUtils.getConfig().setSaveDays(2 * 30);
    }


    public static boolean isLogDebug() {
        return sLogDebug;
    }


    public static Context getAppContext() {
        return com.blankj.utilcode.util.Utils.getApp().getApplicationContext();
    }

    @Nullable
    public static Activity getActivity(@NonNull Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof ContextWrapper) {
            Context baseContext = ((ContextWrapper) context).getBaseContext();
            if (baseContext instanceof Activity) {
                return (Activity) baseContext;
            }
        }
        return null;
    }

    public static boolean assertValidRequest(Context context) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            return !isDestroy(activity);
        } else if (context instanceof ContextWrapper) {
            ContextWrapper contextWrapper = (ContextWrapper) context;
            if (contextWrapper.getBaseContext() instanceof Activity) {
                Activity activity = (Activity) contextWrapper.getBaseContext();
                return !isDestroy(activity);
            }
        }
        return true;
    }

    private static boolean isDestroy(Activity activity) {
        if (activity == null) {
            return true;
        }
        return activity.isFinishing() || activity.isDestroyed();
    }


}
