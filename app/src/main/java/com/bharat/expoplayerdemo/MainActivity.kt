package com.bharat.expoplayerdemo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Util
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import com.bharat.expoplayerdemo.databinding.ActivityMainBinding

@SuppressLint("UnsafeOptInUsageError")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var player: ExoPlayer? = null
    private var playWhenReady = true
    private var mediaItemIndex = 0
    private var playbackPosition = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initializeMediaPlayer() = with(binding) {
        player = ExoPlayer.Builder(this@MainActivity).build().also {
            playerView.player = it
        }

        //todo:add any of the audio file in your raw asset and use it below
        val rr = RawResourceDataSource.buildRawResourceUri(R.raw.reponse_192x_4)
//        val rawResource = RawResourceDataSource(this@MainActivity)
//        rawResource.open(rr)
        val mediaItem = MediaItem.fromUri(rr)
        player?.setMediaItems(listOf(mediaItem), mediaItemIndex, playbackPosition)
        //player?.setMediaItem(mediaItem)
        player?.playWhenReady = playWhenReady
        player?.prepare()
    }


    public override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializeMediaPlayer()
        }
    }

    public override fun onResume() {
        super.onResume()
        //hideSystemUi()
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializeMediaPlayer()
        }
    }

    public override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }


    public override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        player?.let { exoPlayer ->
            playbackPosition = exoPlayer.currentPosition
            mediaItemIndex = exoPlayer.currentMediaItemIndex
            playWhenReady = exoPlayer.playWhenReady
            exoPlayer.release()
        }
        player = null
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.playerView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}