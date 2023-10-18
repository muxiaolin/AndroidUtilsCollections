package core.colin.basic.utils.time;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class NetworkTime {
    public static final String SPACE = " ";
    private static NetworkTime mInterface = null;
    public static List<String> webUrlList = new ArrayList<>();

    static {
        webUrlList.add("http://www.bjtime.cn");
        webUrlList.add("http://www.baidu.com");
        webUrlList.add("http://www.taobao.com");
        webUrlList.add("http://www.ntsc.ac.cn");
        webUrlList.add("http://www.360.cn");
        webUrlList.add("http://www.beijing-time.org");
    }

    public static NetworkTime newInstance() {
        if (mInterface == null) {
            mInterface = new NetworkTime();
        }
        return mInterface;
    }

    private static long getWebsiteDatetime(String str) {
        try {
            URLConnection openConnection = new URL(str).openConnection();
            openConnection.connect();
            return openConnection.getDate();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return 0;
        } catch (IOException e2) {
            e2.printStackTrace();
            return 0;
        }
    }

    public static long getNetworkTimeActiveLong() {
        for (int i = 0; i < webUrlList.size(); i++) {
            long websiteDatetime = getWebsiteDatetime(webUrlList.get(i));
            if (websiteDatetime != 0) {
                return websiteDatetime;
            }
        }
        return 0;
    }

    public static Timestamp getNetworkTimeActiveTimeStamp() {
        long networkTimeActiveLong = getNetworkTimeActiveLong();
        if (networkTimeActiveLong != 0) {
            return new Timestamp(networkTimeActiveLong);
        }
        return null;
    }

    public static Date getNetworkTimeActiveDate() {
        long networkTimeActiveLong = getNetworkTimeActiveLong();
        if (networkTimeActiveLong != 0) {
            return new Date(networkTimeActiveLong);
        }
        return null;
    }

    public static String getNetworkTimeActiveString() {
        Date networkTimeActiveDate = getNetworkTimeActiveDate();
        if (networkTimeActiveDate != null) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(networkTimeActiveDate);
        }
        return null;
    }

    public static String setSystemTime(String str) {
        String property = System.getProperty("os.name");
        String[] split = str.split(SPACE);
        String str2 = split[0];
        String str3 = split[1];
        try {
            if (property.matches("^(?i)Windows.*$")) {
                Runtime.getRuntime().exec(" cmd /c date " + str2);
                Runtime.getRuntime().exec(" cmd /c time " + str3);
            } else if (!property.matches("^(?i)Linux.*$")) {
                return null;
            } else {
                Runtime.getRuntime().exec("date -s \"" + str2 + SPACE + str3 + "\"");
            }
            return str;
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    public static boolean calibrationSystemTime() {
        String networkTimeActiveString = getNetworkTimeActiveString();
        return networkTimeActiveString != null && networkTimeActiveString.equals(setSystemTime(networkTimeActiveString));
    }
}
