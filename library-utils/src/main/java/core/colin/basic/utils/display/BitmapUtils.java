package core.colin.basic.utils.display;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.StringUtils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import core.colin.basic.utils.Utils;
import core.colin.basic.utils.app.IntentUtils;
import core.colin.basic.utils.CacheUtils;

/**
 *
 */
public class BitmapUtils {

    public static final String ANCHOR_PATH = "anchor";

    /**
     * @param bmp     获取的bitmap数据
     * @param picName 自定义的图片名
     */
    public static File saveGallery2(Bitmap bmp, Bitmap.CompressFormat format, String picName) {
        FileOutputStream fos = null;
        try {
            File gallery = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!gallery.exists()) {
                gallery.mkdirs();
            }
            String fileName_pix = "" + System.currentTimeMillis();
            if (!StringUtils.isEmpty(picName)) {
                fileName_pix = picName;
            }
            String fileName = fileName_pix + "." + format.name().toLowerCase();
            File file = new File(gallery, fileName);
            fos = new FileOutputStream(file.getPath());
            boolean isSuccess = bmp.compress(format, 100, fos);
            fos.flush();

            //保存图片后发送广播通知更新数据库
            Uri uri = IntentUtils.getFileUri(file);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(uri);
            Utils.getAppContext().sendBroadcast(intent);
            if (isSuccess) {
                return file;
            } else {
                return null;
            }

        } catch (Exception e) {
            e.getStackTrace();
            return null;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param bmp     获取的bitmap数据
     * @param picName 自定义的图片名
     */
    public static boolean saveGallery(Bitmap bmp, String picName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, picName);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
        } else {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File file = new File(dir, picName);
            contentValues.put(MediaStore.MediaColumns.DATA, file.getPath());
        }
        Uri insertUri = Utils.getAppContext()
                .getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        if (insertUri == null) {
            return false;
        }
        OutputStream outputStream = null;
        try {
            outputStream = Utils.getAppContext()
                    .getContentResolver()
                    .openOutputStream(insertUri);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            return true;
        } catch (FileNotFoundException e) {
            return false;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static File saveBitmapToCache(Bitmap bm, Bitmap.CompressFormat format) {
        return saveBitmapToCache(bm, null, format);
    }


    public static File saveBitmapToCache(Bitmap bm, String name, Bitmap.CompressFormat format) {
        FileOutputStream outStream = null;
        try {
            File dir = new File(CacheUtils.getCacheDir());
            if (!dir.exists()) {
                dir.mkdir();
            }
            String fileName_pix = "" + System.currentTimeMillis();
            if (!StringUtils.isEmpty(name)) {
                fileName_pix = name;
            }
            String fileName = fileName_pix + "." + format.name().toLowerCase();

            File f = new File(dir, fileName);
            outStream = new FileOutputStream(f);
            bm.compress(format, 100, outStream);
            outStream.flush();
            outStream.close();
//            Log.d("utils", "f->" + f.getAbsolutePath());
            return f;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Nullable
    public static Bitmap getBitmapFromPath(Context context, String path) {
        Uri uri = getImageContentUri(context, path);
        if (uri == null) {
            return null;
        }
        return getBitmapFromUri(context, uri);
    }

    // 通过uri加载图片
    @Nullable
    public static Bitmap getBitmapFromUri(Context context, Uri uri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
            if (parcelFileDescriptor == null) {
                return null;
            }
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Nullable
    public static Uri getImageContentUri(Context context, String path) {
        try (Cursor cursor = context.getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID},
                        MediaStore.Images.Media.DATA + "=? ",
                        new String[]{path}, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(MediaStore.MediaColumns._ID);
                if (index >= 0) {
                    int id = cursor.getInt(index);
                    Uri baseUri = Uri.parse("content://media/external/images/media");
                    return Uri.withAppendedPath(baseUri, "" + id);
                }
            } else {
                if (new File(path).exists()) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DATA, path);
                    return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                }
            }
            return null;
        }
    }


    /**
     * bitmap设置透明度
     *
     * @param sourceImg
     * @param number    number-100，0表示完全透明即完全看不到。
     * @return
     */
    public static Bitmap getAlplaBitmap(Bitmap sourceImg, int number) {
        int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];
        sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0, sourceImg.getWidth(), sourceImg.getHeight());
        number = number * 255 / 100;
        for (int i = 0; i < argb.length; i++) {
            int a = Color.alpha(argb[i]);
            if (a == 0) { //透明像素
                continue;
            }
            argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF);
        }
        sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(), sourceImg.getHeight(), Bitmap.Config.ARGB_8888);
        return sourceImg;
    }


    public static Bitmap dealBackground(Bitmap sourceImg) {
        int portraitWidth = sourceImg.getWidth();
        int portraitHeight = sourceImg.getHeight();
        int[] argbs = new int[portraitWidth * portraitHeight];
        sourceImg.getPixels(argbs, 0, portraitWidth, 0, 0, portraitWidth, portraitHeight);// 获得图片的ARGB值
        for (int i = 0; i < argbs.length; i++) {
            int a = Color.alpha(argbs[i]);
            int r = Color.red(argbs[i]);
            int g = Color.green(argbs[i]);
            int b = Color.blue(argbs[i]);
            if (r > 240 && g > 240 && b > 240) {
                argbs[i] = 0x00FFFFFF;
            }
        }
        return Bitmap.createBitmap(argbs, 0, portraitWidth, portraitWidth, portraitHeight, Bitmap.Config.ARGB_4444);

    }


}
