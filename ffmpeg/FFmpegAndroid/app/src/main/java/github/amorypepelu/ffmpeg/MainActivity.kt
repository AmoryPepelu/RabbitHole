package github.amorypepelu.ffmpeg

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.Window
import github.amorypepelu.ffmpeg.avplayer.NativePlayer
import github.amorypepelu.ffmpeg.mediaplayer.PlayMediaActivity

class MainActivity : AppCompatActivity() {


    companion object {
        private const val TAG = "pepelu_tag"
        private const val CODE_STORAGE = 999
        private val mPermissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setSupportActionBar(null)
        setContentView(R.layout.activity_main)
        checkPermission()
    }

    fun onClick(view: View?) {
        when (view?.id) {
            R.id.av_register_all -> {
                val result = NativePlayer.avRegisterAll()
                Log.i(TAG, "register:$result")
            }
            R.id.play_video -> {
                startAct(PlayVideoActivity::class.java)
            }
            R.id.play_audio -> {
                startAct(PlayAudioActivity::class.java)
            }
            R.id.play_media -> {
                startAct(PlayMediaActivity::class.java)
            }
        }
    }

    private fun startAct(clazz: Class<out Activity>) {
        val intent = Intent(this, clazz)
        startActivity(intent)
    }

    //动态申请权限
    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(mPermissions[0]) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(mPermissions, CODE_STORAGE)
            }
        }
    }
}
