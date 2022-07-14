package io.github.pepelu.asmtarget.trackpkg;

import android.util.Log;
import android.view.View;

/**
 * 因为这个类实现了一个接口，并且 onClick() 方法的desc 是:(Landroid/view/View;)V，所以 onClick() 方法也会被埋点
 */
public class ViewClickImpl implements View.OnClickListener {
    private static final String TAG = "Simple_TAG";

    @Override
    public void onClick(View view) {
        Log.i(TAG, "onClick here 2 ! ");
    }
}
