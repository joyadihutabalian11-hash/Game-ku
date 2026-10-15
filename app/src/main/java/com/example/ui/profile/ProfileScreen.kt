package com.example.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PlayerProfile
import com.example.ui.components.getAvatarEmoji
import com.example.ui.theme.JoyGreen
import com.example.ui.theme.JoyPurple
import com.example.ui.theme.JoyYellow

@Composable
fun ProfileScreen(
    currentProfile: PlayerProfile?,
    allProfiles: List<PlayerProfile>,
    onSelectProfile: (Long) -> Unit,
    onCreateProfile: (String, String) -> Unit,
    onBackClick: () -> Unit
) {
    var isAddingProfile by remember { mutableStateOf(false) }
    var newProfileName by remember { mutableStateOf("") }
    var selectedAvatar by remember { mutableStateOf("mascot_joy") }

    val availableAvatars = listOf(
        "mascot_joy" to "🤖 Joy Robot",
        "dino_robot" to "🦖 Robot Dino",
        "bunny_star" to "🐰 Kelinci Star",
        "wizard_cat" to "🐱 Kucing Penyihir",
        "rocket_dog" to "🐶 Anjing Roket"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3E5F5))
            .padding(16.dp)
    ) {
        Text(
            text = "PROFIL PEMAIN KODING",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = JoyPurple
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (!isAddingProfile) {
            Button(
                onClick = { isAddingProfile = true },
                colors = ButtonDefaults.buttonColors(containerColor = JoyGreen),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("add_profile_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "BUAT PROFIL BARU",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(allProfiles) { profile ->
                    val isSelected = profile.id == currentProfile?.id
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) JoyPurple else Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectProfile(profile.id) }
                            .testTag("profile_item_${profile.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = getAvatarEmoji(profile.avatarIcon),
                                    fontSize = 36.sp
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = profile.name,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else JoyPurple
                                        )
                                    )
                                    Text(
                                        text = if (isSelected) "Profil Aktif" else "Ketuk untuk Memilih",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (isSelected) JoyYellow else Color.Gray
                                        )
                                    )
                                }
                            }

                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(JoyYellow),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = JoyPurple
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Add Profile Form
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Buat Profil Anak Baru",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = JoyPurple
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = newProfileName,
                        onValueChange = { newProfileName = it },
                        label = { Text("Nama Pemain / Anak") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_name_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Pilih Karakter Maskot Avatar:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        availableAvatars.forEach { (key, label) ->
                            val isChosen = selectedAvatar == key
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(if (isChosen) JoyYellow else Color(0xFFEEEEEE))
                                    .border(
                                        width = if (isChosen) 3.dp else 0.dp,
                                        color = if (isChosen) JoyPurple else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedAvatar = key },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = getAvatarEmoji(key),
                                    fontSize = 28.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = {
                                if (newProfileName.isNotBlank()) {
                                    onCreateProfile(newProfileName.trim(), selectedAvatar)
                                    isAddingProfile = false
                                    newProfileName = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = JoyGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("save_profile_button")
                        ) {
                            Text("SIMPAN PROFIL", fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { isAddingProfile = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text("BATAL", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
