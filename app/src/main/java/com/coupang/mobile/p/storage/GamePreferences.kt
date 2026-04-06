package com.coupang.mobile.p.storage

import android.content.Context
import android.content.SharedPreferences

class GamePreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("icce_fishing_prefs", Context.MODE_PRIVATE)

    var highestUnlockedLevel: Int
        get() = prefs.getInt(KEY_HIGHEST_UNLOCKED, 1)
        set(value) = prefs.edit().putInt(KEY_HIGHEST_UNLOCKED, value).apply()

    var musicEnabled: Boolean
        get() = prefs.getBoolean(KEY_MUSIC, true)
        set(value) = prefs.edit().putBoolean(KEY_MUSIC, value).apply()

    var soundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND, value).apply()

    var bestScore: Int
        get() = prefs.getInt(KEY_BEST_SCORE, 0)
        set(value) = prefs.edit().putInt(KEY_BEST_SCORE, value).apply()

    var totalWins: Int
        get() = prefs.getInt(KEY_TOTAL_WINS, 0)
        set(value) = prefs.edit().putInt(KEY_TOTAL_WINS, value).apply()

    var totalGamesPlayed: Int
        get() = prefs.getInt(KEY_TOTAL_PLAYED, 0)
        set(value) = prefs.edit().putInt(KEY_TOTAL_PLAYED, value).apply()

    fun unlockNextLevel(currentLevel: Int) {
        val next = currentLevel + 1
        if (next > highestUnlockedLevel && next <= 30) {
            highestUnlockedLevel = next
        }
    }

    companion object {
        private const val KEY_HIGHEST_UNLOCKED = "highest_unlocked_level"
        private const val KEY_MUSIC = "music_enabled"
        private const val KEY_SOUND = "sound_enabled"
        private const val KEY_BEST_SCORE = "best_score"
        private const val KEY_TOTAL_WINS = "total_wins"
        private const val KEY_TOTAL_PLAYED = "total_games_played"
    }
}
