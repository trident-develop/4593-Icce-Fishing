package com.coupang.mobile.p.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coupang.mobile.p.R
import com.coupang.mobile.p.ui.theme.GameFont
import kotlin.math.sin

@Composable
fun LoadingScreen(onTimeout: () -> Unit) {
    BackHandler(enabled = true) {}
    val transition = rememberInfiniteTransition(label = "loading")

    // Fish 1 - red, swims right then left
    val fish1X by transition.animateFloat(
        initialValue = -80f, targetValue = 350f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Reverse),
        label = "fish1X"
    )
    val fish1Y by transition.animateFloat(
        initialValue = 0f, targetValue = 25f,
        animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing), RepeatMode.Reverse),
        label = "fish1Y"
    )
    val fish1Scale by transition.animateFloat(
        initialValue = 0.95f, targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Reverse),
        label = "fish1Scale"
    )

    // Fish 2 - orange, swims left then right
    val fish2X by transition.animateFloat(
        initialValue = 380f, targetValue = -60f,
        animationSpec = infiniteRepeatable(tween(5000, easing = LinearEasing), RepeatMode.Reverse),
        label = "fish2X"
    )
    val fish2Y by transition.animateFloat(
        initialValue = -15f, targetValue = 20f,
        animationSpec = infiniteRepeatable(tween(2200, easing = LinearEasing), RepeatMode.Reverse),
        label = "fish2Y"
    )
    val fish2Scale by transition.animateFloat(
        initialValue = 1.0f, targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Reverse),
        label = "fish2Scale"
    )

    // Fish 3 - blue, gentle wave
    val fish3X by transition.animateFloat(
        initialValue = -40f, targetValue = 320f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Reverse),
        label = "fish3X"
    )
    val fish3Rot by transition.animateFloat(
        initialValue = -8f, targetValue = 8f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Reverse),
        label = "fish3Rot"
    )

    // Bubbles animation
    val bubblePhase by transition.animateFloat(
        initialValue = 0f, targetValue = 6.28f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Restart),
        label = "bubbles"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_vertical),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Glowing bubble circles
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            for (i in 0..7) {
                val bx = (w * 0.15f) + (i * w * 0.1f)
                val by = h * 0.4f + sin(bubblePhase + i * 0.8f) * 80f
                val radius = 8f + sin(bubblePhase + i * 1.2f) * 4f
                drawCircle(
                    color = Color.White.copy(alpha = 0.2f + sin(bubblePhase + i) * 0.1f),
                    radius = radius,
                    center = Offset(bx, by)
                )
            }
            for (i in 0..5) {
                val bx = (w * 0.1f) + (i * w * 0.15f)
                val by = h * 0.65f + sin(bubblePhase + i * 1.1f + 2f) * 60f
                val radius = 6f + sin(bubblePhase + i * 0.9f) * 3f
                drawCircle(
                    color = Color.White.copy(alpha = 0.15f + sin(bubblePhase + i + 1f) * 0.08f),
                    radius = radius,
                    center = Offset(bx, by)
                )
            }
        }

        // Fish 1 - Red
        Image(
            painter = painterResource(R.drawable.fish),
            contentDescription = null,
            modifier = Modifier
                .size(90.dp)
                .offset(x = fish1X.dp, y = (280 + fish1Y).dp)
                .graphicsLayer {
                    scaleX = if (fish1X > 135f) fish1Scale else -fish1Scale
                    scaleY = fish1Scale
                    rotationZ = sin(fish1Y / 10f) * 5f
                }
        )

        // Fish 2 - Orange
        Image(
            painter = painterResource(R.drawable.fish_2),
            contentDescription = null,
            modifier = Modifier
                .size(75.dp)
                .offset(x = fish2X.dp, y = (400 + fish2Y).dp)
                .graphicsLayer {
                    scaleX = if (fish2X < 160f) fish2Scale else -fish2Scale
                    scaleY = fish2Scale
                    rotationZ = sin(fish2Y / 8f) * 6f
                }
        )

        // Fish 3 - Blue
        Image(
            painter = painterResource(R.drawable.fish_3),
            contentDescription = null,
            modifier = Modifier
                .size(65.dp)
                .offset(x = fish3X.dp, y = (520f + sin(fish3X / 50f) * 30f).dp)
                .graphicsLayer {
                    scaleX = if (fish3X > 140f) 1f else -1f
                    rotationZ = fish3Rot
                }
        )

        // Title and progress
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.15f))

            Text(
                text = "Icce\nFishing",
                fontFamily = GameFont,
                fontSize = 52.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 58.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = Color(0xFF4FC3F7),
                strokeWidth = 4.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading...",
                fontFamily = GameFont,
                fontSize = 18.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}