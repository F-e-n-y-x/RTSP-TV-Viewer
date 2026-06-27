package com.example.rtspviewer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Launcher entry point. Kept as a tiny router so the LEANBACK_LAUNCHER intent
 * filter has a stable home; all real work (URL entry + playback) lives in
 * [PlayerActivity], which reads the saved URL from preferences itself.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, PlayerActivity::class.java))
        finish()
    }
}
