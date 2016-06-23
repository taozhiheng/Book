package util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by taozhiheng on 15-7-22.
 *
 */
public class FileUtil {

    public static byte[] getBytesFromFile(File f){
        if (f == null){
            return null;
        }
        try {
            FileInputStream stream = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = stream.read(b)) != -1)
                out.write(b, 0, n);
            stream.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e){
        }
        return null;
    }

    public static String saveBitmapToFile(Bitmap bitmap, Context context, String fileName) {
        String filepath = null;
        // 图片存储路径
        String SavePath = Environment.getExternalStorageDirectory().getPath() + "/Pokebook/Avatar";
        // 保存Bitmap
        try {
            File path = new File(SavePath);
            // 文件
            if(fileName == null) {
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");// 获取当前时间，进一步转化为字符串
                Date date = new Date();
                String str = format.format(date); // 保存文件名为"Picture+时间"
                fileName = "Picture" + str ; //
            }
            fileName += ".png";
            filepath = SavePath + "/" + fileName;
            File file = new File(filepath);
            if (!path.exists()) {
                path.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            if (null != fos) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }
            // 通知系统刷新相册，否则点击相册后，找不到该文件，除非mount SD卡
            Uri localUri = Uri.fromFile(file);
            Intent localIntent = new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
            context.sendBroadcast(localIntent);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return filepath;

    }

}
