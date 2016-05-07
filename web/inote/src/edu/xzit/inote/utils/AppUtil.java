package edu.xzit.inote.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class AppUtil {

	private static final String TAG = AppUtil.class.getSimpleName();

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
		// String date_s = "2011-01-18 00:00:00.0";

		// *** note that it's "yyyy-MM-dd hh:mm:ss" not "yyyy-mm-dd hh:mm:ss"
		// SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		// Date date = dt.parse(date_s);

		// *** same for the format String below
		SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		return dt1.format(date);
	}

	/**
	 * 格式化时间，有秒
	 * @return
	 */
	public static String getDateStringWithSecond() {
		// String date_s = "2011-01-18 00:00:00.0";

		// *** note that it's "yyyy-MM-dd hh:mm:ss" not "yyyy-mm-dd hh:mm:ss"
		// SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		// Date date = dt.parse(date_s);

		// *** same for the format String below
		SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date date = new Date();
		return dt1.format(date);
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
