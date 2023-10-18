package core.colin.basic.utils.app;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresPermission;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;

import com.blankj.utilcode.util.RomUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * 一些系统的Intent跳转
 */
public class IntentUtils {
    private static final String TAG = "IntentUtils";

    public static class PackageName {
        public static final String QQ_LITE = "com.tencent.qqlite";
        public static final String QQ = "com.tencent.mobileqq";
        public static final String WECHAT = "com.tencent.mm";
        public static final String GOOGLE_STORE = "com.android.vending";
        public static final String UNION_PAY = "com.unionpay";
    }

    public static Uri getFileUri(File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String authority = Utils.getApp().getPackageName() + ".utilcode.fileprovider";
            Uri contentUri = FileProvider.getUriForFile(Utils.getApp(), authority, file);
            // 授权给微信访问路径 com.tencent.mm
            Utils.getApp().grantUriPermission(core.colin.basic.utils.Utils.getAppContext().getPackageName(),
                    contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            return contentUri;

        }
        return Uri.fromFile(file);
    }


    public static String getUri(int resId) {
        Resources r = Utils.getApp().getResources();
        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + r.getResourcePackageName(resId)
                + "/" + r.getResourceTypeName(resId) + "/"
                + r.getResourceEntryName(resId));
        return uri.toString();
    }

    public static String getUriFileName(String uri) {
        if (TextUtils.isEmpty(uri)) {
            return null;
        }
        if (!uri.contains("/")) {
            return uri;
        }
        if (uri.endsWith("/")) {
            return "";
        }
        return uri.substring(uri.lastIndexOf("/") + 1);
    }

    /**
     * 打开下载目录 Android 6.0 以上 打开子目录路径 ‘/’ 需要转义为 ‘%2F’
     *
     * @param context Context
     */
    public static void openDownloadDirectory(Context context, String path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String downloadPath = "content://com.android.externalstorage.documents/document/primary::Download";
            if (!StringUtils.isTrimEmpty(path)) {
                downloadPath = downloadPath + path.replace("/", "%2f");
            }
            Uri uri = Uri.parse(downloadPath);
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setType("*/*");
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
            context.startActivity(intent);
        }
    }


    /**
     * 检测是否安装支付宝
     *
     * @param context
     * @return
     */
    public static boolean isInstallAlipay(Context context) {
        Uri uri = Uri.parse("alipays://platformapi/startApp");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        ComponentName componentName = intent.resolveActivity(context.getPackageManager());
        return componentName != null;
    }

    /**
     * 判断 用户是否安装微信客户端
     */
    public static boolean isInstallWeixin(Context context) {
        List<PackageInfo> pinfo = context.getPackageManager().getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals(PackageName.WECHAT)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断 用户是否安装云闪付
     */
    public static boolean isInstallUnionPay(Context context) {
        List<PackageInfo> pinfo = context.getPackageManager().getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals(PackageName.UNION_PAY)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 判断 用户是否安装QQ客户端
     */
    public static boolean isInstallQQ(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equalsIgnoreCase(PackageName.QQ_LITE) || pn.equalsIgnoreCase(PackageName.QQ)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 调用系统拨号界面
     */
    public static void dialingPhone(Context context, String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        context.startActivity(intent);
    }

    /**
     * 调用系统拨号界面
     */
    @RequiresPermission(value = "android.permission.CALL_PHONE")
    public static void callPhone(Context context, String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        context.startActivity(intent);
    }

    /**
     * 应用设置
     */
    public static void goToSetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        context.startActivity(intent);
    }

    /**
     * 浏览器打开
     */
    public static void openBrowser(Context context, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取跳转到应用商店的 Intent
     *
     * @return 跳转到应用商店的 Intent
     */
    public static Intent getAppStoreIntent() {
        return getAppStoreIntent(Utils.getApp().getPackageName(), false);
    }

    /**
     * 获取跳转到应用商店的 Intent
     *
     * @param isIncludeGooglePlayStore 是否包括 Google Play 商店
     * @return 跳转到应用商店的 Intent
     */
    public static Intent getAppStoreIntent(boolean isIncludeGooglePlayStore) {
        return getAppStoreIntent(Utils.getApp().getPackageName(), isIncludeGooglePlayStore);
    }

    /**
     * 获取跳转到应用商店的 Intent
     *
     * @param packageName 包名
     * @return 跳转到应用商店的 Intent
     */
    public static Intent getAppStoreIntent(final String packageName) {
        return getAppStoreIntent(packageName, false);
    }

    /**
     * 获取跳转到应用商店的 Intent
     * <p>优先跳转到手机自带的应用市场</p>
     *
     * @param packageName              包名
     * @param isIncludeGooglePlayStore 是否包括 Google Play 商店
     * @return 跳转到应用商店的 Intent
     */
    public static Intent getAppStoreIntent(final String packageName, boolean isIncludeGooglePlayStore) {
        if (RomUtils.isSamsung()) {// 三星单独处理跳转三星市场
            Intent samsungAppStoreIntent = getSamsungAppStoreIntent(packageName);
            if (samsungAppStoreIntent != null) return samsungAppStoreIntent;
        }
        if (RomUtils.isLeeco()) {// 乐视单独处理跳转乐视市场
            Intent leecoAppStoreIntent = getLeecoAppStoreIntent(packageName);
            if (leecoAppStoreIntent != null) return leecoAppStoreIntent;
        }

        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent intent = new Intent();
        intent.setData(uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        List<ResolveInfo> resolveInfos = Utils.getApp().getPackageManager()
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (resolveInfos == null || resolveInfos.size() == 0) {
            Log.e(TAG, "No app store!");
            return null;
        }
        Intent googleIntent = null;
        for (ResolveInfo resolveInfo : resolveInfos) {
            String pkgName = resolveInfo.activityInfo.packageName;
            if (!PackageName.GOOGLE_STORE.equals(pkgName)) {
                if (isAppSystem(pkgName)) {
                    intent.setPackage(pkgName);
                    return intent;
                }
            } else {
                intent.setPackage(PackageName.GOOGLE_STORE);
                googleIntent = intent;
            }
        }
        if (isIncludeGooglePlayStore && googleIntent != null) {
            return googleIntent;
        }

        intent.setPackage(resolveInfos.get(0).activityInfo.packageName);
        return intent;
    }

    private static boolean go2NormalAppStore(String packageName) {
        Intent intent = getNormalAppStoreIntent();
        if (intent == null) return false;
        intent.setData(Uri.parse("market://details?id=" + packageName));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Utils.getApp().startActivity(intent);
        return true;
    }

    private static Intent getNormalAppStoreIntent() {
        Intent intent = new Intent();
        Uri uri = Uri.parse("market://details?id=" + Utils.getApp().getPackageName());
        intent.setData(uri);
        if (getAvailableIntentSize(intent) > 0) {
            return intent;
        }
        return null;
    }

    private static Intent getSamsungAppStoreIntent(final String packageName) {
        Intent intent = new Intent();
        intent.setClassName("com.sec.android.app.samsungapps", "com.sec.android.app.samsungapps.Main");
        intent.setData(Uri.parse("http://www.samsungapps.com/appquery/appDetail.as?appId=" + packageName));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (getAvailableIntentSize(intent) > 0) {
            return intent;
        }
        return null;
    }

    private static Intent getLeecoAppStoreIntent(final String packageName) {
        Intent intent = new Intent();
        intent.setClassName("com.letv.app.appstore", "com.letv.app.appstore.appmodule.details.DetailsActivity");
        intent.setAction("com.letv.app.appstore.appdetailactivity");
        intent.putExtra("packageName", packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (getAvailableIntentSize(intent) > 0) {
            return intent;
        }
        return null;
    }

    private static int getAvailableIntentSize(final Intent intent) {
        return Utils.getApp().getPackageManager()
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                .size();
    }

    private static boolean isAppSystem(final String packageName) {
        if (TextUtils.isEmpty(packageName)) return false;
        try {
            PackageManager pm = Utils.getApp().getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            return ai != null && (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 导入数据库.
     *
     * @param context the context
     * @param dbName  the db name
     * @param rawRes  the raw res
     * @return true, if successful
     */
    public static boolean importDatabase(Context context, String dbName, int rawRes) {
        int buffer_size = 1024;
        InputStream is = null;
        FileOutputStream fos = null;
        boolean flag = false;

        try {
            String dbPath = "/data/data/" + context.getPackageName() + "/databases/" + dbName;
            File dbfile = new File(dbPath);
            //判断数据库文件是否存在，若不存在则执行导入，否则直接打开数据库
            if (!dbfile.exists()) {
                //欲导入的数据库
                if (!dbfile.getParentFile().exists()) {
                    dbfile.getParentFile().mkdirs();
                }
                dbfile.createNewFile();
                is = context.getResources().openRawResource(rawRes);
                fos = new FileOutputStream(dbfile);
                byte[] buffer = new byte[buffer_size];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.flush();
            }
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
        }
        return flag;
    }

    /**
     * get an asset using ACCESS_STREAMING mode. This provides access to files that have been
     * bundled with an application as assets -- that is, files placed in to the "assets" directory.
     *
     * @param context
     * @param fileName The name of the asset to open. This name can be hierarchical.
     * @return
     */
    public static String geFileFromAssets(Context context, String fileName) {
        InputStream is = null;
        InputStreamReader in = null;
        BufferedReader br = null;
        StringBuilder s = new StringBuilder("");
        try {
            AssetManager assetManager = context.getResources().getAssets();
            is = assetManager.open(fileName);
            in = new InputStreamReader(is);
            br = new BufferedReader(in);
            String line;
            while ((line = br.readLine()) != null) {
                s.append(line);
            }
            return s.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (br != null) {
                    br.close();
                }

                if (in != null) {
                    in.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * get content from a raw resource. This can only be used with resources whose value is the name
     * of an asset files -- that is, it can be used to open drawable, sound, and raw resources; it
     * will packetFail on string and color resources.
     *
     * @param context
     * @param resId   The resource identifier to open, as generated by the appt tool.
     * @return
     */
    public static String geFileFromRaw(Context context, int resId) {
        if (context == null) {
            return null;
        }

        StringBuilder s = new StringBuilder();
        try {
            InputStreamReader in = new InputStreamReader(context.getResources().openRawResource(
                    resId));
            BufferedReader br = new BufferedReader(in);
            String line;
            while ((line = br.readLine()) != null) {
                s.append(line);
            }
            return s.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 隐式意图转换成显式意图
     *
     * @param context
     * @param implicitIntent
     * @return
     */
    public static Intent getExplicitIntent(Context context, Intent implicitIntent) {
        PackageManager pm = context.getPackageManager(); // 得到包管理器。

        // 返回給定条件下的ResolveInfo对象，本质上是service.
        List<ResolveInfo> info = pm.queryIntentServices(implicitIntent, 0);
        // 这个地方意图对象一次只能进来一个。
        if (info == null || info.size() != 1) {

            return null;
        }
        // 所得到的对象就是ServiceInfo.

        ResolveInfo resolveInfo = info.get(0);
        String packageName = resolveInfo.serviceInfo.packageName;

        String className = resolveInfo.serviceInfo.name;
        //通过Service的包名，和类名创建Component组件。
        ComponentName componentName = new ComponentName(packageName, className);
        Intent explicitIntent = new Intent(implicitIntent);
        explicitIntent.setComponent(componentName);
        return explicitIntent;
    }


    public static boolean isNotificationPermissionOpen(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return NotificationManagerCompat.from(context).getImportance() != NotificationManager.IMPORTANCE_NONE;
        }
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }

    public static void openNotificationPermissionSetting(Context context) {
        if (Build.VERSION.SDK_INT >= 26) {
            Intent intent = new Intent();

            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
            context.startActivity(intent);
            return;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent intent = new Intent();

            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
            context.startActivity(intent);
            return;
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent();

            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
            return;
        }

    }

    /**
     * whether the app whost package's name is packageName is on the top of the stack
     * <ul>
     * <strong>Attentions:</strong>
     * <li>You should add <strong>android.permission.GET_TASKS</strong> in manifest</li>
     * </ul>
     *
     * @param context
     * @param packageName
     * @return if params error or task stack is null, return null, otherwise retun whether the app
     * is on the top of stack
     */
    public static Boolean isTopActivity(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
        if (tasksInfo == null || tasksInfo.isEmpty()) {
            return null;
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return tasksInfo.get(0).topActivity.getPackageName().equals(packageName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }

    /**
     * 判断程序是否在前台运行
     *
     * @return
     */
    public static boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) Utils.getApp().getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return false;
        }
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            String packageName = Utils.getApp().getPackageName();
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.processName.equals(packageName) && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                }
            }
            return false;
        } else {
//            Android L开始，Google开始对getRunningTasks接口进行限制使用。
//            默认情况下，普通的三方应用（非系统应用不能使用该接口）
//            之前，使用该接口需要 android.permission.GET_TASKS
//            即使是自己开发的普通应用，只要声明该权限，即可以使用getRunningTasks接口。
//            但从L开始，这种方式以及废弃。
//            应用要使用该接口必须声明权限android.permission.REAL_GET_TASKS
//            而这个权限是不对三方应用开放的。（在Manifest里申请了也没有作用）
//            系统应用（有系统签名）可以调用该权限。
            ComponentName componentInfo = activityManager.getRunningTasks(1).get(0).topActivity;
            if (null != componentInfo && componentInfo.getPackageName().equals(Utils.getApp().getPackageName())) {
                return true;
            }
        }
        return false;
    }

}
