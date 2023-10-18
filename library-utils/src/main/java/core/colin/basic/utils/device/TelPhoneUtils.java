package core.colin.basic.utils.device;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_PHONE_STATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.annotation.RequiresPermission;
import androidx.core.content.PermissionChecker;

import com.blankj.utilcode.util.NetworkUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;

import core.colin.basic.utils.Utils;

/**
 * @author PengLin
 * @project robo
 * @desc
 * @date 2020/4/14
 */
public class TelPhoneUtils {
    private static final String TAG = "TelPhoneUtils";

    /**
     * 判断数据流量开关是否打开
     *
     * @return
     */
    public static boolean isMobileDataEnabled() {
        return NetworkUtils.getMobileDataEnabled();
//        try {
//            Method method = ConnectivityManager.class.getDeclaredMethod("getMobileDataEnabled");
//            method.setAccessible(true);
//            ConnectivityManager connectivityManager = (ConnectivityManager) InitApp.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//            return (Boolean) method.invoke(connectivityManager);
//        } catch (Throwable t) {
//            Log.e(TAG, "Check mobile data encountered exception");
//            return false;
//        }
    }


    /**
     * 获取手机号码
     * <p>
     * 需要动态获取android.permission.READ_PHONE_STATE 权限
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    public static String getTelPhone() {
        if (PermissionChecker.checkSelfPermission(Utils.getAppContext(), Manifest.permission.READ_PHONE_STATE) == PermissionChecker.PERMISSION_GRANTED) {
            TelephonyManager tm = (TelephonyManager) Utils.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
//        String imei = tm.getSimSerialNumber();//获得SIM卡的序号
//        String imsi = tm.getSubscriberId();//得到用户Id
//            String deviceid = tm.getDeviceId();//获取智能设备唯一编号
            String telPhone = tm.getLine1Number();//获取本机号码
            if (!TextUtils.isEmpty(telPhone)) {
                return telPhone;
            }
        }
        return null;
    }

    //在Android6.0以及之后，需要动态获取android.permission.READ_PHONE_STATE 权限。因此存在用户拒绝授权的可能，此外首次启动后就上报设备ID时也可能影响启动速度
    //可能存在获取不到DeviceId的可能，存在返回null或者000000的垃圾数据可能
    //只对有电话功能的设备有效(无需插卡，但是需要有对应硬件模块)。例如在部分pad上可能无法获取到DeviceId
    @SuppressLint("MissingPermission")
    public static String getIMEI() {
        try {
            if (PermissionChecker.checkSelfPermission(Utils.getAppContext(), Manifest.permission.READ_PHONE_STATE) == PermissionChecker.PERMISSION_GRANTED) {
                TelephonyManager telMagr = (TelephonyManager) Utils.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
                if (telMagr != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        return telMagr.getImei();
                    } else {
                       return telMagr.getDeviceId();
                    }
                }
            }
        } catch (Exception ex) {
        }
        return "";
    }

    /**
     * 运营商网络信号强度
     *
     * @return
     */
    @RequiresPermission(allOf={READ_PHONE_STATE, ACCESS_FINE_LOCATION})
    public static int getDbm() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                TelephonyManager tm = (TelephonyManager) Utils.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
                List<CellInfo> allCellInfo = tm.getAllCellInfo();
                int i = -1;
                if (allCellInfo != null) {
                    /** 1、GSM是通用的移动联通电信2G的基站。
                     2、CDMA是3G的基站。
                     3、LTE，则证明支持4G的基站。*/
                    for (CellInfo next : allCellInfo) {
                        if (next instanceof CellInfoGsm) {
                            i = ((CellInfoGsm) next).getCellSignalStrength().getDbm();
                        } else if (next instanceof CellInfoCdma) {
                            i = ((CellInfoCdma) next).getCellSignalStrength().getDbm();
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && next instanceof CellInfoWcdma) {
                            i = ((CellInfoWcdma) next).getCellSignalStrength().getDbm();
                        } else if (next instanceof CellInfoLte) {
                            i = ((CellInfoLte) next).getCellSignalStrength().getDbm();
                        }
                    }
                }
                return i;
            }
        } catch (Exception e) {
        }
        return -1;
    }

    /**
     * 用来获取手机拨号上网（包括CTWAP和CTNET）时由PDSN分配给手机终端的源IP地址。
     */
    public static String getPsdnIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 获取运营商sim卡的ICCID号
     * <p>
     * 需要动态获取android.permission.READ_PHONE_STATE 权限
     *
     * @return ICCID号
     */
    @RequiresPermission(allOf = {READ_PHONE_STATE})
    @SuppressLint("MissingPermission")
    public static String getICCID() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            List<SubscriptionInfo> infoList;
            try {
                if ((infoList = SubscriptionManager.from(Utils.getAppContext()).getActiveSubscriptionInfoList()) != null
                        && infoList.size() >= 1) {
                    return infoList.get(0).getIccId();
                }
            } catch (Exception e) {
            }
        } else {
            TelephonyManager tm = (TelephonyManager) Utils.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
            String simSerialNumber = tm.getSimSerialNumber();
            if (!TextUtils.isEmpty(simSerialNumber)) {
                return tm.getSimSerialNumber();
            }
        }
        return null;
    }

}
