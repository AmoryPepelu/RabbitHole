package io.github.pepelu.view_click_jar_lib;

import android.util.Log;
import android.view.View;

/**
 * Created by sly on 2019-04-22.
 */
public class TestViewClick {
    public void setViewClick(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("TestViewClick", "onclick here!!");
            }
        });
    }
}
