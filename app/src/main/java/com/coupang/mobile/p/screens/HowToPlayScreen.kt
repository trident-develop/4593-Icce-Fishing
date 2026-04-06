package com.coupang.mobile.p.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coupang.mobile.p.R
import com.coupang.mobile.p.ui.components.SquareButton
import com.coupang.mobile.p.ui.theme.GameFont

@Composable
fun HowToPlayScreen(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_vertical),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp, bottom = 16.dp, start = 24.dp, end = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SquareButton(
                    btnRes = R.drawable.back,
                    btnMaxWidth = 0.14f,
                    btnClickable = onBack
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "How To Play",
                fontFamily = GameFont,
                fontSize = 32.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color.Black.copy(alpha = 0.45f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                HelpSection(
                    title = "Goal",
                    body = "Each round shows a target fish color at the top of the screen. Tap only the fish that match the target color to earn points!"
                )
                HelpSection(
                    title = "Scoring",
                    body = "Each correct catch gives you points. Reach the target score to win the level and unlock the next one."
                )
                HelpSection(
                    title = "Lives",
                    body = "You start with 3 lives. Tapping the wrong fish costs you 1 life. If you lose all lives, the round is over."
                )
                HelpSection(
                    title = "Fish Types",
                    body = "There are three fish colors:\n\u2022 Red fish\n\u2022 Orange fish\n\u2022 Blue fish\n\nOnly tap the one matching the target!"
                )
                HelpSection(
                    title = "Bonuses",
                    body = "Special bonus cards appear from time to time. Tap them for temporary power-ups:\n\u2022 Huge Reds \u2014 red fish give double points\n\u2022 Lil Blues \u2014 all fish slow down\n\u2022 Big Oranges \u2014 orange fish give double points"
                )
                HelpSection(
                    title = "Tips",
                    body = "Stay focused on the target color. Fish get faster and more numerous in higher levels. Use bonuses wisely!"
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun HelpSection(title: String, body: String) {
    Text(
        text = title,
        fontFamily = GameFont,
        fontSize = 22.sp,
        color = Color(0xFFFFD54F)
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = body,
        fontFamily = GameFont,
        fontSize = 17.sp,
        color = Color.White,
        lineHeight = 22.sp
    )
    Spacer(modifier = Modifier.height(20.dp))
}