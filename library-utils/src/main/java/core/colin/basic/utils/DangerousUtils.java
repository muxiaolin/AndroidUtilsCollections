package core.colin.basic.utils;

import static android.Manifest.permission.MODIFY_PHONE_STATE;
import static android.Manifest.permission.REBOOT;
import static android.Manifest.permission.SEND_SMS;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.annotation.RequiresPermission;

import com.blankj.utilcode.util.IntentUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ShellUtils;
import com.blankj.utilcode.util.Utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.List;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2019/06/29
 *     desc  :
 * </pre>
 */
public class DangerousUtils {


    private DangerousUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    ///////////////////////////////////////////////////////////////////////////
    // AppUtils
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Install the app silently.
     * <p>Without root permission must hold
     * {@code android:sharedUserId="android.uid.shell"} and
     * {@code <uses-permission android:name="android.permission.INSTALL_PACKAGES" />}</p>
     *
     * @param filePath The path of file.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean installAppSilent(final String filePath) {
        return installAppSilent(getFileByPath(filePath), null);
    }

    /**
     * Install the app silently.
     * <p>Without root permission must hold
     * {@code android:sharedUserId="android.uid.shell"} and
     * {@code <uses-permission android:name="android.permission.INSTALL_PACKAGES" />}</p>
     *
     * @param file The file.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean installAppSilent(final File file) {
        return installAppSilent(file, null);
    }


    /**
     * Install the app silently.
     * <p>Without root permission must hold
     * {@code android:sharedUserId="android.uid.shell"} and
     * {@code <uses-permission android:name="android.permission.INSTALL_PACKAGES" />}</p>
     *
     * @param filePath The path of file.
     * @param params   The params of installation(e.g.,<code>-r</code>, <code>-s</code>).
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean installAppSilent(final String filePath, final String params) {
        return installAppSilent(getFileByPath(filePath), params);
    }

    /**
     * Install the app silently.
     * <p>Without root permission must hold
     * {@code android:sharedUserId="android.uid.shell"} and
     * {@code <uses-permission android:name="android.permission.INSTALL_PACKAGES" />}</p>
     *
     * @param file   The file.
     * @param params The params of installation(e.g.,<code>-r</code>, <code>-s</code>).
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean installAppSilent(final File file, final String params) {
        return installAppSilent(file, params, isDeviceRooted());
    }

    /**
     * Install the app silently.
     * <p>Without root permission must hold
     * {@code android:sharedUserId="android.uid.shell"} and
     * {@code <uses-permission android:name="android.permission.INSTALL_PACKAGES" />}</p>
     *
     * @param file     The file.
     * @param params   The params of installation(e.g.,<code>-r</code>, <code>-s</code>).
     * @param isRooted True to use root, false otherwise.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean installAppSilent(final File file,
                                           final String params,
                                           final boolean isRooted) {
        if (!isFileExists(file)) return false;
        String filePath = '"' + file.getAbsolutePath() + '"';
        String command = "LD_LIBRARY_PATH=/vendor/lib*:/system/lib* pm install " +
                (params == null ? "" : params + " ")
                + filePath;
        ShellUtils.CommandResult commandResult = ShellUtils.execCmd(command, isRooted);
        if (commandResult.successMsg != null
                && commandResult.successMsg.toLowerCase().contains("success")) {
            return true;
        } else {
            LogUtils.e("installAppSilent successMsg: " + commandResult.successMsg +
                    ", errorMsg: " + commandResult.errorMsg);
            return false;
        }
    }

    /**
     * Uninstall the app silently.
     * <p>Without root permission must hold
     * {@code android:sharedUserId="android.uid.shell"} and
     * {@code <uses-permission android:name="android.permission.DELETE_PACKAGES" />}</p>
     *
     * @param packageName The name of the package.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean uninstallAppSilent(final String packageName) {
        return uninstallAppSilent(packageName, false);
    }

    /**
     * Uninstall the app silently.
     * <p>Without root permission must hold
     * {@code android:sharedUserId="android.uid.shell"} and
     * {@code <uses-permission android:name="android.permission.DELETE_PACKAGES" />}</p>
     *
     * @param packageName The name of the package.
     * @param isKeepData  Is keep the data.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean uninstallAppSilent(final String packageName, final boolean isKeepData) {
        return uninstallAppSilent(packageName, isKeepData, isDeviceRooted());
    }

    /**
     * Uninstall the app silently.
     * <p>Without root permission must hold
     * {@code android:sharedUserId="android.uid.shell"} and
     * {@code <uses-permission android:name="android.permission.DELETE_PACKAGES" />}</p>
     *
     * @param packageName The name of the package.
     * @param isKeepData  Is keep the data.
     * @param isRooted    True to use root, false otherwise.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean uninstallAppSilent(final String packageName,
                                             final boolean isKeepData,
                                             final boolean isRooted) {
        if (isSpace(packageName)) return false;
        String command = "LD_LIBRARY_PATH=/vendor/lib*:/system/lib* pm uninstall "
                + (isKeepData ? "-k " : "")
                + packageName;
        ShellUtils.CommandResult commandResult = ShellUtils.execCmd(command, isRooted);
        if (commandResult.successMsg != null
                && commandResult.successMsg.toLowerCase().contains("success")) {
            return true;
        } else {
            LogUtils.e("uninstallAppSilent successMsg: " + commandResult.successMsg +
                    ", errorMsg: " + commandResult.errorMsg);
            return false;
        }
    }

    private static boolean isFileExists(final File file) {
        return file != null && file.exists();
    }

    private static File getFileByPath(final String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isDeviceRooted() {
        String su = "su";
        String[] locations = {"/system/bin/", "/system/xbin/", "/sbin/", "/system/sd/xbin/",
                "/system/bin/failsafe/", "/data/local/xbin/", "/data/local/bin/", "/data/local/",
                "/system/sbin/", "/usr/bin/", "/vendor/bin/"};
        for (String location : locations) {
            if (new File(location + su).exists()) {
                return true;
            }
        }
        return false;
    }

    private static boolean mHaveRoot = false;

    public static boolean isDeviceRootedShell() {
        if (!mHaveRoot) {
            // 通过执行测试命令来检测
            int ret = ShellUtils.execCmd("echo root", false, true).result;
            if (ret != -1) {
                LogUtils.d("have root!");
                mHaveRoot = true;
            } else {
                LogUtils.d("not root!");
            }
        } else {
            LogUtils.d("HaveRoot = true, have root!");
        }
        return mHaveRoot;
    }


    ///////////////////////////////////////////////////////////////////////////
    // DeviceUtils
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Shutdown the device
     * <p>Requires root permission
     * or hold {@code android:sharedUserId="android.uid.system"},
     * {@code <uses-permission android:name="android.permission.SHUTDOWN" />}
     * in manifest.</p>
     *
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean shutdown() {
        try {
            ShellUtils.CommandResult result = ShellUtils.execCmd("reboot -p", true);
            if (result.result == 0) return true;
            Utils.getApp().startActivity(IntentUtils.getShutdownIntent());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Reboot the device.
     * <p>Requires root permission
     * or hold {@code android:sharedUserId="android.uid.system"} in manifest.</p>
     *
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean reboot() {
        try {
            ShellUtils.CommandResult result = ShellUtils.execCmd("reboot", true);
            if (result.result == 0) return true;
            Intent intent = new Intent(Intent.ACTION_REBOOT);
            intent.putExtra("nowait", 1);
            intent.putExtra("interval", 1);
            intent.putExtra("window", 0);
            Utils.getApp().sendBroadcast(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Reboot the device.
     * <p>Requires root permission
     * or hold {@code android:sharedUserId="android.uid.system"},
     * {@code <uses-permission android:name="android.permission.REBOOT" />}</p>
     *
     * @param reason code to pass to the kernel (e.g., "recovery") to
     *               request special boot modes, or null.
     * @return {@code true}: success<br>{@code false}: fail
     */
    @RequiresPermission(REBOOT)
    public static boolean reboot(final String reason) {
        try {
            if (!PermissionUtils.isGranted(Manifest.permission.REBOOT)) {
                LogUtils.w("未开启系统重启权限");
                return false;
            }

            PowerManager pm = (PowerManager) Utils.getApp().getSystemService(Context.POWER_SERVICE);
            pm.reboot(reason);
            return true;
        } catch (Exception e) {
            LogUtils.e("reboot error", e);
            return false;
        }
    }

