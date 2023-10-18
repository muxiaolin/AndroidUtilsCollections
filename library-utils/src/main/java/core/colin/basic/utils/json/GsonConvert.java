package core.colin.basic.utils.json;

import com.blankj.utilcode.util.GsonUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonReader;

import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

/**
 * ================================================
 * Gson Convert
 * ================================================
 */
public class GsonConvert {
    private static final String KEY_CUSTOM = "customGson";

    static {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        GsonUtils.setGson(KEY_CUSTOM, gson);
    }

    //new TypeToken<Map<String, String>>() {}.getType()
    public static <T> T fromJson(byte[] jsonArray, Class<T> type) throws UnsupportedEncodingException {
        String json = new String(jsonArray, "UTF-8");
        return fromJson(json, type);
    }

    public static <T> T fromJson(byte[] jsonArray, Type type) throws UnsupportedEncodingException {
        String json = new String(jsonArray, "UTF-8");
        return fromJson(json, type);
    }

    public static <T> T fromJson(JsonReader reader, Type typeOfT) {
        return GsonUtils.getGson(KEY_CUSTOM).fromJson(reader, typeOfT);
    }

    public static <T> T fromJson(JsonElement json, Class<T> classOfT) {
        return GsonUtils.getGson(KEY_CUSTOM).fromJson(json, classOfT);
    }

    public static <T> T fromJson(String json, Class<T> type) {
        return GsonUtils.getGson(KEY_CUSTOM).fromJson(json, type);
    }

    public static <T> T fromJson(String json, Type type) {
        return GsonUtils.getGson(KEY_CUSTOM).fromJson(json, type);
    }

    public static <T> T fromJson(Reader json, Class<T> classOfT) {
        return GsonUtils.getGson(KEY_CUSTOM).fromJson(json, classOfT);
    }

    public static <T> T fromJson(Reader json, Type typeOfT) {
        return GsonUtils.getGson(KEY_CUSTOM).fromJson(json, typeOfT);
    }

    public static String toJson(Object src) {
        return GsonUtils.getGson(KEY_CUSTOM).toJson(src);
    }

    public static String toJson(Object src, Type typeOfSrc) {
        return GsonUtils.getGson(KEY_CUSTOM).toJson(src, typeOfSrc);
    }


}