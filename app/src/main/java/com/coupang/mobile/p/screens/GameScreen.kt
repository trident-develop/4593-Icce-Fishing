package com.coupang.mobile.p.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.coupang.mobile.p.R
import com.coupang.mobile.p.audio.SoundManager
import com.coupang.mobile.p.model.ActiveBonus
import com.coupang.mobile.p.model.BonusCard
import com.coupang.mobile.p.model.BonusType
import com.coupang.mobile.p.model.FishColor
import com.coupang.mobile.p.model.GameFish
import com.coupang.mobile.p.model.GamePhase
import com.coupang.mobile.p.storage.GamePreferences
import com.coupang.mobile.p.ui.components.MenuButton
import com.coupang.mobile.p.ui.components.SquareButton
import com.coupang.mobile.p.ui.theme.GameFont
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun GameScreen(
    level: Int,
    prefs: GamePreferences,
    soundManager: SoundManager,
    onHome: () -> Unit,
    onBack: () -> Unit,
    onNextLevel: (Int) -> Unit,
    onReplay: () -> Unit
) {
    val targetScore = 10 + level * 3
    val maxLives = 3
    val baseSpeed = 1.5f + level * 0.15f
    val fishCount = (4 + level / 3).coerceAtMost(12)

    var score by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(maxLives) }
    var phase by remember { mutableStateOf(GamePhase.PLAYING) }
    var targetColor by remember { mutableStateOf(FishColor.entries.random()) }

    val fishes = remember { mutableStateListOf<GameFish>() }
    var activeBonus by remember { mutableStateOf<ActiveBonus?>(null) }
    var bonusCard by remember { mutableStateOf<BonusCard?>(null) }
    var lastBonusTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    var areaWidth by remember { mutableFloatStateOf(0f) }
    var areaHeight by remember { mutableFloatStateOf(0f) }

    val density = LocalDensity.current
    val fishSizeDp = 70.dp
    val fishSizePx = with(density) { fishSizeDp.toPx() }
    val bonusSizePx = with(density) { 80.dp.toPx() }

    var isExitingScreen by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val obs = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE && phase != GamePhase.WON && phase != GamePhase.LOST && !isExitingScreen)
                phase = GamePhase.PAUSED
        }
        lifecycleOwner.lifecycle.addObserver(obs)
        onDispose { lifecycleOwner.lifecycle.removeObserver(obs) }
    }

    BackHandler(enabled = true) {
        isExitingScreen = true
        onBack()
    }

    // Initialize fishes
    LaunchedEffect(Unit) {
        if (fishes.isEmpty()) {
            repeat(fishCount) { i ->
                fishes.add(
                    GameFish(
                        id = i,
                        color = FishColor.entries[i % 3],
                        position = Offset(
                            Random.nextFloat() * 600f + 50f,
                            Random.nextFloat() * 800f + 200f
                        ),
                        velocity = Offset(
                            (Random.nextFloat() - 0.5f) * baseSpeed * 2f,
                            (Random.nextFloat() - 0.5f) * baseSpeed * 1.5f
                        ),
                        wobblePhase = Random.nextFloat() * 6.28f
                    )
                )
            }
        }
    }

    // Change target color periodically
    LaunchedEffect(score) {
        if (score > 0 && score % 5 == 0 && phase == GamePhase.PLAYING) {
            targetColor = FishColor.entries.random()
        }
    }

    // Game loop
    LaunchedEffect(phase) {
        if (phase != GamePhase.PLAYING) return@LaunchedEffect
        while (phase == GamePhase.PLAYING) {
            delay(16L) // ~60fps
            val now = System.currentTimeMillis()
            val speedMult = if (activeBonus?.type == BonusType.LIL_BLUES && now < (activeBonus?.expiresAt ?: 0)) 0.4f else 1f

            // Expire bonus
            activeBonus?.let {
                if (now > it.expiresAt) activeBonus = null
            }

            // Spawn bonus card
            if (bonusCard == null && now - lastBonusTime > 12000 && Random.nextFloat() < 0.01f) {
                bonusCard = BonusCard(
                    type = BonusType.entries.random(),
                    position = Offset(
                        Random.nextFloat() * (areaWidth - 200f).coerceAtLeast(100f) + 50f,
                        Random.nextFloat() * (areaHeight - 400f).coerceAtLeast(200f) + 200f
                    ),
                    spawnedAt = now
                )
            }

            // Expire bonus card after 4 seconds
            bonusCard?.let {
                if (now - it.spawnedAt > 4000) {
                    bonusCard = null
                    lastBonusTime = now
                }
            }

            // Update fish positions
            for (i in fishes.indices) {
                val fish = fishes[i]
                var vx = fish.velocity.x * speedMult
                var vy = fish.velocity.y * speedMult
                var nx = fish.position.x + vx
                var ny = fish.position.y + vy

                val maxX = (areaWidth - fishSizePx).coerceAtLeast(100f)
                val maxY = (areaHeight - fishSizePx).coerceAtLeast(100f)

                if (nx < 0f || nx > maxX) {
                    vx = -vx
                    nx = nx.coerceIn(0f, maxX)
                }
                if (ny < 0f || ny > maxY) {
                    vy = -vy
                    ny = ny.coerceIn(0f, maxY)
                }

                fishes[i] = fish.copy(
                    position = Offset(nx, ny),
                    velocity = Offset(vx / speedMult, vy / speedMult),
                    wobblePhase = fish.wobblePhase + 0.08f
                )
            }
        }
    }

    // Record game stats on end + play sound
    LaunchedEffect(phase) {
        if (phase == GamePhase.WON) {
            soundManager.playWinSound()
            prefs.totalGamesPlayed += 1
            prefs.totalWins += 1
            if (score > prefs.bestScore) prefs.bestScore = score
            prefs.unlockNextLevel(level)
        } else if (phase == GamePhase.LOST) {
            soundManager.playLoseSound()
            prefs.totalGamesPlayed += 1
            if (score > prefs.bestScore) prefs.bestScore = score
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_vertical),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            TopBar(
                level = level,
                score = score,
                targetScore = targetScore,
                lives = lives,
                maxLives = maxLives,
                onBack = {
                    isExitingScreen = true
                    onBack()
                },
                onHome = onHome,
                onPause = { if (phase == GamePhase.PLAYING) phase = GamePhase.PAUSED }
            )

            // Target indicator
            Text(
                text = "Catch ${targetColor.displayName}!",
                fontFamily = GameFont,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(targetColor.colorHex),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Active bonus indicator
            activeBonus?.let { bonus ->
                Text(
                    text = bonus.type.description,
                    fontFamily = GameFont,
                    fontSize = 14.sp,
                    color = Color(0xFFFFD54F),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Game play area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { coords ->
                        areaWidth = coords.size.width.toFloat()
                        areaHeight = coords.size.height.toFloat()
                    }
                    .pointerInput(phase) {
                        if (phase != GamePhase.PLAYING) return@pointerInput
                        detectTapGestures { tapOffset ->
                            // Check bonus card tap
                            bonusCard?.let { card ->
                                if (tapOffset.x in card.position.x..(card.position.x + bonusSizePx) &&
                                    tapOffset.y in card.position.y..(card.position.y + bonusSizePx)
                                ) {
                                    activeBonus = ActiveBonus(
                                        type = card.type,
                                        expiresAt = System.currentTimeMillis() + 8000
                                    )
                                    bonusCard = null
                                    lastBonusTime = System.currentTimeMillis()
                                    return@detectTapGestures
                                }
                            }

                            // Check fish taps (reverse order for top-most first)
                            for (i in fishes.indices.reversed()) {
                                val fish = fishes[i]
                                if (tapOffset.x in fish.position.x..(fish.position.x + fishSizePx) &&
                                    tapOffset.y in fish.position.y..(fish.position.y + fishSizePx)
                                ) {
                                    if (fish.color == targetColor) {
                                        var points = 1
                                        activeBonus?.let { bonus ->
                                            if (bonus.type == BonusType.HUGE_REDS && fish.color == FishColor.RED) points = 2
                                            if (bonus.type == BonusType.BIG_ORANGES && fish.color == FishColor.ORANGE) points = 2
                                        }
                                        score += points
                                        // Respawn fish
                                        fishes[i] = fish.copy(
                                            position = Offset(
                                                Random.nextFloat() * (areaWidth - fishSizePx).coerceAtLeast(10f),
                                                Random.nextFloat() * (areaHeight - fishSizePx).coerceAtLeast(10f)
                                            ),
                                            velocity = Offset(
                                                (Random.nextFloat() - 0.5f) * baseSpeed * 2f,
                                                (Random.nextFloat() - 0.5f) * baseSpeed * 1.5f
                                            )
                                        )
                                        if (score >= targetScore) {
                                            phase = GamePhase.WON
                                        }
                                    } else {
                                        lives -= 1
                                        if (lives <= 0) {
                                            phase = GamePhase.LOST
                                        }
                                    }
                                    break
                                }
                            }
                        }
                    }
            ) {
                // Render fish
                fishes.forEach { fish ->
                    val wobble = sin(fish.wobblePhase) * 5f
                    val scaleWobble = 1f + sin(fish.wobblePhase * 0.7f) * 0.05f
                    val rotation = sin(fish.wobblePhase * 0.5f) * 8f
                    val facingLeft = fish.velocity.x < 0

                    Image(
                        painter = painterResource(fish.color.drawableRes),
                        contentDescription = fish.color.displayName,
                        modifier = Modifier
                            .size(fishSizeDp)
                            .offset {
                                IntOffset(
                                    fish.position.x.toInt(),
                                    (fish.position.y + wobble).toInt()
                                )
                            }
                            .graphicsLayer {
                                scaleX = if (facingLeft) -scaleWobble else scaleWobble
                                scaleY = scaleWobble
                                rotationZ = rotation
                            }
                    )
                }

                // Render bonus card
                bonusCard?.let { card ->
                    val bonusPulse = rememberInfiniteTransition(label = "bonusPulse")
                    val pulseScale by bonusPulse.animateFloat(
                        initialValue = 0.9f, targetValue = 1.1f,
                        animationSpec = infiniteRepeatable(
                            tween(500, easing = LinearEasing), RepeatMode.Reverse
                        ),
                        label = "bonusScale"
                    )
                    Image(
                        painter = painterResource(card.type.drawableRes),
                        contentDescription = card.type.description,
                        modifier = Modifier
                            .size(80.dp)
                            .offset {
                                IntOffset(card.position.x.toInt(), card.position.y.toInt())
                            }
                            .graphicsLayer {
                                scaleX = pulseScale
                                scaleY = pulseScale
                            }
                    )
                }
            }
        }

        // Overlays
        when (phase) {
            GamePhase.PAUSED -> PausePopup(
                onResume = { phase = GamePhase.PLAYING },
                onReplay = onReplay,
                onHome = {
                    isExitingScreen = true
                    onHome()
                }
            )
            GamePhase.WON -> WinPopup(
                score = score,
                level = level,
                onReplay = onReplay,
                onNext = { onNextLevel(level + 1) },
                onHome = {
                    isExitingScreen = true
                    onHome()
                }
            )
            GamePhase.LOST -> LosePopup(
                score = score,
                onReplay = onReplay,
                onHome = {
                    isExitingScreen = true
                    onHome()
                }
            )
            else -> {}
        }
    }
}

