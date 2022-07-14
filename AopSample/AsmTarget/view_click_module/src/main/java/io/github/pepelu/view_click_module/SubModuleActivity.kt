package io.github.pepelu.view_click_module

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Log

import kotlinx.android.synthetic.main.activity_sub_module.*
import kotlinx.android.synthetic.main.content_sub_module.*

class SubModuleActivity : AppCompatActivity() {
    private val TAG = "Sub_TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_module)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        btn_1.setOnClickListener {
            Log.i(TAG, "click !!")
        }
    }

}
