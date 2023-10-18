package core.colin.basic.utils.app;

import android.net.Uri;
import android.text.TextUtils;

import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class UrlParse {
    /**
     * 获得解析后的URL参数
     *
     * @param url url对象
     * @return URL参数map集合
     */
    public static Map<String, String> getUrlParams(String url) {
        final Map<String, String> query_pairs = new LinkedHashMap<>();
        URL mUrl = stringToURL(url);
        if (mUrl == null) {
            return query_pairs;
        }
        try {
            String query = mUrl.getQuery();
            if (query == null) {
                return query_pairs;
            }
            //判断是否包含url=,如果是url=后面的内容不用解析
            if (query.contains("url=")) {
                int index = query.indexOf("url=");
                String urlValue = query.substring(index + 4);
                query_pairs.put("url", URLDecoder.decode(urlValue, "UTF-8"));
                query = query.substring(0, index);
            }
            //除url之外的参数进行解析
            if (query.length() > 0) {
                final String[] pairs = query.split("&");
                for (String pair : pairs) {
                    final int idx = pair.indexOf("=");
                    //如果等号存在且不在字符串两端，取出key、value
                    if (idx > 0 && idx < pair.length() - 1) {
                        final String key = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
                        final String value = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
                        query_pairs.put(key, value);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return query_pairs;
    }

    /**
     * 获得Url参数字符串
     *
     * @param url url地址
     * @return 参数字符串
     */
    public static String getUrlParamStr(String url) {
        URL mUrl = stringToURL(url);
        if (mUrl == null) {
            return "";
        }
        try {
            String query = mUrl.getQuery();
            return query;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    /**
     * 获得url的协议+域+路径（即url路径问号左侧的内容）
     *
     * @param url url地址
     * @return url的协议+域+路径
     */
    public static String getUrlHostAndPath(String url) {
        if (url.contains("?")) {
            return url.substring(0, url.indexOf("?"));
        }
        return url;
    }

    /**
     * 获得Uri参数值
     *
     * @param uri      uri
     * @param paramKey 参数名称
     * @return 参数值
     */
    public static String getUriParam(Uri uri, String paramKey) {
        if (uri == null || paramKey == null || paramKey.length() == 0) {
            return "";
        }
        String paramValue = uri.getQueryParameter(paramKey);
        if (paramValue == null) {
            paramValue = "";
        }
        return paramValue;
    }

    /**
     * 获得Uri参数值Boolean
     *
     * @param uri      uri
     * @param paramKey 参数名称
     * @return 参数值
     */
    public static boolean getUriParamBoolean(Uri uri, String paramKey) {
        if (uri == null || paramKey == null || paramKey.length() == 0) {
            return false;
        }
        String paramValue = uri.getQueryParameter(paramKey);
        if (paramValue != null) {
            try {
                return Boolean.parseBoolean(paramKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * 获得Uri参数值
     *
     * @param uri      uri
     * @param paramKey 参数名称
     * @return 参数值
     */
    public static int getIntUriParam(Uri uri, String paramKey) {
        if (uri == null || paramKey == null || paramKey.length() == 0) {
            return 0;
        }
        try {
            String paramValue = uri.getQueryParameter(paramKey);
            if (paramValue == null || paramValue.length() == 0) {
                return 0;
            }
            return parseInt(paramValue);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    /**
     * 获得Uri参数值
     *
     * @param uri      uri
     * @param paramKey 参数名称
     * @return 参数值
     */
    public static long getLongUriParam(Uri uri, String paramKey, long defaultValue) {
        if (uri == null || paramKey == null || paramKey.length() == 0) {
            return 0;
        }
        try {
            String paramValue = uri.getQueryParameter(paramKey);
            if (paramValue == null || paramValue.length() == 0) {
                return defaultValue;
            }
            return parseLong(paramValue);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return defaultValue;
    }

    /**
     * 字符串转为URL对象
     *
     * @param url url字符串
     * @return url对象
     */
    private static URL stringToURL(String url) {
        if (url == null || url.length() == 0 || !url.contains("://")) {
            return null;
        }
        try {
            StringBuilder sbUrl = new StringBuilder("http");
            sbUrl.append(url.substring(url.indexOf("://")));
            URL mUrl = new URL(sbUrl.toString());
            return mUrl;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * parse string to int
     *
     * @return default return 0
     */
    public static int parseInt(String val) {
        return parseInt(val, 0);
    }

    /**
     * parse string to int
     *
     * @param val        the target string
     * @param defaultInt the default return value
     */
    public static int parseInt(String val, int defaultInt) {
        if (TextUtils.isEmpty(val)) {
            return defaultInt;
        } else {
            try {
                return Integer.parseInt(val);
            } catch (Exception var3) {
                return defaultInt;
            }
        }
    }

    /**
     * parse string to int
     *
     * @return default return 0
     */
    public static long parseLong(String val) {
        return parseLong(val, 0);
    }

    /**
     * parse string to int
     *
     * @param val        the target string
     * @param defaultInt the default return value
     */
    public static long parseLong(String val, int defaultInt) {
        if (TextUtils.isEmpty(val)) {
            return defaultInt;
        } else {
            try {
                return Long.parseLong(val);
            } catch (Exception var3) {
                return defaultInt;
            }
        }
    }
}