@Composable
private fun TopBar(
    level: Int,
    score: Int,
    targetScore: Int,
    lives: Int,
    maxLives: Int,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onPause: () -> Unit
) {
    val sideButtonSize = 48.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, start = 8.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        // LEFT
        Box(modifier = Modifier.size(sideButtonSize)) {
            SquareButton(
                btnRes = R.drawable.back,
                btnMaxWidth = 1f,
                btnClickable = onBack
            )
        }

        // SCORE
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(R.drawable.score_bg),
                contentDescription = null,
                modifier = Modifier.size(width = 120.dp, height = 44.dp),
                contentScale = ContentScale.FillBounds
            )
            Text(
                text = "$score / $targetScore",
                fontFamily = GameFont,
                fontSize = 16.sp,
                color = Color.Blue
            )
        }

        // LIVES
        Row(verticalAlignment = Alignment.CenterVertically) {
            repeat(maxLives) { i ->
                Text(
                    text = "\u2764\uFE0F",
                    fontSize = 18.sp,
                    modifier = Modifier.graphicsLayer {
                        alpha = if (i < lives) 1f else 0.25f
                    }
                )
            }
        }

        // RIGHT
        Box(modifier = Modifier.size(sideButtonSize)) {
            SquareButton(
                btnRes = R.drawable.pause,
                btnMaxWidth = 1f,
                btnClickable = onPause
            )
        }
    }

    // Level indicator
    Text(
        text = "Level $level",
        fontFamily = GameFont,
        fontSize = 16.sp,
        color = Color.White.copy(alpha = 0.7f),
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun PausePopup(
    onResume: () -> Unit,
    onReplay: () -> Unit,
    onHome: () -> Unit
) {
    GamePopup {
        Text(
            text = "Paused",
            fontFamily = GameFont,
            fontSize = 32.sp,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(24.dp))
        MenuButton(text = "Resume", onClick = onResume)
        MenuButton(text = "Replay", onClick = onReplay)
        MenuButton(text = "Home", onClick = onHome)
    }
}

@Composable
private fun WinPopup(
    score: Int,
    level: Int,
    onReplay: () -> Unit,
    onNext: () -> Unit,
    onHome: () -> Unit
) {
    GamePopup {
        Image(
            painter = painterResource(R.drawable.you_win),
            contentDescription = "You Win!",
            modifier = Modifier.size(200.dp, 100.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Score: $score",
            fontFamily = GameFont,
            fontSize = 28.sp,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (level < 30) {
            MenuButton(text = "Next Level", fontSize = 20.sp, onClick = onNext)
        }
        MenuButton(text = "Home", onClick = onHome)
    }
}

@Composable
private fun LosePopup(
    score: Int,
    onReplay: () -> Unit,
    onHome: () -> Unit
) {
    GamePopup {
        Image(
            painter = painterResource(R.drawable.game_over),
            contentDescription = "Game Over",
            modifier = Modifier.size(200.dp, 80.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Score: $score",
            fontFamily = GameFont,
            fontSize = 20.sp,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))
        MenuButton(text = "Replay", onClick = onReplay)
        MenuButton(text = "Home", onClick = onHome)
    }
}

@Composable
private fun GamePopup(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Dim background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = 0.6f }
                .pointerInput(Unit) { detectTapGestures { /* consume taps */ } }
        ) {
            Box(modifier = Modifier.fillMaxSize().graphicsLayer { alpha = 1f }) {
                // Dark overlay
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(Color.Black.copy(alpha = 0.6f))
                }
            }
        }

        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(R.drawable.popup_1),
                contentDescription = null,
                modifier = Modifier.size(320.dp, 420.dp),
                contentScale = ContentScale.FillBounds
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(32.dp)
            ) {
                content()
            }
        }
    }
}