package github.amorypepelu.ffmpeg

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.Window
import github.amorypepelu.ffmpeg.avplayer.NativePlayer
import github.amorypepelu.ffmpeg.config.MediaPath

/**
 * Created by sly on 2019/2/25.
 */
class PlayAudioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setSupportActionBar(null)
        setContentView(R.layout.audio_player_activity)
    }

    fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_start -> {
                NativePlayer.playAudio(MediaPath.StarButterflyMp3)
//                NativePlayer.playAudio(MediaPath.AudioNetLoser)
            }
            R.id.btn_stop -> {
                NativePlayer.stopAudio()
            }
            R.id.btn_start_audio_track -> {
                Thread(Runnable {
                    //同步播放
                    NativePlayer.playAudioByAudioTrack(MediaPath.StarButterflyMp3)
                }).start()
            }
        }
    }
}