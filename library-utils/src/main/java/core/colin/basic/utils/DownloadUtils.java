package core.colin.basic.utils;

import static android.content.Context.DOWNLOAD_SERVICE;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;

import androidx.core.content.FileProvider;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.PathUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import core.colin.basic.utils.app.IntentUtils;

/**
 * 版本更新下载apk
 */
public class DownloadUtils {

    private DownloadUtils(String url, DownloadListener listener) {
        new DownloadApk(listener).execute(url);
    }

    public static DownloadUtils download(String url, DownloadListener listener) {
        return new DownloadUtils(url, listener);
    }

    public static void installApk(Context context, String filePath) {
        if (filePath == null) {
            return;
        }
        File file = new File(filePath);
        installApk(context, file);
    }

    public static void installApk(Context context, File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(context, Utils.getAppContext().getPackageName() + ".fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }

    public interface DownloadListener {
        void onPreExecute();

        void onDownloadLength(int length);

        void onProgressUpdate(int progress);

        void onPostExecute(File apk);
    }

    /**
     * 通过路径去下载
     **/
    static class DownloadApk extends AsyncTask<String, Integer, File> {

        private final DownloadListener downloadListener;

        DownloadApk(DownloadListener listener) {
            this.downloadListener = listener;
        }

        @Override
        protected File doInBackground(String... params) {
            File file = null;
            try {
                URL url = new URL(params[0]);
                try {
                    final String fileName = Utils.getAppContext().getPackageName() + ".apk";
                    String parentPath;
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        parentPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                    } else {
                        parentPath = Environment.getDownloadCacheDirectory().getAbsolutePath();
                    }
                    File tmpFile = new File(parentPath);
                    if (!tmpFile.exists()) {
                        tmpFile.mkdirs();
                    }
                    file = new File(parentPath, fileName);
                    if (!file.exists()) {
                        file.createNewFile();
                    }

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Accept-Encoding", "identity");
                    int length = conn.getContentLength();
                    downloadListener.onDownloadLength(length);
                    InputStream is = conn.getInputStream();
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] buf = new byte[256];
                    conn.connect();
                    double count = 0;
                    if (conn.getResponseCode() >= 400) {
                    } else {
                        double cou = 0;
                        while (count <= 100) {
                            if (is != null) {
                                int numRead = is.read(buf);
                                cou += numRead;
                                int pro = (int) ((cou / length) * 100);
                                if (numRead <= 0) {
                                    break;
                                } else {
                                    publishProgress(pro);
                                    fos.write(buf, 0, numRead);
                                }
                            } else {
                                break;
                            }
                        }
                    }
                    conn.disconnect();
                    fos.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return file;
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            downloadListener.onPostExecute(file);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            downloadListener.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            downloadListener.onProgressUpdate(values[0]);
        }
    }


    public static Map<String, String> sysDownload(String fileName, String extension, String url) {
        if (fileName == null || fileName.trim().isEmpty()) {
            fileName = IntentUtils.getUriFileName(url);
        }
        if (extension == null || extension.trim().isEmpty()) {
            extension = FileUtils.getFileExtension(fileName);
        }
//        Log.d("@@@", "fileName:" + fileName);
//        Log.d("@@@", "extension:" + extension);
        Uri uri = Uri.parse(url);
        DownloadManager downloadManager = (DownloadManager) Utils.getAppContext().getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        // 设置允许使用的网络类型，这里是移动网络和wifi都可以
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        //设置是否允许漫游
        request.setAllowedOverRoaming(true);
        //设置文件类型
        request.setMimeType(getDataType(extension));
        //在通知栏中显示
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle(fileName);
        request.setDescription("正在下载");
        request.setVisibleInDownloadsUi(true);
        // 设置为可被媒体扫描器找到
        request.allowScanningByMediaScanner();
        String documentsPath = PathUtils.getExternalDownloadsPath();
//        Log.d("@@@", "documentsPath:" + documentsPath);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        // 将下载请求放入队列
        long downloadId = downloadManager.enqueue(request);
        Map<String, String> map = new HashMap<>();
        map.put("filePath", documentsPath);
        map.put("fileName", fileName);
        map.put("downloadId", String.valueOf(downloadId));
        return map;
    }

    public static String getDataType(String extension) {
        if (extension.equalsIgnoreCase("m4a") || extension.equalsIgnoreCase("mp3")
                || extension.equalsIgnoreCase("mid")
                || extension.equalsIgnoreCase("xmf") || extension.equalsIgnoreCase("ogg")
                || extension.equalsIgnoreCase("wav")) {
            return "audio/*";
        } else if (extension.equalsIgnoreCase("3gp") || extension.equalsIgnoreCase("mp4")) {
            return "video/*";
        } else if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("gif")
                || extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("jpeg")
                || extension.equalsIgnoreCase("bmp")) {
            return "image/*";
        } else if (extension.equalsIgnoreCase("apk")) {
            return "application/vnd.android.package-archive";
        } else if (extension.equalsIgnoreCase("ppt") || extension.equalsIgnoreCase("pptx")) {
            return "application/vnd.ms-powerpoint";
        } else if (extension.equalsIgnoreCase("xls") || extension.equalsIgnoreCase("xlsx")) {
            return "application/vnd.ms-excel";
        } else if (extension.equalsIgnoreCase("doc") || extension.equalsIgnoreCase("docx")) {
            return "application/msword";
        } else if (extension.equalsIgnoreCase("pdf")) {
            return "application/pdf";
        } else if (extension.equalsIgnoreCase("chm")) {
            return "application/x-chm";
        } else if (extension.equalsIgnoreCase("txt")) {
            return "text/plain";
        } else if (extension.equalsIgnoreCase("html")) {
            return "text/html";
        }
        return "*/*";
    }



}
