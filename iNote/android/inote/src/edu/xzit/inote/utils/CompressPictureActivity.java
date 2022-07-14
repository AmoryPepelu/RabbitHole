package edu.xzit.inote.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class CompressPictureActivity extends Activity {

    private final String TAG = CompressPictureActivity.class.getSimpleName();
    private ImageView mImageView;
    private Bitmap mBitmap;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        String url = Environment.getExternalStorageDirectory() + File.separator + "pepelu" + File.separator;
        final String filePath = url + "compress.jpg";
        Log.d("pepelu", "filePath::" + filePath);
        File file = new File(filePath);
        Log.d("pepelu", "file exit::" + (file == null ? null : file.exists()));
        mBitmap = getSmallBitmap(filePath);
        mImageView.setImageBitmap(mBitmap);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //保存
                // saveBitmapToFile(getSmallBitmap(filePath), getFilesDir().getPath());
                compressBitmap(CompressPictureActivity.this, filePath, Environment.getExternalStorageDirectory() + File.separator + "dds.jpg");
            }
        });

        Log.d("pepelu", "this.getPackageResourcePath()::" + this.getPackageResourcePath());
        Log.d("pepelu", "getPackageCodePath()::" + getPackageCodePath());
        Log.d("pepelu", "getFilesDir()::" + getFilesDir());
    }

    private Bitmap getSmallBitmap(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = caculateInSampleSize(options, 480, 800);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    private int caculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSimpleSize = 1;
        if (width > reqWidth || height > reqHeight) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSimpleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSimpleSize;
    }

    private String bitmapToString(String filePath) {
        Bitmap bitmap = getSmallBitmap(filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    private void saveBitmapToFile(Bitmap bitmap, String filePath) {
        //String url = this.getFilesDir() + File.separator;
        //String filePath2 = url + "2.jpg";
        File file = new File(filePath);
        // File f = new File(Environment.getExternalStorageDirectory()                 , fileName);
        FileOutputStream fOut = null;
        try {
            file.createNewFile();
            fOut = new FileOutputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, fOut);
        try {
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 图片压缩，默认压缩后保存位置/data/data/packagename/files/
     * 若要自定义存储文件路径，需要先把文件创建好
     *
     * @param context
     * @param inFilePath  原图片位置
     * @param outFilePath 压缩后图片位置
     * @return
     */
    private String compressBitmap(Context context, String inFilePath, String outFilePath) {
        if (!checkFileExit(outFilePath)) {
            outFilePath = context.getFilesDir().getPath() + File.separator + "compressedPic.jpg";
            Log.d("pepelu", "outFilePath::" + outFilePath);
        }
        saveBitmapToFile(getSmallBitmap(inFilePath), outFilePath);
        return outFilePath;
    }

    /**
     * 检查文件是否存在
     *
     * @param filePath
     * @return
     */
    private boolean checkFileExit(String filePath) {
        if (filePath == null) {
            return false;
        }
        File file = new File(filePath);
        return file.exists();
    }
}
