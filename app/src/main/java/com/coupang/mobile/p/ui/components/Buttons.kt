package com.coupang.mobile.p.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coupang.mobile.p.R
import com.coupang.mobile.p.ui.theme.GameFont

@Composable
fun MenuButton(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    fontSize: TextUnit = 24.sp,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth(0.6f)
            .height(height = 80.dp)
            .padding(vertical = 8.dp)
            .pressableWithCooldown(onClick = onClick, enabled = enabled),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.main_button),
            contentDescription = "button",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
        Text(
            text = text,
            color = Color.White,
            fontSize = fontSize,
            fontFamily = GameFont,
            modifier = Modifier.padding(bottom = 6.dp)
        )
    }
}

@Composable
fun SquareButton(
    modifier: Modifier = Modifier,
    btnRes: Int,
    btnMaxWidth: Float = 0.18f,
    cooldownMillis: Long = 1000L,
    btnEnabled: Boolean = true,
    btnClickable: () -> Unit
) {
    Image(
        painter = painterResource(id = btnRes),
        contentDescription = "Button",
        modifier = modifier
            .fillMaxWidth(btnMaxWidth)
            .aspectRatio(1f)
            .peckPress(
                onPeck = btnClickable,
                cooldownMillis = cooldownMillis,
                isChickenReady = btnEnabled
            )
    )
}
