package core.colin.basic.utils;

import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.SDCardUtils;

import java.io.File;

/**
 * 缓存辅助类
 */
public class CacheUtils {

    /**
     * 获取系统默认缓存文件夹
     * 优先返回SD卡中的缓存文件夹
     */
    public static String getCacheDir(String dirName) {
        final String cachePath = getCacheDir();
        return cachePath + File.separator + dirName;
    }

    /**
     * 获取系统默认缓存文件夹
     * 优先返回SD卡中的缓存文件夹
     */
    public static String getCacheDir() {
        File cacheFile = null;
        if (SDCardUtils.isSDCardEnableByEnvironment()) {
            cacheFile = Utils.getAppContext().getExternalCacheDir();
        }
        if (cacheFile == null) {
            cacheFile = Utils.getAppContext().getCacheDir();
        }
        return cacheFile.getAbsolutePath();
    }

    public static String getFilesDir() {
        return Utils.getAppContext().getFilesDir().getAbsolutePath();
    }

    /**
     * 获取系统默认缓存文件夹内的缓存大小
     */
    public static String getTotalCacheSize() {
        long cacheSize = FileUtils.getLength(Utils.getAppContext().getCacheDir());
        if (SDCardUtils.isSDCardEnableByEnvironment()) {
            cacheSize += FileUtils.getLength(Utils.getAppContext().getExternalCacheDir());
        }
        return CommonUtils.formatSize(cacheSize);
    }

    /**
     * 清除系统默认缓存文件夹内的缓存
     */
    public static void clearAllCache() {
        FileUtils.delete(Utils.getAppContext().getCacheDir());
        if (SDCardUtils.isSDCardEnableByEnvironment()) {
            FileUtils.delete(Utils.getAppContext().getExternalCacheDir());
        }
    }

    /**
     * 保存base64图片到本地目录
     *
     * @param imageBase64  base64图片
     * @param saveDir      文件目录
     * @param saveFileName 文件名
     * @return
     */
    public static String saveBase64Picture(String imageBase64, String saveDir, String saveFileName) {
        File dirFile = new File(saveDir);
        if (!dirFile.exists()) {
            if (!dirFile.mkdirs()) {
                return null;
            }
        }
        File imgFile = new File(dirFile, saveFileName);
        if (imgFile.exists()) {
            imgFile.delete();
        }
        final byte[] datas = EncodeUtils.base64Decode(imageBase64);
        final boolean result = FileIOUtils.writeFileFromBytesByStream(imgFile, datas);
        return result ? imgFile.getAbsolutePath() : null;
    }


}