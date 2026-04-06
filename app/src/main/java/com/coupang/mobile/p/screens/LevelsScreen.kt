package com.coupang.mobile.p.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.coupang.mobile.p.ui.components.pressableWithCooldown
import com.coupang.mobile.p.ui.theme.GameFont

@Composable
fun LevelsScreen(
    prefs: GamePreferences,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onLevelSelected: (Int) -> Unit
) {
    val unlockedLevel = prefs.highestUnlockedLevel

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
                .padding(top = 48.dp, bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SquareButton(btnRes = R.drawable.back, btnMaxWidth = 0.14f, btnClickable = onBack)
                Text(
                    text = "Levels",
                    fontFamily = GameFont,
                    fontSize = 32.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.fillMaxWidth(0.2f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items((1..30).toList()) { level ->
                    val isUnlocked = level <= unlockedLevel
                    LevelItem(
                        level = level,
                        isUnlocked = isUnlocked,
                        onClick = { if (isUnlocked) onLevelSelected(level) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LevelItem(level: Int, isUnlocked: Boolean, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .aspectRatio(1f)
            .then(
                if (isUnlocked) {
                    Modifier.pressableWithCooldown(
                        onClick = onClick
                    )
                } else Modifier
            )
    ) {
        Image(
            painter = painterResource(
                if (isUnlocked) R.drawable.level_open else R.drawable.level_close
            ),
            contentDescription = "Level $level",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(70.dp)
        )

        Text(
            text = "$level",
            fontFamily = GameFont,
            fontSize = 26.sp,
            color = Color.White
        )
    }
}