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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.coupang.mobile.p.R
import com.coupang.mobile.p.audio.SoundManager
import com.coupang.mobile.p.storage.GamePreferences
import com.coupang.mobile.p.ui.components.MenuButton
import com.coupang.mobile.p.ui.components.SquareButton
import com.coupang.mobile.p.ui.theme.GameFont
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun SettingsScreen(
    prefs: GamePreferences,
    soundManager: SoundManager,
    onBack: () -> Unit,
    onHowToPlay: () -> Unit,
    onPrivacyPolicy: () -> Unit
) {
    val isInPreview = LocalInspectionMode.current
    var musicOn by remember { mutableStateOf(prefs.musicEnabled) }
    var soundOn by remember { mutableStateOf(prefs.soundEnabled) }

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
                .padding(top = 48.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
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
                text = "Settings",
                fontFamily = GameFont,
                fontSize = 36.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(40.dp))

            Row(
                modifier = Modifier.fillMaxWidth(0.7f),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Music", fontFamily = GameFont, fontSize = 18.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    SquareButton(
                        btnRes = if (musicOn) R.drawable.music_on else R.drawable.music_off,
                        btnMaxWidth = 0.25f,
                        cooldownMillis = 0L,
                        btnClickable = {
                            musicOn = !musicOn
                            soundManager.onMusicToggle(musicOn)
                        }
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Sound", fontFamily = GameFont, fontSize = 18.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    SquareButton(
                        btnRes = if (soundOn) R.drawable.sound_on else R.drawable.sound_off,
                        btnMaxWidth = 0.34f,
                        cooldownMillis = 0L,
                        btnClickable = {
                            soundOn = !soundOn
                            soundManager.onSoundToggle(soundOn)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            MenuButton(text = "How To Play", fontSize = 20.sp, onClick = onHowToPlay)
            Spacer(modifier = Modifier.height(8.dp))
            MenuButton(text = "Privacy Policy", fontSize = 18.sp, onClick = onPrivacyPolicy)
        }

        if (!isInPreview) {
            AndroidView(
                factory = {
                    val adView = AdView(it)
                    adView.setAdSize(AdSize.BANNER)
                    adView.adUnitId = "ca-app-pub-3940256099942544/9214589741"
                    adView.loadAd(AdRequest.Builder().build())
                    adView
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}