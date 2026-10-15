package com.example.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.JoyGreen
import com.example.ui.theme.JoyPurple
import com.example.ui.theme.JoyRed
import com.example.ui.theme.JoyYellow

@Composable
fun SettingsScreen(
    isSoundEnabled: Boolean,
    isMusicEnabled: Boolean,
    onSoundToggle: () -> Unit,
    onMusicToggle: () -> Unit,
    onResetProgressClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3E5F5))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "PENGATURAN GAME",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = JoyPurple
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Audio Settings Card
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Suara & Audio",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = JoyPurple
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Sound Effects Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Sound FX",
                        tint = JoyPurple
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Efek Suara (Sound FX)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "Suara langkah, lompatan, dan koding", fontSize = 11.sp, color = Color.Gray)
                    }
                    Switch(
                        checked = isSoundEnabled,
                        onCheckedChange = { onSoundToggle() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = JoyGreen
                        ),
                        modifier = Modifier.testTag("settings_sound_switch")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Ambient Music Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "Musik",
                        tint = JoyPurple
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Musik Latar Ceria", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "Melodi musik santai anak-anak", fontSize = 11.sp, color = Color.Gray)
                    }
                    Switch(
                        checked = isMusicEnabled,
                        onCheckedChange = { onMusicToggle() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = JoyGreen
                        ),
                        modifier = Modifier.testTag("settings_music_switch")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Educational Guidance Card for Parents
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = "Info", tint = JoyPurple)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Panduan Edukasi Orang Tua",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = JoyPurple
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Aplikasi JOYGAMING dirancang khusus untuk melatih:\n" +
                            "• Pemikiran Algoritmik: Menyusun urutan langkah dari A ke B.\n" +
                            "• Pemecahan Masalah (Problem Solving): Mengenali pola dan mengatasi rintangan.\n" +
                            "• Efisiensi Koding (Loop): Menggunakan blok pengulangan agar kode lebih efektif.",
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Reset Progress Button
        Button(
            onClick = onResetProgressClick,
            colors = ButtonDefaults.buttonColors(containerColor = JoyRed),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("reset_progress_button")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "RESET PROGRES LEVEL SAYA",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
