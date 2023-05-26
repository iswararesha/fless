package com.resha.fless.ui.material

import android.media.MediaPlayer
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.resha.fless.databinding.ActivityVideoBinding


class VideoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoBinding
    private lateinit var video: String

    companion object{
        const val VIDEO = "image"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        video = intent.getStringExtra(VIDEO) as String

        showLoading(true)
        setupView()
    }

    private fun setupView(){
        supportActionBar?.hide()


        val uri = Uri.parse(video)
        binding.vvCourseIntro.setVideoURI(uri)

        val mediaController = MediaController(this)
        mediaController.setAnchorView(binding.vvCourseIntro)
        mediaController.setMediaPlayer(binding.vvCourseIntro)
        binding.vvCourseIntro.setMediaController(mediaController)
        binding.vvCourseIntro.start()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mediaController.addOnUnhandledKeyEventListener { v: View?, event: KeyEvent ->
                if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                    mediaController.hide()
                }
                true
            }
        }

        binding.vvCourseIntro.setOnPreparedListener(OnPreparedListener { mp ->
            mp.start()
            mp.setOnInfoListener(MediaPlayer.OnInfoListener { mp, what, extra ->
                when (what) {
                    MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
                        showLoading(false)
                        return@OnInfoListener true
                    }
                    MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                        showLoading(true)
                        return@OnInfoListener true
                    }
                    MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                        showLoading(false)
                        return@OnInfoListener true
                    }
                }
                false
            })
        })
    }

    private fun showLoading(isLoading: Boolean) {
        if(isLoading){
            binding.loading.visibility = View.VISIBLE
        }else{
            binding.loading.visibility = View.GONE
        }
    }
}