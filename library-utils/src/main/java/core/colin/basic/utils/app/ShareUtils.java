package core.colin.basic.utils.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.widget.ImageView;

import java.io.File;

import core.colin.basic.utils.display.BitmapUtils;

/**
 *
 */
public class ShareUtils {

    public static void setQrCodeImageBitmap(ImageView imageView, String qrCode) {
        imageView.setImageBitmap(createQrCodeImageBitmap(qrCode));
    }

    public static Bitmap createQrCodeImageBitmap(String qrCode) {
        try {
            if (qrCode.startsWith("data:image/png;base64,")) {
                qrCode = qrCode.replace("data:image/png;base64,", "");
            } else if (qrCode.startsWith("data:image/jpeg;base64,")) {
                qrCode = qrCode.replace("data:image/jpeg;base64,", "");
            } else if (qrCode.startsWith("data:image/jpg;base64,")) {
                qrCode = qrCode.replace("data:image/jpg;base64,", "");
            }
            byte[] decodedString = Base64.decode(qrCode, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void shareBitmap(Context context, Bitmap bitmap) {
        File file = BitmapUtils.saveBitmapToCache(bitmap, Bitmap.CompressFormat.PNG);
        bitmap.recycle();
        shareImageFile(context, file);
    }

    public static void shareImageFile(Context context, File file) {
        if (file == null) {
            return;
        }
        try {
            Uri shareUri = IntentUtils.getFileUri(file);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, shareUri);
            intent = Intent.createChooser(intent, "分享到");
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void shareLink(Context context, CharSequence url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, url);
        intent.setType("text/plain");
        intent = Intent.createChooser(intent, "分享到");
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }
}
