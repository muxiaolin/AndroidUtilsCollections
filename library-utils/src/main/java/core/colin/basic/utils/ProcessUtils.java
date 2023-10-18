package core.colin.basic.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;

import com.blankj.utilcode.util.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

/**
 * Author: PL
 * Date: 2022/7/14
 * Desc:
 */
public class ProcessUtils {

    public static void killProcess() {
        Process.killProcess(Process.myPid());
    }

    public static boolean isMainProcess() {
        return com.blankj.utilcode.util.ProcessUtils.isMainProcess();
    }

    static String getCurrentProcessName() {
        return com.blankj.utilcode.util.ProcessUtils.getCurrentProcessName();
    }

    public static String getCurrentProcessNameByFile(int pid) {
        try {
            File file = new File("/proc/" + pid + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getCurrentProcessNameByAms(int pid) {
        ActivityManager am = (ActivityManager) Utils.getApp().getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) return "";
        List<ActivityManager.RunningAppProcessInfo> info = am.getRunningAppProcesses();
        if (info == null || info.size() == 0) return "";
        for (ActivityManager.RunningAppProcessInfo aInfo : info) {
            if (aInfo.pid == pid) {
                if (aInfo.processName != null) {
                    return aInfo.processName;
                }
            }
        }
        return "";
    }

}
