package core.colin.basic.utils.app;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Process;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class AppState {
    private static final String TAG = "AppState";

    public static boolean isServiceRunning(Context context, String className) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (serviceList == null || serviceList.isEmpty()) {
            return false;
        }

        for (ActivityManager.RunningServiceInfo runningServiceInfo : serviceList) {
            if (runningServiceInfo.service.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isActivityRunning(Context context, String packageName, String className) {
        List<ActivityManager.RunningTaskInfo> runningTasks = ((ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE))
                .getRunningTasks(100);
        if (runningTasks == null || runningTasks.isEmpty()) {
            return false;
        }
        for (ActivityManager.RunningTaskInfo next : runningTasks) {
            if (next.topActivity.getPackageName().equals(packageName)) {
                if (next.topActivity.getClassName().equals(className)) {
                    return true;
                }
            } else if (next.baseActivity.getPackageName().equals(packageName) &&
                    next.baseActivity.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isTopActivy(Context context, String packageName, String className) {
        List<ActivityManager.RunningTaskInfo> runningTasks = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE))
                .getRunningTasks(1);
        if (runningTasks != null && !runningTasks.isEmpty()) {
            ComponentName componentName = runningTasks.get(0).topActivity;
            if (!componentName.getPackageName().equals(packageName) || !componentName.getClassName().equals(className)) {
                return false;
            }
            return true;
        }
        return false;
    }

    public static boolean isUIProcess(Context context) {
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = ((ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE))
                .getRunningAppProcesses();
        String packageName = context.getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo next : runningAppProcesses) {
            if (next.pid == myPid && next.processName.startsWith(packageName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isRunning(Context context) {
        return isAppRunning(context, context.getApplicationContext().getPackageName());
    }

    public static boolean isAppRunning(Context context, String packageName) {
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = ((ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses();
        if (runningAppProcesses == null) {
            return false;
        }
//        Log.d("AppState", "process size: " + runningAppProcesses.size());
        for (ActivityManager.RunningAppProcessInfo info : runningAppProcesses) {
//            Log.d("AppState", "processName: " + info.processName);
            if (info.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isRunBackground(Context context) {
        return isAppRunBackground(context, context.getApplicationContext().getPackageName());
    }

    public static boolean isAppRunBackground(Context context, String str) {
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = ((ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses();
        if (runningAppProcesses == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo next : runningAppProcesses) {
            if (next.processName.startsWith(str) && next.importance == 400) {
                return true;
            }
        }
        return false;
    }

    public static boolean isRunForeground(Context context) {
        return isAppRunForeground(context, context.getApplicationContext().getPackageName());
    }

    public static boolean isAppRunForeground(Context context, String packageName) {
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = ((ActivityManager) context.getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses();
        if (runningAppProcesses == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo next : runningAppProcesses) {
            if (next.processName.startsWith(packageName) && next.importance == 100) {
                return true;
            }
        }
        return false;
    }

    public static boolean appIsExist(Context context, String packageName) {
        if ("".equals(packageName.trim())) {
            return false;
        }
        for (PackageInfo packageInfo : context.getPackageManager().getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES)) {
            if (packageInfo.packageName.startsWith(packageName)) {
                return true;
            }
        }
        return false;
    }

    public static void setToTop(Context context) {
        setAppToTop(context, context.getApplicationContext().getPackageName());
    }

    @SuppressLint("MissingPermission")
    public static void setAppToTop(Context context, String packageName) {
        if (!isAppRunForeground(context, packageName)) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningTaskInfo next : activityManager.getRunningTasks(100)) {
                if (next.topActivity.getPackageName().startsWith(packageName)) {
                    activityManager.moveTaskToFront(next.id, 0);
                    return;
                }
            }
        }
    }

    public static boolean isLauncherRunnig(Context context) {
        return isLauncherRunnig(context, context.getApplicationContext().getPackageName());
    }

    public static boolean isLauncherRunnig(Context context, String packageName) {
        List<String> allTheLauncherPackageName = getAllTheLauncherPackageName(context);
        Iterator<ActivityManager.RunningAppProcessInfo> it = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE))
                .getRunningAppProcesses().iterator();
        while (true) {
            if (!it.hasNext()) {
                return false;
            }
            ActivityManager.RunningAppProcessInfo next = it.next();
            if (next.importance == 100) {
                for (int i = 0; i < allTheLauncherPackageName.size(); i++) {
                    if (allTheLauncherPackageName.get(i).equals(next.processName)
                            && next.processName.startsWith(packageName)) {
                        return true;
                    }
                }
                continue;
            }
        }
    }

    public static List<String> getAllTheLauncherPackageName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 0);
        List<String> arrayList = queryIntentActivities.size() != 0 ? new ArrayList<>() : null;
        for (int i = 0; i < queryIntentActivities.size(); i++) {
            arrayList.add(queryIntentActivities.get(i).activityInfo.packageName);
        }
        return arrayList;
    }

    @SuppressLint("MissingPermission")
    public static void killBackgroundProcesses(Context context, String str) {
        ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE))
                .killBackgroundProcesses(str);
    }

    public static void killBackgroundProcessesFromPid(Context context, String packageName) {
        for (ActivityManager.RunningAppProcessInfo next : ((ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses()) {
            if (next.processName.startsWith(packageName)) {
                Process.killProcess(next.pid);
            }
        }
    }

    public static void forceStopPackage(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo next : activityManager.getRunningAppProcesses()) {
            if (next.processName.startsWith(packageName)) {
//                Log.i(TAG, "kill----> " + next.processName);
                try {
                    Class.forName("android.app.ActivityManager")
                            .getMethod("forceStopPackage", new Class[]{String.class})
                            .invoke(activityManager, new Object[]{packageName});
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e2) {
                    e2.printStackTrace();
                } catch (InvocationTargetException e3) {
                    e3.printStackTrace();
                } catch (ClassNotFoundException e4) {
                    e4.printStackTrace();
                }
            }
        }
    }

}
