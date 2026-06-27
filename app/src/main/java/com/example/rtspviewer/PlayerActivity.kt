package com.example.rtspviewer

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.preference.PreferenceManager
import com.example.rtspviewer.databinding.ActivityPlayerBinding

/**
 * Full-screen RTSP player tuned for low-latency live playback, with an
 * on-screen, D-pad navigable options menu (Change URL / Reconnect / Exit)
 * and automatic reconnection on stream errors.
 */
@UnstableApi
class PlayerActivity : AppCompatActivity(), RtspUrlDialogFragment.RtspUrlListener {

    private lateinit var binding: ActivityPlayerBinding
    private var player: ExoPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var retryCount = 0

    private val prefs by lazy { PreferenceManager.getDefaultSharedPreferences(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        binding.btnChangeUrl.setOnClickListener { hideMenu(); showUrlDialog() }
        binding.btnReconnect.setOnClickListener { hideMenu(); retryCount = 0; startPlayback() }
        binding.btnExit.setOnClickListener { finishAffinity() }
    }

    // Use start/stop so playback follows the activity lifecycle (TV apps are
    // foreground; this also frees the codec when the app is backgrounded).
    override fun onStart() {
        super.onStart()
        startOrPrompt()
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacksAndMessages(null)
        releasePlayer()
    }

    private fun startOrPrompt() {
        val url = prefs.getString(KEY_URL, null)
        if (url.isNullOrBlank()) showUrlDialog() else startPlayback()
    }

    private fun startPlayback() {
        val url = prefs.getString(KEY_URL, null)
        if (url.isNullOrBlank()) {
            showUrlDialog()
            return
        }
        releasePlayer()
        showStatus(getString(R.string.connecting))

        // Small buffers keep us close to the live edge — large default buffers
        // are what make RTSP feel laggy / non-real-time.
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                /* minBufferMs = */ 1_000,
                /* maxBufferMs = */ 4_000,
                /* bufferForPlaybackMs = */ 250,
                /* bufferForPlaybackAfterRebufferMs = */ 1_000
            )
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()

        val exo = ExoPlayer.Builder(this)
            .setLoadControl(loadControl)
            // Don't let the player wait to sync A/V before rendering the first frame.
            .build()
        binding.playerView.player = exo

        val forceTcp = prefs.getBoolean(KEY_TCP, true)
        val source = RtspMediaSource.Factory()
            .setForceUseRtpTcp(forceTcp)   // TCP avoids UDP packet-loss stutter
            .setTimeoutMs(10_000)
            .createMediaSource(MediaItem.fromUri(url))

        exo.addListener(playerListener)
        exo.setMediaSource(source)
        exo.playWhenReady = true
        exo.prepare()
        player = exo

        binding.urlLabel.text = url
    }

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(state: Int) {
            when (state) {
                Player.STATE_BUFFERING -> showStatus(getString(R.string.buffering))
                Player.STATE_READY -> {
                    retryCount = 0
                    hideStatus()
                    // If we've drifted behind live (e.g. after a rebuffer), jump
                    // back to the newest frame to stay real-time.
                    seekToLiveEdge()
                }
                Player.STATE_ENDED -> showStatus(getString(R.string.stream_ended))
                Player.STATE_IDLE -> Unit
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            if (retryCount < MAX_RETRIES) {
                retryCount++
                showStatus(getString(R.string.reconnecting, retryCount, MAX_RETRIES))
                handler.postDelayed({ startPlayback() }, RETRY_DELAY_MS)
            } else {
                showStatus(getString(R.string.playback_failed, error.errorCodeName))
                showMenu()
            }
        }
    }

    private fun seekToLiveEdge() {
        val p = player ?: return
        val duration = p.duration
        if (duration != C.TIME_UNSET && duration > 0) {
            p.seekTo(duration)
        }
    }

    // --- On-screen menu --------------------------------------------------

    private val isMenuVisible: Boolean
        get() = binding.overlayMenu.visibility == View.VISIBLE

    private fun showMenu() {
        binding.overlayMenu.visibility = View.VISIBLE
        binding.btnChangeUrl.requestFocus()
    }

    private fun hideMenu() {
        binding.overlayMenu.visibility = View.GONE
    }

    private fun showStatus(message: String) {
        binding.statusText.text = message
        binding.statusText.visibility = View.VISIBLE
    }

    private fun hideStatus() {
        binding.statusText.visibility = View.GONE
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                // BACK toggles the menu instead of instantly exiting, so the
                // user always has a way back to options. Exit is in the menu.
                if (isMenuVisible) hideMenu() else showMenu()
                return true
            }
            KeyEvent.KEYCODE_MENU,
            KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_ENTER,
            KeyEvent.KEYCODE_BUTTON_A -> {
                // Open the menu on OK only when it's hidden; when it's open let
                // the focused button handle the click normally.
                if (!isMenuVisible) {
                    showMenu()
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    // --- URL dialog ------------------------------------------------------

    private fun showUrlDialog() {
        val current = prefs.getString(KEY_URL, "")
        val tcp = prefs.getBoolean(KEY_TCP, true)
        RtspUrlDialogFragment.newInstance(current, tcp)
            .show(supportFragmentManager, "rtsp_dialog")
    }

    override fun onUrlEntered(url: String, forceTcp: Boolean) {
        prefs.edit()
            .putString(KEY_URL, url)
            .putBoolean(KEY_TCP, forceTcp)
            .apply()
        retryCount = 0
        hideMenu()
        startPlayback()
    }

    // --- Cleanup ---------------------------------------------------------

    private fun releasePlayer() {
        player?.let {
            it.removeListener(playerListener)
            it.release()
        }
        player = null
        binding.playerView.player = null
    }

    companion object {
        const val KEY_URL = "rtsp_url"
        const val KEY_TCP = "force_tcp"
        private const val MAX_RETRIES = 5
        private const val RETRY_DELAY_MS = 2_000L
    }
}
