package core.colin.basic.utils.netstate;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.format.Formatter;

import androidx.annotation.RequiresPermission;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

public class GetNetworkParameters {

    /**
     * 获取IP地址
     *
     * @return
     */
    public static String getLocalIpAddress() {
        try {
            for (NetworkInterface inetAddresses : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                for (InetAddress inetAddress : Collections.list(inetAddresses.getInetAddresses())) {
                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
            return "0.0.0.0";
        } catch (SocketException e) {
            e.printStackTrace();
            return "0.0.0.0";
        }
    }


    /**
     * 获取子网掩码
     *
     * @return
     */
    public static String getLocalMask() {
        try {
            for (NetworkInterface inetAddresses : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                for (InterfaceAddress inetAddress : inetAddresses.getInterfaceAddresses()) {
                    InetAddress address = inetAddress.getAddress();
                    if (!address.isLoopbackAddress() && (address instanceof Inet4Address)) {
                        return MaskConversion.calcMaskByPrefixLength(inetAddress.getNetworkPrefixLength());
                    }
                }
            }
            return "0.0.0.0";
        } catch (SocketException e) {
            e.printStackTrace();
            return "0.0.0.0";
        }
    }

    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static String[] getDnsFromConnectionManager() {
        LinkedList<String> dnsServers = new LinkedList<>();
        if (Build.VERSION.SDK_INT >= 21) {
            ConnectivityManager connectivityManager = (ConnectivityManager) Utils.getApp().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                 NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo != null) {
                    for (Network network : connectivityManager.getAllNetworks()) {
                        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
                        if (networkInfo != null && networkInfo.getType() == activeNetworkInfo.getType()) {
                            LinkProperties lp = connectivityManager.getLinkProperties(network);
                            for (InetAddress addr : lp.getDnsServers()) {
                                dnsServers.add(addr.getHostAddress());
                            }
                        }
                    }
                }
            }
        }
        return dnsServers.isEmpty() ? new String[0] : dnsServers.toArray(new String[0]);
    }

    public static String getMacAddress() {
        return FileIOUtils.readFile2String("/sys/class/net/eth0/address").toUpperCase(Locale.ENGLISH).substring(0, 17);
    }

    @SuppressLint("SoonBlockedPrivateApi")
    public static Map<String, String> getEthernetParameters(Context context) {
        Map<String, String> hashMap = new HashMap<>();
        try {
            Class<?> cls = Class.forName("android.net.EthernetManager");
            Object systemService = context.getSystemService((String) Context.class.getField("ETHERNET_SERVICE").get((Object) null));
            Field declaredField = cls.getDeclaredField("mService");
            declaredField.setAccessible(true);
            Object obj = declaredField.get(systemService);
            for (Method method : Class.forName("android.net.IEthernetManager").getDeclaredMethods()) {
                String name = method.getName();
                if ("getGateway".equals(name)) {
                    hashMap.put("gateWay", (String) method.invoke(obj, new Object[0]));
                } else if ("getNetmask".equals(name)) {
                    hashMap.put("maskAddress", (String) method.invoke(obj, new Object[0]));
                } else if ("getIpAddress".equals(name)) {
                    hashMap.put("ipAddress", (String) method.invoke(obj, new Object[0]));
                } else if ("getDns".equals(name)) {
                    String[] split = ((String) method.invoke(obj, new Object[0])).split("\\,");
                    int i = 0;
                    while (i < split.length) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("dns");
                        int i2 = i + 1;
                        sb.append(i2);
                        hashMap.put(sb.toString(), split[i]);
                        i = i2;
                    }
                }
            }
            hashMap.put("macAddress", getMacAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    @SuppressLint("MissingPermission")
    public static Map<String, String> getWifiParameters(Context context) {
        Map<String, String> hashMap = new HashMap<>();
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        hashMap.put("ipAddress", Formatter.formatIpAddress(dhcpInfo.ipAddress));
        hashMap.put("maskAddress", Formatter.formatIpAddress(dhcpInfo.netmask));
        hashMap.put("gateWay", Formatter.formatIpAddress(dhcpInfo.gateway));
        hashMap.put("dns1", Formatter.formatIpAddress(dhcpInfo.dns1));
        hashMap.put("dns2", Formatter.formatIpAddress(dhcpInfo.dns2));
        hashMap.put("macAddress", wifiManager.getConnectionInfo().getMacAddress());
        return hashMap;
    }

    public static boolean pingIpAddress(String str) {
        try {
            Runtime runtime = Runtime.getRuntime();
            if (runtime.exec("/system/bin/ping -c 1 -w 100 " + str).waitFor() == 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int inToLongReverse(String str) {
        int indexOf = str.indexOf(".");
        int i = indexOf + 1;
        int indexOf2 = str.indexOf(".", i);
        int i2 = indexOf2 + 1;
        int indexOf3 = str.indexOf(".", i2);
        int[] jArr = {
                Integer.parseInt(str.substring(0, indexOf)),
                Integer.parseInt(str.substring(i, indexOf2)),
                Integer.parseInt(str.substring(i2, indexOf3)),
                Integer.parseInt(str.substring(indexOf3 + 1))
        };
        return (jArr[3] << 24) + (jArr[2] << 16) + (jArr[1] << 8) + jArr[0];
    }

    /**
     * ip地址转成int型数字
     * 将IP地址转化成整数的方法如下：
     * 1、通过String的split方法按.分隔得到4个长度的数组
     * 2、通过左移位操作（<<）给每一段的数字加权，第一段的权为2的24次方，第二段的权为2的16次方，第三段的权为2的8次方，最后一段的权为1
     *
     * @param strIp
     * @return
     */
    public static long ipToInt(String strIp) {
        String[] ip = strIp.split("\\.");
        return (Integer.parseInt(ip[0]) << 24) + (Integer.parseInt(ip[1]) << 16) + (Integer.parseInt(ip[2]) << 8) + Integer.parseInt(ip[3]);
    }

    /**
     * 将十进制整数形式转换成127.0.0.1形式的ip地址
     * 将整数形式的IP地址转化成字符串的方法如下：
     * 1、将整数值进行右移位操作（>>>），右移24位，右移时高位补0，得到的数字即为第一段IP。
     * 2、通过与操作符（&）将整数值的高8位设为0，再右移16位，得到的数字即为第二段IP。
     * 3、通过与操作符吧整数值的高16位设为0，再右移8位，得到的数字即为第三段IP。
     * 4、通过与操作符吧整数值的高24位设为0，得到的数字即为第四段IP。
     *
     * @param ipInt
     * @return
     */
    public static String intToIP(int ipInt) {
        StringBuffer sb = new StringBuffer("");
        // 直接右移24位
        sb.append(String.valueOf((ipInt >>> 24))).append(".");
        // 将高8位置0，然后右移16位
        sb.append(String.valueOf((ipInt & 0x00FFFFFF) >>> 16)).append(".");
        // 将高16位置0，然后右移8位
        sb.append(String.valueOf((ipInt & 0x0000FFFF) >>> 8)).append(".");
        // 将高24位置0
        sb.append(String.valueOf((ipInt & 0x000000FF)));
        return sb.toString();
    }


}
