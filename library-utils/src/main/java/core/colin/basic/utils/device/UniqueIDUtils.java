package core.colin.basic.utils.device;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.PermissionChecker;

import com.blankj.utilcode.util.Utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author 彭林
 * @version v1.0
 * @date 2020/8/21
 */
public class UniqueIDUtils {
    private static final String TAG = "UniqueIDUtils";
    private static String uniqueID;
    private static final String uniqueKey = "unique_id";
    private static final String uniqueIDFile = "unique.txt";


    /**
     * @param isUserAgree 是否用户同意获取设备信息
     * @return
     */
    public static String getUniqueID(boolean isUserAgree) {
        Context context = Utils.getApp().getApplicationContext();
        //从内存中读取
        if (!TextUtils.isEmpty(uniqueID)) {
            Log.d(TAG, "getUniqueID, 内存中获取: " + uniqueID);
            return uniqueID;
        }
        //从SP读取
        uniqueID = PreferenceManager.getDefaultSharedPreferences(context).getString(uniqueKey, "");
        if (!TextUtils.isEmpty(uniqueID)) {
            Log.d(TAG, "getUniqueID, SP中获取: " + uniqueID);
            return uniqueID;
        }
        //从文件中读取
        uniqueID = readUniqueFile(context);
        if (!TextUtils.isEmpty(uniqueID)) {
            Log.d(TAG, "getUniqueID, 外部存储中获取: " + uniqueID);
            return uniqueID;
        }
        //两步创建：硬件获取；自行生成与存储
        uniqueID = getDeviceID(context);
        if (!TextUtils.isEmpty(uniqueID)) {
            Log.d(TAG, "getUniqueID, IMEI中获取: " + uniqueID);
            saveUniquerID(context);
            return uniqueID;
        }
        //获得AndroidId（无需权限）
        if (isUserAgree) {
            uniqueID = getAndroidID(context);
            if (!TextUtils.isEmpty(uniqueID)) {
                Log.d(TAG, "getUniqueID, AndroidID获取: " + uniqueID);
                saveUniquerID(context);
                return uniqueID;
            }
            //
            uniqueID = getSNID();
            if (!TextUtils.isEmpty(uniqueID)) {
                Log.d(TAG, "getUniqueID, SNID获取: " + uniqueID);
                saveUniquerID(context);
                return uniqueID;
            }
        }
        //生成一个UUID
        String uuid = UUID.randomUUID().toString().replace("-", "");
        Log.d(TAG, "getUniqueID, 生成一个UUID: " + uuid);
//        uniqueID = uuid;
//        saveUniquerID(context);
        return uuid;
    }

    private static void saveUniquerID(Context context) {
        if (!TextUtils.isEmpty(uniqueID)) {
            //写文件
            createUniqueID(context, uniqueID);
            //写入SP
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            editor.putString(uniqueKey, uniqueID);
            editor.apply();
        }
    }

