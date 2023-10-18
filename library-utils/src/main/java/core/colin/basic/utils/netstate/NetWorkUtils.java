package core.colin.basic.utils.netstate;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import core.colin.basic.utils.Utils;

/**
 * @author PengLin
 * @project
 * @desc
 * @date 2017/3/6
 */

public final class NetWorkUtils {

    private static final String TAG = "NetWorkUtils";
    // wifi信号等级
    private static final int WIFI_SIGNAL_LEVEL = 4;

    public static final int TYPE_NONE = -1;
    public static final int TYPE_MOBILE = 0;
    public static final int TYPE_WIFI = 1;


    /**
     * 获取网络状态
     *
     * @return one of TYPE_NONE, TYPE_MOBILE, TYPE_WIFI
     * @permission android.permission.ACCESS_NETWORK_STATE
     */
    @SuppressLint("MissingPermission")
    public static final int getNetWorkStates() {
        if (Utils.getAppContext() != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) Utils.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
                return TYPE_NONE;//没网
            }

            int type = activeNetworkInfo.getType();
            switch (type) {
                case ConnectivityManager.TYPE_MOBILE:
                    return TYPE_MOBILE;//移动数据
                case ConnectivityManager.TYPE_WIFI:
                    return TYPE_WIFI;//WIFI
                default:
                    break;
            }
        }
        return TYPE_NONE;
    }


    /**
     * 判断是否有网络
     *
     * @return 返回值
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean isConnected() {
        if (Utils.getAppContext() != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) Utils.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null) {
                    return networkInfo.isConnected();
                }
            }
        }
        return false;
    }


    /**
     * 描述：判断网络是否有效.
     *
     * @param context the context
     * @return true, if is network available
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                //方式一
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
                //方式二
                NetworkInfo[] networkInfos = connectivity.getAllNetworkInfo();
                for (NetworkInfo network : networkInfos) {
                    if (network.isConnected()) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * 检测wifi是否连接上了(检测wifi的具体连接信息)
     *
     * @param ctx
     * @return
     */
    public static boolean isWifiConnected2(Context ctx) {
        WifiManager wifiManager = (WifiManager) ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (wifiInfo != null) {
                    String ssid = wifiInfo.getSSID();
                    SupplicantState state = wifiInfo.getSupplicantState();
                    if (ssid != null && state == SupplicantState.COMPLETED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 获得Wifi信号强度等级
     *
     * @param ctx
     * @return 0-4
     */
    public static int getRssiLevel(Context ctx) {
        WifiManager wifiManager = (WifiManager) ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int rssi = wifiInfo.getRssi();//获得信号强度RSSI的值
        return WifiManager.calculateSignalLevel(rssi, WIFI_SIGNAL_LEVEL);
    }


    /**
     * 获取mac地址
     *
     * @return
     */
    public static String getMac() {
        String mac = "";
        String netMac = getEth0NetMac();
        mac = (netMac == null ? "" : netMac.toUpperCase());
        Log.d(TAG, "ethernet mac:" + mac);
        if (mac == null || mac.isEmpty()) {
            String wifiMac = getWifiMac();
            mac = (wifiMac == null ? "" : wifiMac.toUpperCase());
            Log.d(TAG, "wifi mac:" + mac);
        }
        return mac;
    }

    /**
     * 获取以太网mac地址
     *
     * @return
     */
    public static String getEth0NetMac() {
        String str = null;
        try {
            byte[] arrayOfByte = NetworkInterface.getByName("eth0").getHardwareAddress();
            str = hexByte(arrayOfByte[0]) + hexByte(arrayOfByte[1]) + hexByte(arrayOfByte[2])
                    + hexByte(arrayOfByte[3]) + hexByte(arrayOfByte[4]) + hexByte(arrayOfByte[5]);
        } catch (Exception e) {
//            Log.e(TAG, "获取以太网mac地址异常");
//            e.printStackTrace();
        }
        return str == null ? null : str.toUpperCase();
    }


    /**
     * 获取Wifi mac地址（适配所有Android版本）
     *
     * @return
     */
    public static String getWifiMac() {
        String mac = "";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mac = getMacDefault();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mac = getMacAddress();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mac = getMacFromHardware();
        }
        return mac == null ? null : mac.toUpperCase();
    }


    /**
     * Android 6.0 之前（不包括6.0）获取mac地址
     * 获取Wifi的Mac地址
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    private static String getMacDefault() {
        String mac = null;
        try {
            WifiManager wifiManager = (WifiManager) Utils.getAppContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null) {
                WifiInfo info = wifiManager.getConnectionInfo();
                if (info != null) {
                    mac = info.getMacAddress();
                    if (!TextUtils.isEmpty(mac)) {
                        //去掉标点符号
                        mac = mac.replaceAll("[\\p{Punct}\\p{Space}]+", "").toUpperCase();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mac;
    }

    /**
     * Android 6.0-Android 7.0 获取mac地址
     * <p>
     * 通过cat /sys/class/net/wlan0/address
     */
    private static String getMacAddress() {
        String macSerial = null;
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec("cat/sys/class/net/wlan0/address");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            while (null != str) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();//去空格
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (macSerial != null) {
            macSerial = macSerial.replaceAll("[\\p{Punct}\\p{Space}]+", "").toUpperCase();
        }
        return macSerial;
    }

    /**
     * Android 6.0-Android 7.0 获取mac地址
     * <p>
     * 通过ip地址来获取绑定的mac地址
     */
    private static String getMacAddressFromIP() {
        String strMacAddr = null;
        String str = "";
        try {
            //获得IpD地址
            InetAddress ip = getLocalInetAddress();
            byte[] b = NetworkInterface.getByInetAddress(ip).getHardwareAddress();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append(':');
                }
                str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddr = buffer.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (strMacAddr != null) {
            strMacAddr = strMacAddr.replaceAll("[\\p{Punct}\\p{Space}]+", "").toUpperCase();
        }
        return strMacAddr;
    }

    /**
     * 获取移动设备本地IP
     *
     * @return
     */
    private static InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            //列举
            Enumeration<NetworkInterface> en_netInterface = NetworkInterface.getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {//是否还有元素
                NetworkInterface ni = (NetworkInterface) en_netInterface.nextElement();//得到下一个元素
                Enumeration<InetAddress> en_ip = ni.getInetAddresses();//得到一个ip地址的列举
                while (en_ip.hasMoreElements()) {
                    ip = en_ip.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1)
                        break;
                    else
                        ip = null;
                }
                if (ip != null) {
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ip;
    }


    /**
     * android 7.0及以上 （2）扫描各个网络接口获取mac地址
     * <p>
     * Android 7.0之后获取Mac地址
     * 遍历循环所有的网络接口，找到接口是 wlan0
     * 必须的权限 <uses-permission android:name="android.permission.INTERNET"></uses-permission>
     *
     * @return
     */
    private static String getMacFromHardware() {
        String mac = null;
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) {
                    continue;
                }
                byte[] macBytes = nif.getHardwareAddress();
                mac = bytesToString(macBytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mac != null) {
            mac = mac.replaceAll("[\\p{Punct}\\p{Space}]+", "").toUpperCase();
        }
        return mac;
    }


    /**
     * android 7.0及以上 （3）
     * *通过busybox获取本地存储的mac地址
     *
     * @return
     */
    private static String getLocalMacAddressFromBusybox() {
        String result = "";
        String Mac = "";
        result = callCmd("busybox ifconfig", "HWaddr");
        // 如果返回的result == null，则说明网络不可取
        if (result == null) {
            return null;
        }
        // 对该行数据进行解析
        // 例如：eth0 Link encap:Ethernet HWaddr 00:16:E8:3E:DF:67
        if (result.length() > 0 && result.contains("HWaddr") == true) {
            Mac = result.substring(result.indexOf("HWaddr") + 6,
                    result.length() - 1);
            result = Mac;
        }
        return result;
    }

    private static String callCmd(String cmd, String filter) {
        String result = "";
        String line = "";
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            InputStreamReader is = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(is);

            while ((line = br.readLine()) != null
                    && line.contains(filter) == false) {
                result += line;
            }

            result = line;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /***
     * byte转为String
     *
     * @param bytes
     * @return
     */
    private static String bytesToString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        for (byte b : bytes) {
            buf.append(String.format("%02X:", b));
        }
        if (buf.length() > 0) {
            buf.deleteCharAt(buf.length() - 1);
        }
        return buf.toString();
    }

    /**
     * 输出本地网络接口以及显示其信息
     */
    public static void printNetworkInfo() {
        try {
            Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface ni = e.nextElement();
                System.out.println("displayname: " + ni.getDisplayName());
                System.out.println("name: " + ni.getName());
                System.out.println("MTU: " + ni.getMTU());
                System.out.println("Loopback: " + ni.isLoopback());
                System.out.println("Virtual: " + ni.isVirtual());
                System.out.println("Up: " + ni.isUp());
                System.out.println("PointToPoint: " + ni.isPointToPoint());
                byte[] arryOfByte = ni.getHardwareAddress();
                if (arryOfByte != null) {
                    String mac = hexByte(arryOfByte[0]) + hexByte(arryOfByte[1])
                            + hexByte(arryOfByte[2]) + hexByte(arryOfByte[3])
                            + hexByte(arryOfByte[4]) + hexByte(arryOfByte[5]);
                    System.out.println("mac: " + mac);
                } else {
                    System.out.println("mac is null");
                }
                System.out.println("---------------------");
            }
        } catch (SocketException e1) {
            e1.printStackTrace();
        }

    }

    private static String hexByte(byte paramByte) {
        String str = "000000" + Integer.toHexString(paramByte);
        return str.substring(-2 + str.length());
    }


    /**
     * IP地址转换为整数
     *
     * @param ip
     * @return
     */
    public static long ip2Int(String ip) {
        String[] items = ip.split("\\.");
        return Long.valueOf(items[0]) << 24 | Long.valueOf(items[1]) << 16
                | Long.valueOf(items[2]) << 8 | Long.valueOf(items[3]);
    }

    /**
     * 整数转换为IP地址
     *
     * @param ipInt
     * @return
     */
    public static String int2Ip(long ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    public static boolean isWifiProxy(Context context) {
        final boolean IS_ICS_OR_LATER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
        String proxyAddress;
        int proxyPort;
        if (IS_ICS_OR_LATER) {
            proxyAddress = System.getProperty("http.proxyHost");    //获取代理主机
            String portStr = System.getProperty("http.proxyPort");  //获取代理端口
            proxyPort = Integer.parseInt((portStr != null ? portStr : "-1"));
        } else {
            proxyAddress = android.net.Proxy.getHost(context);
            proxyPort = android.net.Proxy.getPort(context);
        }
        Log.i("代理信息", "proxyAddress :" + proxyAddress + ", proxyPort : " + proxyPort);
        return (!TextUtils.isEmpty(proxyAddress)) && (proxyPort != -1);
    }


}
