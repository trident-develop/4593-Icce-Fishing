package com.coupang.mobile.p.model

import androidx.compose.ui.geometry.Offset
import com.coupang.mobile.p.R

enum class FishColor(val displayName: String, val colorHex: Long, val drawableRes: Int) {
    RED("RED", 0xFFFF4444, R.drawable.fish),
    ORANGE("ORANGE", 0xFFFF9800, R.drawable.fish_2),
    BLUE("BLUE", 0xFF0005B2, R.drawable.fish_3);
}

data class GameFish(
    val id: Int,
    val color: FishColor,
    var position: Offset,
    var velocity: Offset,
    val size: Float = 80f,
    var wobblePhase: Float = 0f
)

enum class BonusType(val drawableRes: Int, val description: String) {
    HUGE_REDS(R.drawable.huge_reds, "Red fish give double points!"),
    LIL_BLUES(R.drawable.lil_blues, "All fish slowed down!"),
    BIG_ORANGES(R.drawable.big_oranges, "Orange fish give double points!");
}

data class ActiveBonus(
    val type: BonusType,
    val expiresAt: Long
)

data class BonusCard(
    val type: BonusType,
    var position: Offset,
    val spawnedAt: Long
)

enum class GamePhase {
    PLAYING, PAUSED, WON, LOST
}

data class GameState(
    val level: Int,
    val targetColor: FishColor,
    val score: Int = 0,
    val targetScore: Int = 10 + level * 3,
    val lives: Int = 3,
    val maxLives: Int = 3,
    val phase: GamePhase = GamePhase.PLAYING,
    val fishes: List<GameFish> = emptyList(),
    val activeBonus: ActiveBonus? = null,
    val bonusCard: BonusCard? = null
)
