package com.coupang.mobile.p.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun Modifier.pressableWithCooldown(
    cooldownMillis: Long = 1000L,
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier {
    var lastClickTimestamp by remember { mutableLongStateOf(0L) }
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.88f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "PressAnimation"
    )

    return this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(enabled) {
            detectTapGestures(
                onPress = {
                    if (!enabled) return@detectTapGestures

                    val now = System.currentTimeMillis()
                    if (now - lastClickTimestamp >= cooldownMillis) {
                        lastClickTimestamp = now
                        isPressed = true
                        try {
                            tryAwaitRelease()
                            onClick()
                        } finally {
                            isPressed = false
                        }
                    }
                }
            )
        }
}

@Composable
fun Modifier.peckPress(
    onPeck: () -> Unit,
    cooldownMillis: Long = 1000L,
    isChickenReady: Boolean = true
): Modifier {
    var lastPeckTime by remember { mutableLongStateOf(0L) }
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed && isChickenReady) 0.85f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "PeckPressAnimation"
    )

    return this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(isChickenReady) {
            detectTapGestures(
                onPress = {
                    if (!isChickenReady) return@detectTapGestures

                    val now = System.currentTimeMillis()
                    if (now - lastPeckTime >= cooldownMillis) {
                        lastPeckTime = now
                        isPressed = true
                        try {
                            tryAwaitRelease()
                            onPeck()
                        } finally {
                            isPressed = false
                        }
                    }
                }
            )
        }
}
