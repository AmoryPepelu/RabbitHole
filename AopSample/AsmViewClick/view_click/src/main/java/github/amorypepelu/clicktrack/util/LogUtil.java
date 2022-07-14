package github.amorypepelu.clicktrack.util;

/**
 * Created by sly on 2019-04-22.
 */
public class LogUtil {
    private static final String TAG = "[AsmViewClick]";

    private static boolean isLogOpen = true;

    public static boolean isIsLogOpen() {
        return isLogOpen;
    }

    public static void setIsLogOpen(boolean isLogOpen) {
        LogUtil.isLogOpen = isLogOpen;
    }

    public static void i(String msg) {
        if (!isLogOpen) return;
        System.out.println(TAG + ":" + msg);
    }
}
