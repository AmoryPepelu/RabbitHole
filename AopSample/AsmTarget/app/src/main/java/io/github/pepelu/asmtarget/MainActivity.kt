package io.github.pepelu.asmtarget

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import io.github.pepelu.asmtarget.tackmethod.TrackMethodTarget
import io.github.pepelu.asmtarget.trackpkg.Track2
import io.github.pepelu.asmtarget.trackpkg.ViewClickImpl
import io.github.pepelu.track_util.TrackViewMethod
import io.github.pepelu.view_click_jar_lib.TestViewClick
import io.github.pepelu.view_click_module.SubModuleActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {
    private val TAG = "Main_Tag"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        btn_1.setOnClickListener { view ->
            Log.i(TAG, "main btn 1 click")
        }

        TestViewClick().setViewClick(btn_2)

        btn3.setOnClickListener {
            startActivity(Intent(this, SubModuleActivity::class.java))
        }

        btn4.setOnClickListener(ViewClickImpl())

        Track2()

        with(TrackMethodTarget()) {
            f1()
            f2(12)
            f3("amory", "pepelu")
            f4(6, "cc")
            f5("jojo", 21)
            f6(233, "age", 90)
            f7()
        }
    }

    @TrackViewMethod
    fun clickHandler(v: View) {
        Log.i(TAG, "clickHandler !")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
