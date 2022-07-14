package edu.xzit.inote.utils;


import android.content.Context;  
import android.os.Handler;  
import android.os.Looper;  
import android.view.Gravity;
import android.widget.Toast;  

/**    
 * Toast util class.    
 * from internet
 * 目前支持的显示方位： 1.屏幕居中显示  2.屏幕底部显示
 */   
public class ToastUtil {  
	private static final String TAG = ToastUtil.class.getCanonicalName();
	
    private static Handler handler = new Handler(Looper.getMainLooper());  
  
    private static Toast toast = null;  
      
    private static Object synObj = new Object();  
  
    public static void showMessage(final Context act, final String msg) {  
        showMessage(act, msg, Toast.LENGTH_SHORT, false);  
    }  
  
    public static void showMessage(final Context act, final int msg) {  
        showMessage(act, msg, Toast.LENGTH_SHORT, false);  
    }  
    
    public static void showMessage(final Context act, final String msg, boolean showInCenter) {  
        showMessage(act, msg, Toast.LENGTH_SHORT, showInCenter);  
    }  
  
    public static void showMessage(final Context act, final int msg, boolean showInCenter) {  
        showMessage(act, msg, Toast.LENGTH_SHORT, showInCenter);  
    }  
  
	private static void showMessage(final Context act, final String msg,  
            final int len, final boolean showInCenter) {  
        new Thread(new Runnable() {  
            public void run() {  
                handler.post(new Runnable() {  
                    @Override  
                    public void run() {  
                        synchronized (synObj) {  
                            if (toast != null) {  
//                                toast.cancel();  
                                toast.setText(msg);  
                                toast.setDuration(len);  
                                if (showInCenter) {
                                	toast.setGravity(Gravity.CENTER, 0, 0);
								} else {
                                	toast.setGravity(Gravity.BOTTOM, 0, 0);
								}
                            } else {  
                                toast = Toast.makeText(act, msg, len);  
                                if (showInCenter) {
                                	toast.setGravity(Gravity.CENTER, 0, 0);
								} else {
                                	toast.setGravity(Gravity.BOTTOM, 0, 0);
								}
                            }  
                            toast.show();  

                        }  
                    }  
                });  
            }  
        }).start();  
    }  
  
  
    private static void showMessage(final Context act, final int msg,  
            final int len, final boolean showInCenter) {  
        new Thread(new Runnable() {  
            public void run() {  
                handler.post(new Runnable() {  
                    @Override  
                    public void run() {  
                        synchronized (synObj) {  
                            if (toast != null) {  
                                toast.cancel();  
                                toast.setText(msg);  
                                toast.setDuration(len);  
                                if (showInCenter) {
                                	toast.setGravity(Gravity.CENTER, 0, 0);
								} else {
                                	toast.setGravity(Gravity.BOTTOM, 0, 0);
								}
                            } else {  
                                toast = Toast.makeText(act, msg, len);  
                            }  
                            toast.show();  
                        }  
                    }  
                });  
            }  
        }).start();  
    }  
  
}  
