package core.colin.basic.utils.app;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.blankj.utilcode.util.ShellUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import core.colin.basic.utils.DangerousUtils;

public class PackageUtils {
    private static final String TAG = "PackageUtils";

    /**
     * App installation location settings values
     */
    public static final int APP_INSTALL_AUTO = 0;

    public static final int APP_INSTALL_INTERNAL = 1;

    public static final int APP_INSTALL_EXTERNAL = 2;

    /**
     * @param context
     * @param filePath
     * @return
     */
    public static final void install(Context context, String filePath) {
        if (PackageUtils.isSystemApplication(context) || DangerousUtils.isDeviceRooted()) {
            installSilent(context, filePath);
        }
        installNormalApp(context, filePath, "");
    }


    /**
     * 普通方式安装app
     *
     * @param context
     * @param filePath
     * @param authority
     */
    public static void installNormalApp(Context context, String filePath, String authority) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            isntallNormalO(context, filePath, authority);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            installNormalN(context, filePath, authority);
        } else {
            installNormal(context, filePath);
        }
    }

    /**
     * android 6.0以下安装apk
     *
     * @param context
     * @param filePath file path of package
     * @return whether apk exist
     */
    public static boolean installNormal(Context context, String filePath) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            return false;
        }

        i.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        return true;
    }

    /**
     * android7.x 安裝apk
     */
    public static void installNormalN(Context context, String filePath, String authority) {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            return;
        }
        //参数1 上下文, 参数2 在AndroidManifest中的android:authorities值, 参数3  共享的文件
        Uri apkUri = FileProvider.getUriForFile(context, authority, file);
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        install.setDataAndType(apkUri, "application/vnd.android.package-archive");
        install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(install);
    }

    /**
     * android8.x
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void isntallNormalO(Context context, String filePath, String authority) {
        //安装未知来源应用的权限
        boolean isGranted = context.getPackageManager().canRequestPackageInstalls();
        if (isGranted) { //安装应用的逻辑(写自己的就可以)
            installNormalN(context, filePath, authority);
        } else {
            //这个权限不是运行时权限, 需要告知用户 "安装应用需要打开未知来源权限，请去设置中开启权限"
        }
    }

    /**
     * 静默安装Apk，应用需要权限
     */
    public static void installApkSilent(Context context, String packageName, File file) {
        try {
            Class packagemanage = Class.forName("android.content.pm.PackageManager");

            Class packageInstallObserver = Class.forName("android.content.pm.IPackageInstallObserver");

            Method installPackage = packagemanage.getMethod("installPackage", Uri.class,
                    packageInstallObserver, int.class, String.class);
            //0x00000002
            int INSTALL_REPLACE_EXISTING = packagemanage.getField("INSTALL_REPLACE_EXISTING")
                    .getInt(null);
            Object iActivityManager = installPackage.invoke(context.getPackageManager(),
                    Uri.fromFile(file), null, INSTALL_REPLACE_EXISTING, packageName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * install package silent by root
     * <ul>
     * <strong>Attentions:</strong>
     * <li>Don't call this on the ui thread, it may costs some times.</li>
     * <li>You should add <strong>android.permission.INSTALL_PACKAGES</strong> in manifest, so no
     * need to request root permission, if you are system app.</li>
     * <li>Default pm install params is "-r".</li>
     * </ul>
     *
     * @param context
     * @param filePath file path of package
     * @see #installSilent(Context, String, String)
     */
    public static boolean installSilent(Context context, String filePath) {
        return installSilent(context, filePath, " -r ");
    }

    /**
     * install package silent by root
     * <ul>
     * <strong>Attentions:</strong>
     * <li>Don't call this on the ui thread, it may costs some times.</li>
     * <li>You should add <strong>android.permission.INSTALL_PACKAGES</strong> in manifest, so no
     * need to request root permission, if you are system app.</li>
     * </ul>
     */
    public static boolean installSilent(Context context, String filePath, String pmParams) {
        if (filePath == null || filePath.length() == 0) {
            return false;
        }

        File file = new File(filePath);
        if (file.length() <= 0 || !file.exists() || !file.isFile()) {
            return false;
        }

        /**
         * if context is system app, don't need root permission, but should add <uses-permission
         * android:name="android.permission.INSTALL_PACKAGES" /> in mainfest
         **/
        StringBuilder command = new StringBuilder()
                .append("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install ")
                .append(pmParams == null ? "" : pmParams).append(" ")
                .append(filePath.replace(" ", "\\ "));

        String chmodCmd = "chmod 777 " + filePath.replace(" ", "\\ ");

       ShellUtils.CommandResult commandResult = ShellUtils.execCmd(new String[]{command.toString(),
                chmodCmd}, !isSystemApplication(context), true);
        Log.d("", "command :" + command.toString() + ", commandResult :" + commandResult);

        if (commandResult.successMsg != null
                && (commandResult.successMsg.contains("Success") || commandResult.successMsg
                .contains("packetSuccess"))) {
            return true;
        }
        return false;
    }

    /**
     * uninstall according conditions
     * <ul>
     * <li>if system application or rooted, see {@link #uninstallSilent(Context, String)}</li>
     * <li>else see {@link #uninstallNormal(Context, String)}</li>
     * </ul>
     *
     * @param context
     * @param packageName package name of app
     * @return
     */
    public static final boolean uninstall(Context context, String packageName) {
        if (PackageUtils.isSystemApplication(context) || DangerousUtils.isDeviceRooted()) {
            return uninstallSilent(context, packageName);
        }
        return uninstallNormal(context, packageName);
    }

    /**
     * uninstall package normal by system intent
     *
     * @param context
     * @param packageName package name of app
     * @return whether package name is empty
     */
    public static boolean uninstallNormal(Context context, String packageName) {
        if (packageName == null || packageName.length() == 0) {
            return false;
        }

        Intent i = new Intent(Intent.ACTION_DELETE, Uri.parse(new StringBuilder(32)
                .append("package:").append(packageName).toString()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        return true;
    }

    /**
     * uninstall package and clear data of app silent by root
     *
     * @param context
     * @param packageName package name of app
     * @return
     * @see #uninstallSilent(Context, String, boolean)
     */
    public static boolean uninstallSilent(Context context, String packageName) {
        return uninstallSilent(context, packageName, true);
    }

    /**
     * uninstall package silent by root
     * <ul>
     * <strong>Attentions:</strong>
     * <li>Don't call this on the ui thread, it may costs some times.</li>
     * <li>You should add <strong>android.permission.DELETE_PACKAGES</strong> in manifest, so no
     * need to request root permission, if you are system app.</li>
     * </ul>
     *
     * @param context     file path of package
     * @param packageName package name of app
     * @param isKeepData  whether keep the data and cache directories around after package removal
     * @return <ul>
     */
    public static boolean uninstallSilent(Context context, String packageName, boolean isKeepData) {
        if (packageName == null || packageName.length() == 0) {
            return false;
        }

        /**
         * if context is system app, don't need root permission, but should add <uses-permission
         * android:name="android.permission.DELETE_PACKAGES" /> in mainfest
         **/
        StringBuilder command = new StringBuilder()
                .append("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm uninstall")
                .append(isKeepData ? " -k " : " ").append(packageName.replace(" ", "\\ "));
        ShellUtils.CommandResult commandResult = ShellUtils.execCmd(command.toString(),
                !isSystemApplication(context), true);
        Log.d(TAG, "command :" + command.toString() + ", commandResult :" + commandResult);
        if (commandResult.successMsg != null
                && (commandResult.successMsg.contains("Success") || commandResult.successMsg
                .contains("packetSuccess"))) {
            return true;
        }
        return false;
    }

    /**
     * whether context is system application
     *
     * @param context
     * @return
     */
    public static boolean isSystemApplication(Context context) {
        return isSystemApplication(context, context.getPackageName());
    }

    /**
     * whether packageName is system application
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isSystemApplication(Context context, String packageName) {
        if (context == null) {
            return false;
        }
        try {
            ApplicationInfo app = context.getPackageManager().getApplicationInfo(packageName, 0);
            return (app != null && (app.flags & ApplicationInfo.FLAG_SYSTEM) > 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * whether context is system update application
     *
     * @param context
     * @return
     */
    public static boolean isSystemUpdateApplication(Context context) {
        return isSystemUpdateApplication(context, context.getPackageName());
    }

    /**
     * whether packageName is system update application
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isSystemUpdateApplication(Context context, String packageName) {
        if (context == null) {
            return false;
        }
        try {
            ApplicationInfo app = context.getPackageManager().getApplicationInfo(packageName, 0);
            return (app != null && (app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) > 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * get system install location<br/>
     * can be set by System Menu Setting->Storage->Prefered install location
     *
     * @return
     */
    public static int getInstallLocation() {
        ShellUtils.CommandResult commandResult = ShellUtils.execCmd(
                "LD_LIBRARY_PATH=/vendor/lib:/system/lib pm get-install-location", false, true);
        Log.d(TAG, "pm get-install-location commandResult: " + commandResult.toString());
        if (commandResult.result == 0 && commandResult.successMsg != null
                && commandResult.successMsg.length() > 0) {
            try {
                int location = Integer.parseInt(commandResult.successMsg.substring(0, 1));
                switch (location) {
                    case APP_INSTALL_INTERNAL:
                        return APP_INSTALL_INTERNAL;
                    case APP_INSTALL_EXTERNAL:
                        return APP_INSTALL_EXTERNAL;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Log.d(TAG, "pm get-install-location error");
            }
        }
        return APP_INSTALL_AUTO;
    }


    /**
     * packetStart InstalledAppDetails Activity
     *
     * @param context
     * @param packageName
     */
    public static void startInstalledAppDetails(Context context, String packageName) {
        Intent intent = new Intent();
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= 9) {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", packageName, null));
        } else {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra((sdkVersion == 8 ? "pkg" : "com.android.settings.ApplicationPkgName"),
                    packageName);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 判断系统是否存在该应用
     *
     * @param context
     * @param packName
     * @return
     */
    public static boolean isExistApp(Context context, String packName) {
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if (packageInfo.packageName.equals(packName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获得属于桌面的应用的应用包名称
     *
     * @return 返回包含所有包名的字符串列表
     */
    public static List<String> getHomes(Context context) {
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = context.getPackageManager();
        // 属性
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfo) {
            names.add(ri.activityInfo.packageName);
        }
        return names;
    }

    /**
     * 判断当前应用是否是home
     *
     * @param context
     * @return
     */
    public static boolean isHome(Context context) {
        return isHome(context, context.getPackageName());
    }

    /**
     * 判断该应用是否是home
     *
     * @param context
     * @return
     */
    public static boolean isHome(Context context, String pkgName) {
        List<String> homePackageNames = getHomes(context);
        if (homePackageNames == null && homePackageNames.isEmpty()) {
            return false;
        } else {
            return homePackageNames.contains(pkgName);
        }
    }

    /**
     * 判断当前界面是否是桌面
     */
    public static boolean isInHome(Context context) {
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
        List<String> homePackageNames = getHomes(context);
        if (homePackageNames == null || homePackageNames.isEmpty()) {
            return false;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return homePackageNames.contains(rti.get(0).topActivity.getPackageName());
            }
        }

        return false;
    }

    /**
     * 启动应用
     *
     * @param context
     * @param packageName
     */
    public static Intent runApp(Context context, String packageName) {
        PackageInfo pi;
        try {
            pi = context.getPackageManager().getPackageInfo(packageName, 0);
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            // resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            resolveIntent.setPackage(pi.packageName);
            PackageManager pManager = context.getPackageManager();
            List<ResolveInfo> apps = pManager.queryIntentActivities(resolveIntent, 0);

            if (apps != null) {
                Iterator<ResolveInfo> iterator = apps.iterator();
                if (iterator.hasNext()) {
                    ResolveInfo ri = iterator.next();
                    if (ri != null) {
                        packageName = ri.activityInfo.packageName;
                        String className = ri.activityInfo.name;

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        // intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        ComponentName cn = new ComponentName(packageName, className);
                        intent.setComponent(cn);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        return intent;
                    }
                }
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 是否允许安装第三方应用
     *
     * @return
     */
    public static boolean isInstallingUnknownAppsAllowed(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(),
                Settings.Secure.INSTALL_NON_MARKET_APPS, 0) > 0;
    }
}
