package io.github.pepelu.asmtarget.tackmethod;

import android.util.Log;

/**
 * Created by sly on 2019-04-24.
 */
public class TrackMethodUtil {
    private static final String TAG = "[TrackMethodUtil]:";

    /**
     * 无参、无返回值
     */
    public static void f1() {
        Log.i(TAG, "f1: ");
    }

    public static void f2(int i) {
        Log.i(TAG, "f2: " + i);
    }

    public static void f3(String s1, String s2) {
        Log.i(TAG, "f3: s1=" + s1 + ",s2=" + s2);
    }

    public static void f4(int i, String s) {
        Log.i(TAG, "f4: i=" + i + "s,=" + s);
    }

    public static void f5(String s, int i) {
        Log.i(TAG, "f5: s=" + s + ",i=" + i);
    }

    public static void f6(int i, String s, int j) {
        Log.i(TAG, "f6: i=" + i + ",s=" + s + ",j=" + j);
    }

    public static void f7(int ret) {
        Log.i(TAG, "f7: " + ret);
    }
}
