package com.coupang.mobile.p.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coupang.mobile.p.R
import com.coupang.mobile.p.ui.components.MenuButton
import kotlin.math.sin

@Composable
fun MenuScreen(
    onPlay: () -> Unit,
    onSettings: () -> Unit,
    onLeaderboard: () -> Unit,
    onExit: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "menu")

    val titleScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.06f,
        animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Reverse),
        label = "titlePulse"
    )

    val fish1X by infiniteTransition.animateFloat(
        initialValue = -100f, targetValue = 400f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Reverse),
        label = "fish1X"
    )
    val fish1Y by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 30f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Reverse),
        label = "fish1Y"
    )

    val fish2X by infiniteTransition.animateFloat(
        initialValue = 400f, targetValue = -80f,
        animationSpec = infiniteRepeatable(tween(7000, easing = LinearEasing), RepeatMode.Reverse),
        label = "fish2X"
    )
    val fish2Y by infiniteTransition.animateFloat(
        initialValue = 10f, targetValue = -20f,
        animationSpec = infiniteRepeatable(tween(2500, easing = LinearEasing), RepeatMode.Reverse),
        label = "fish2Y"
    )

    val fish3X by infiniteTransition.animateFloat(
        initialValue = -60f, targetValue = 350f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Reverse),
        label = "fish3X"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_vertical),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Decorative background fish
        Image(
            painter = painterResource(R.drawable.fish),
            contentDescription = null,
            modifier = Modifier
                .size(70.dp)
                .offset(x = fish1X.dp, y = (150 + fish1Y).dp)
                .graphicsLayer {
                    scaleX = if (fish1X > 150f) -1f else 1f
                    alpha = 0.4f
                }
        )
        Image(
            painter = painterResource(R.drawable.fish_2),
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .offset(x = fish2X.dp, y = (300 + fish2Y).dp)
                .graphicsLayer {
                    scaleX = if (fish2X < 160f) -1f else 1f
                    alpha = 0.35f
                }
        )
        Image(
            painter = painterResource(R.drawable.fish_3),
            contentDescription = null,
            modifier = Modifier
                .size(55.dp)
                .offset(x = fish3X.dp, y = (500f + sin(fish3X / 40f) * 20f).dp)
                .graphicsLayer { alpha = 0.3f }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.fillMaxHeight(0.3f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                MenuButton(text = "Play", onClick = onPlay)
                Spacer(modifier = Modifier.height(4.dp))
                MenuButton(text = "Leaderboard", fontSize = 20.sp, onClick = onLeaderboard)
                Spacer(modifier = Modifier.height(4.dp))
                MenuButton(text = "Settings", onClick = onSettings)
                Spacer(modifier = Modifier.height(4.dp))
                MenuButton(text = "Exit", onClick = onExit)
            }

            Spacer(modifier = Modifier.height(1.dp))
        }
    }
}