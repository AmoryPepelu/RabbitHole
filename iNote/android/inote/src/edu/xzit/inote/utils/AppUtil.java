package edu.xzit.inote.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

public class AppUtil {

	private static final String TAG = AppUtil.class.getSimpleName();

	// 加载提示
	private static ProgressDialog mProgressDialog;

	/**
	 * MD5加密结果16位
	 * 
	 * @param sourceStr
	 * @return
	 */
	public static String getMD5String(String sourceStr) {
		String result = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(sourceStr.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			result = buf.toString().substring(8, 24);
			// System.out.println("MD5(" + sourceStr + ",32) = " + result);
			// System.out.println("MD5(" + sourceStr + ",16) = " +
			// buf.toString().substring(8, 24));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			Log.d(TAG, "getMD5String::" + e.getMessage());
		}
		return result;
	}

	/**
	 * 检查字符串是否为空
	 * 
	 * @param string
	 * @return 如果字符串为空，返回true，否则发回false
	 */
	public static boolean isEmpty(String string) {
		if (string == null || string.length() == 0)
			return true;

		if (("").equals(string.trim().replaceAll("\t", "").replaceAll("\n", "")
				.replaceAll("\r", "").replaceAll("\f", ""))) {
			return true;
		}
		return false;
	}

	/**
	 * 获取当前时间
	 * 
	 * @return
	 */
	public static String getDateString() {
		android.text.format.DateFormat df = new android.text.format.DateFormat();

		String dateString = android.text.format.DateFormat.format(
				"yyyy-MM-dd hh:mm:ss", new java.util.Date()).toString();
		return dateString;
	}

	/**
	 * 显示dialog
	 * 
	 * @param context
	 */
	public static void showProgressDialog(Context context) {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(context);
			mProgressDialog.setMessage("正在加载...");
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.setCancelable(false);
		}
		mProgressDialog.show();
	}

	/**
	 * 关闭加载进度对话框
	 */
	public static void closeProgressDialog() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}

	/**
	 * 检查文件是否存在
	 *
	 * @param filePath
	 * @return
	 */
	public static boolean checkFileExit(String filePath) {
		if (filePath == null) {
			return false;
		}
		File file = new File(filePath);
		return file.exists();
	}

	/**
	 * 图片压缩，默认压缩后保存位置/data/data/packagename/files/ 若要自定义存储文件路径，需要先把文件创建好
	 *
	 * @param context
	 * @param inFilePath
	 *            原图片位置
	 * @param outFilePath
	 *            压缩后图片位置
	 * @return
	 */
	public static String compressBitmap(Context context, String inFilePath,
			String outFileName) {
		String outFilePath = "";
		if (outFileName == null) {
			return "";
		}
		outFilePath = context.getFilesDir().getPath() + File.separator
				+ outFileName;
		Log.d("pepelu", "outFilePath::" + outFilePath);
		saveBitmapToFile(getSmallBitmap(inFilePath), outFilePath);
		return outFilePath;
	}

	/**
	 * 把bitmap保存到文件系统中
	 * 
	 * @param bitmap
	 * @param filePath
	 */
	public static void saveBitmapToFile(Bitmap bitmap, String filePath) {
		File file = new File(filePath);
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
	 * 把bitmap转换成string
	 * 
	 * @param filePath
	 * @return
	 */
	public static String bitmapToString(String filePath) {
		Bitmap bitmap = getSmallBitmap(filePath);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
		byte[] b = baos.toByteArray();
		return Base64.encodeToString(b, Base64.DEFAULT);
	}

	/**
	 * 计算图片大小
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int caculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int width = options.outWidth;
		final int height = options.outHeight;
		int inSimpleSize = 1;
		if (width > reqWidth || height > reqHeight) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSimpleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSimpleSize;
	}

	/**
	 * 获取小图
	 * 
	 * @param filePath
	 * @return
	 */
	public static Bitmap getSmallBitmap(String filePath) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		options.inSampleSize = caculateInSampleSize(options, 480, 800);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}

	/**
	 * 获取uuid:32位
	 * 
	 * @return
	 */
	public static String getUUID() {
		UUID uuid = UUID.randomUUID();
		String str = uuid.toString();
		// 去掉"-"符号
		String temp = str.substring(0, 8) + str.substring(9, 13)
				+ str.substring(14, 18) + str.substring(19, 23)
				+ str.substring(24);
		return temp;
	}
}
