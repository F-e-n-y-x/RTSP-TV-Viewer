package com.example.rtspviewer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

class MainActivity : AppCompatActivity(), RtspUrlDialogFragment.RtspUrlListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val url = prefs.getString("rtsp_url", null)

        if (url.isNullOrBlank()) {
            RtspUrlDialogFragment().show(supportFragmentManager, "rtsp_dialog")
        } else {
            launchPlayer(url)
        }
    }

    override fun onUrlEntered(url: String) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.edit().putString("rtsp_url", url).apply()
        launchPlayer(url)
    }

    private fun launchPlayer(url: String) {
        startActivity(Intent(this, PlayerActivity::class.java).apply {
            putExtra("url", url)
        })
        finish()
    }
}
