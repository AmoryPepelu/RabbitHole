package github.amorypepelu.ffmpeg.mediaplayer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.Window
import github.amorypepelu.ffmpeg.R
import github.amorypepelu.ffmpeg.avplayer.NativePlayer
import github.amorypepelu.ffmpeg.config.MediaPath

/**
 * Created by sly on 2019/2/23.
 */
class PlayMediaActivity : AppCompatActivity(), SurfaceHolder.Callback {

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        NativePlayer.setSurface(holder!!.surface)
        NativePlayer.prepareAsync()
    }

    private lateinit var mSurfaceViewHolder: SurfaceHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.video_player_activity)

        //init player
        NativePlayer.createMediaPlayer()
        val url = MediaPath.SouthParkMp4
        NativePlayer.setDataSource(url)
        NativePlayer.setEventCallback(object : MPEventCallback() {
            override fun onPrepared() {
                NativePlayer.start()
            }
        })

        val surfaceView = findViewById<SurfaceView>(R.id.surfaceView)
        surfaceView.keepScreenOn = true
        mSurfaceViewHolder = surfaceView.holder
        mSurfaceViewHolder.addCallback(this)
    }

    override fun onPause() {
        super.onPause()
        NativePlayer.pause()
    }
}