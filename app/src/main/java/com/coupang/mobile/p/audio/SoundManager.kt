package com.coupang.mobile.p.audio

import android.content.Context
import android.media.MediaPlayer
import com.coupang.mobile.p.R
import com.coupang.mobile.p.storage.GamePreferences

class SoundManager(private val context: Context, private val prefs: GamePreferences) {

    private var mediaPlayer: MediaPlayer? = null
    private var isPaused = false

    fun startMusic() {
        if (!prefs.musicEnabled) return
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.game_music).apply {
                isLooping = true
                setVolume(0.5f, 0.5f)
            }
        }
        if (!isPlaying()) {
            mediaPlayer?.start()
            isPaused = false
        }
    }

    fun stopMusic() {
        mediaPlayer?.let {
            if (it.isPlaying) it.pause()
            isPaused = true
        }
    }

    fun releaseMusic() {
        mediaPlayer?.release()
        mediaPlayer = null
        isPaused = false
    }

    fun onMusicToggle(enabled: Boolean) {
        prefs.musicEnabled = enabled
        if (enabled) startMusic() else stopMusic()
    }

    fun playWinSound() {
        if (!prefs.soundEnabled) return
        playOneShot(R.raw.level_win)
    }

    fun playLoseSound() {
        if (!prefs.soundEnabled) return
        playOneShot(R.raw.level_lose)
    }

    private fun playOneShot(resId: Int) {
        try {
            MediaPlayer.create(context, resId)?.apply {
                setOnCompletionListener { it.release() }
                start()
            }
        } catch (_: Exception) {}
    }

    fun onSoundToggle(enabled: Boolean) {
        prefs.soundEnabled = enabled
    }

    fun resumeIfNeeded() {
        if (prefs.musicEnabled) startMusic()
    }

    fun pauseIfPlaying() {
        if (isPlaying()) {
            mediaPlayer?.pause()
            isPaused = true
        }
    }

    private fun isPlaying(): Boolean = try {
        mediaPlayer?.isPlaying == true
    } catch (_: Exception) {
        false
    }
}