    @SuppressLint("MissingPermission")
    private static String getDeviceID(Context context) {
        String deviceId = null;
        try {
            if (PermissionChecker.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PermissionChecker.PERMISSION_GRANTED) {
                TelephonyManager telMagr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (telMagr != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        deviceId = telMagr.getImei();
                    } else {
                        deviceId = telMagr.getDeviceId();
                    }
                }
            }
        } catch (Exception e) {

        }
        if (TextUtils.isEmpty(deviceId) || "null".equalsIgnoreCase(deviceId)
                || "unknown".equalsIgnoreCase(deviceId) || "9774d56d682e549c".equals(deviceId)) {
            return null;
        } else {
            Log.d(TAG, "getDevice IMEI: " + deviceId);
            return deviceId;
        }
    }

    private static String getAndroidID(Context context) {
        String androidID = null;
        try {
            androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {

        }
        if (TextUtils.isEmpty(androidID) || "null".equalsIgnoreCase(androidID)
                || "unknown".equalsIgnoreCase(androidID) || "9774d56d682e549c".equals(androidID)) {
            return null;
        } else {
            Log.d(TAG, "getAndroidID: " + androidID);
            return androidID;
        }

    }

    private static String getSNID() {
        String snID = "";
        //方式一: 获得设备序列号（如：WTK7N16923005607）, 个别设备无法获取, 部分红米手机都会返回无用值0123456789ABCDEF
        try {
            String serialNumber = null;
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            if (clazz != null) {
                Method method_get = clazz.getMethod("get", String.class, String.class);
                if (method_get != null) {
                    serialNumber = (String) (method_get.invoke(clazz, "ro.serialno", ""));
                }
            }
            Log.d(TAG, "getSNID: serialNumber=" + serialNumber);
            if (!TextUtils.isEmpty(serialNumber) && !"null".equalsIgnoreCase(serialNumber)
                    && !"unknown".equalsIgnoreCase(serialNumber) && !"0123456789ABCDEF".equalsIgnoreCase(serialNumber)) {
                snID = serialNumber;
            }
        } catch (Exception ex) {
        }
        //方式二
        if (TextUtils.isEmpty(snID)) {
            try {
                String serial = Build.class.getField("SERIAL").get((Object) null).toString();
                Log.d(TAG, "getSNID: serial=" + serial);
                if (!TextUtils.isEmpty(serial) && !"null".equalsIgnoreCase(serial) && !"unknown".equalsIgnoreCase(serial)) {
                    snID = serial;
                } else {
                    snID = Build.SERIAL;
                }
            } catch (Exception e) {
                snID = Build.SERIAL;
            }
        }
        if (TextUtils.isEmpty(snID) || "null".equalsIgnoreCase(snID) || "unknown".equalsIgnoreCase(snID)) {
            return null;
        } else {
            Log.d(TAG, "getSNID: snID=" + snID);
            return snID;
        }
    }


    private static String getCacheFieDir(Context context) {
        if (Environment.isExternalStorageEmulated()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + context.getPackageName();
        }
        return context.getExternalCacheDir().getAbsolutePath();
    }

    /**
     * 生成一个UUID保存到磁盘文件
     *
     * @param context
     */
    private static String createUniqueID(Context context, String uniqueID) {
        if (PermissionChecker.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED
                || PermissionChecker.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            return null;
        }
        FileOutputStream outputStream = null;
        try {
            File filesDir = new File(getCacheFieDir(context));
            if (!filesDir.exists()) {
                filesDir.mkdir();
            }
            File file = new File(filesDir, uniqueIDFile);
            if (!file.exists()) {
                file.createNewFile();
            }
            outputStream = new FileOutputStream(file);
            outputStream.write(uniqueID.getBytes());
            Log.d(TAG, "createUniqueID: UUID=" + uniqueID);
            return uniqueID;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 读取磁盘上的UUID
     *
     * @param context
     */
    private static String readUniqueFile(Context context) {
        if (PermissionChecker.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            return null;
        }
        File filesDir = new File(getCacheFieDir(context));
        File file = new File(filesDir, uniqueIDFile);
        if (!file.exists()) {
            return null;
        }
        String uniqueID = null;
        ByteArrayOutputStream os = null;
        InputStream is = null;
        int sBufferSize = 512 * 1024;
        try {
            os = new ByteArrayOutputStream();
            is = new BufferedInputStream(new FileInputStream(file), sBufferSize);
            byte[] b = new byte[sBufferSize];
            int len;
            while ((len = is.read(b, 0, sBufferSize)) != -1) {
                os.write(b, 0, len);
            }
            uniqueID = os.toString();
            Log.d(TAG, "readUniqueFile: UUID=" + uniqueID);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return uniqueID;
    }

    /**
     * 清除磁盘上的UUID文件
     *
     * @param context
     */
    public static void clearUniqueFile(Context context) {
        File filesDir = new File(getCacheFieDir(context));
        deleteFile(context, filesDir);
    }

    private static void deleteFile(Context context, File file) {
        if (PermissionChecker.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED
                || PermissionChecker.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            return;
        }
        if (file.isDirectory()) {
            for (File listFile : file.listFiles()) {
                deleteFile(context, listFile);
            }
        } else {
            file.delete();
        }
    }
}
