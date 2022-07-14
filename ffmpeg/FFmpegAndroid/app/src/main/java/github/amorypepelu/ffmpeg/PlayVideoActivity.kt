package github.amorypepelu.ffmpeg

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.Window
import github.amorypepelu.ffmpeg.avplayer.NativePlayer
import github.amorypepelu.ffmpeg.config.MediaPath

/**
 * Created by sly on 2019/2/23.
 */
class PlayVideoActivity : AppCompatActivity(), SurfaceHolder.Callback {

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        Thread(Runnable {
            val url = MediaPath.SouthParkMp4
//            val url = MediaPath.VideoNetLoser
//            val url = MediaPath.VideoNet2
            NativePlayer.playVideo(url, holder!!.surface)
        }).start()
    }

    private lateinit var mSurfaceViewHolder: SurfaceHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.video_player_activity)

        val surfaceView = findViewById<SurfaceView>(R.id.surfaceView)
        surfaceView.keepScreenOn = true
        mSurfaceViewHolder = surfaceView.holder
        mSurfaceViewHolder.addCallback(this)
    }
}