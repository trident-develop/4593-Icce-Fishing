package com.coupang.mobile.p.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coupang.mobile.p.R
import com.coupang.mobile.p.storage.GamePreferences
import com.coupang.mobile.p.ui.components.SquareButton
import com.coupang.mobile.p.ui.theme.GameFont

@Composable
fun LeaderboardScreen(
    prefs: GamePreferences,
    onBack: () -> Unit
) {
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
                .padding(top = 48.dp, bottom = 32.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
                text = "Leaderboard",
                fontFamily = GameFont,
                fontSize = 32.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(32.dp))

            StatCard(label = "Highest Level", value = "${prefs.highestUnlockedLevel}")
            Spacer(modifier = Modifier.height(12.dp))
            StatCard(label = "Best Score", value = "${prefs.bestScore}")
            Spacer(modifier = Modifier.height(12.dp))
            StatCard(label = "Total Wins", value = "${prefs.totalWins}")
            Spacer(modifier = Modifier.height(12.dp))
            StatCard(label = "Games Played", value = "${prefs.totalGamesPlayed}")
        }
    }
}

@Composable
private fun StatCard(label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0x88000033)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontFamily = GameFont,
                fontSize = 20.sp,
                color = Color(0xFFFFD54F)
            )
            Text(
                text = value,
                fontFamily = GameFont,
                fontSize = 24.sp,
                color = Color.White,
                textAlign = TextAlign.End
            )
        }
    }
}