    /**
     * Reboot the device to recovery.
     * <p>Requires root permission.</p>
     *
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean reboot2Recovery() {
        ShellUtils.CommandResult result = ShellUtils.execCmd("reboot recovery", true);
        return result.result == 0;
    }

    /**
     * Reboot the device to bootloader.
     * <p>Requires root permission.</p>
     *
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean reboot2Bootloader() {
        ShellUtils.CommandResult result = ShellUtils.execCmd("reboot bootloader", true);
        return result.result == 0;
    }


    /**
     * Enable or disable mobile data.
     * <p>Must hold {@code android:sharedUserId="android.uid.system"},
     * {@code <uses-permission android:name="android.permission.MODIFY_PHONE_STATE" />}</p>
     *
     * @param enabled True to enabled, false otherwise.
     * @return {@code true}: success<br>{@code false}: fail
     */
    @RequiresPermission(MODIFY_PHONE_STATE)
    public static boolean setMobileDataEnabled(final boolean enabled) {
        try {
            TelephonyManager tm =
                    (TelephonyManager) Utils.getApp().getSystemService(Context.TELEPHONY_SERVICE);
            if (tm == null) return false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                tm.setDataEnabled(enabled);
                return true;
            }
            Method setDataEnabledMethod =
                    tm.getClass().getDeclaredMethod("setDataEnabled", boolean.class);
            setDataEnabledMethod.invoke(tm, enabled);
            return true;
        } catch (Exception e) {
            LogUtils.e("setMobileDataEnabled: ", e);
        }
        return false;
    }

    /**
     * Send sms silently.
     * <p>Must hold {@code <uses-permission android:name="android.permission.SEND_SMS" />}</p>
     *
     * @param phoneNumber The phone number.
     * @param content     The content.
     */
    @RequiresPermission(SEND_SMS)
    public static void sendSmsSilent(final String phoneNumber, final String content) {
        if (TextUtils.isEmpty(content)) return;
        PendingIntent sentIntent = PendingIntent.getBroadcast(Utils.getApp(), 0, new Intent("send"),
         (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) ? PendingIntent.FLAG_MUTABLE : PendingIntent.FLAG_UPDATE_CURRENT);
        SmsManager smsManager = SmsManager.getDefault();
        if (content.length() >= 70) {
            List<String> ms = smsManager.divideMessage(content);
            for (String str : ms) {
                smsManager.sendTextMessage(phoneNumber, null, str, sentIntent, null);
            }
        } else {
            smsManager.sendTextMessage(phoneNumber, null, content, sentIntent, null);
        }
    }

    public static void setSysTime(String time) {
        try {
            DataOutputStream os = new DataOutputStream(Runtime.getRuntime().exec("su").getOutputStream());
            os.writeBytes("setprop persist.sys.timezone GMT\n");
            os.writeBytes("/system/bin/date -s " + time + "\n");
            os.writeBytes("clock -w\n");
            os.writeBytes("exit\n");
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ping ip地址
     *
     * @return
     * @throws Exception
     */
    public static String ping(String ip) throws Exception {
        BufferedReader br = null;
        Process p = null;
        String str = "";
        try {
            p = Runtime.getRuntime().exec("/system/bin/ping -c 4 " + ip);
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            // p.destroy();
            // int state = p.exitValue();

            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            // xmString = new String(sb.toString().getBytes("UTF-8"));
            LogUtils.d("ping ip " + ip + ":" + sb.toString());
            str = sb.toString().split("---")[2];
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (p != null) {
                p.destroy();
            }
        }
        return str;

    }

    /**
     * 获取权限
     *
     * @param permission 权限
     * @param path       路径
     */
    public static void chmod(String permission, String path) {
        String command = "chmod " + permission + " " + path;
        ShellUtils.execCmd(command, false, false);
    }

}
