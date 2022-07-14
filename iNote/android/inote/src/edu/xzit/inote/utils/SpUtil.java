package edu.xzit.inote.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SpUtil {


	public static String getStringValue(Context context, String key, String defaultVaule) {
		SharedPreferences sp= context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		String result="";
		if (null!=sp) {
			result=sp.getString(key, defaultVaule);
		}
		return result;
	}
	
	public static void setStringValue(Context context, String key, String vaule){
		SharedPreferences sp= context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		Editor edit=sp.edit();
		edit.putString(key, vaule);
		edit.commit();
	}
	
	public static int getIntValue(Context context, String key, int defaultVaule) {
		SharedPreferences sp= context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		int result=0;
		if (null!=sp) {
			result=sp.getInt(key, defaultVaule);
		}
		return result;
	}
	
	public static void setIntValue(Context context, String key, int vaule){
		SharedPreferences sp= context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		Editor edit=sp.edit();
	
		edit.putInt(key, vaule);
		edit.commit();
	
	}
	
	public static boolean getBooleanValue(Context context, String key, boolean defaultVaule) {
		SharedPreferences sp= context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		boolean result=false;
		if (null!=sp) {
			result=sp.getBoolean(key, defaultVaule);
		}
		return result;
	}
	
	public static void setBooleanValue(Context context, String key, boolean vaule){
		SharedPreferences sp= context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		Editor edit=sp.edit();
		edit.putBoolean(key, vaule);
		edit.commit();
	}
	
	public static double getDoubleValue(Context context, String key, double defaultVaule) {
		SharedPreferences sp= context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		double result = 0;
		if (null!=sp) {
			result= Double.parseDouble(sp.getString(key, defaultVaule+""));
		}
		return result;
	}
	
	public static void setDoubleValue(Context context, String key, double vaule){
		SharedPreferences sp= context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		Editor edit=sp.edit();
		edit.putString(key, vaule+"");
		edit.commit();
	}
	
	public static double getFloatValue(Context context, String key, float defaultVaule) {
		SharedPreferences sp= context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		float result = 0;
		if (null!=sp) {
			result=sp.getFloat(key, defaultVaule);
		}
		return result;
	}
	
	public static void setFloatValue(Context context, String key, float vaule){
		SharedPreferences sp= context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		Editor edit=sp.edit();
		edit.putFloat(key, vaule);
		edit.commit();
	}
	
	public static double getLongValue(Context context, String key, long defaultVaule) {
		SharedPreferences sp= context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		long result = 0;
		if (null!=sp) {
			result=sp.getLong(key, defaultVaule);
		}
		return result;
	}
	
	public static void setLongValue(Context context, String key, long vaule){
		SharedPreferences sp= context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		Editor edit=sp.edit();
		edit.putLong(key, vaule);
		edit.commit();
	}

	public static void clean(Context context){
		SharedPreferences sp = context.getSharedPreferences(context.getPackageName(),
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.clear();
		editor.commit();
	}
}
