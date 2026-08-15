package com.example.gymappv2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NameScreen(
    onBackClick: () -> Unit,
    onNextClick: (String) -> Unit
) {
    val bgDark = Color(0xFF07090F)
    val neonCyan = Color(0xFF00F0FF)
    val glassCard = Color(0xFF141A29)

    var name by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgDark)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                TextButton(
                    onClick = onBackClick
                ) {
                    Text(
                        text = "← Volver",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            LinearProgressIndicator(
                progress = { 0.15f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = neonCyan,
                trackColor = glassCard
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "¡Empecemos!",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "¿Cómo deberíamos llamarte?",
                color = neonCyan,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { nuevoNombre ->
                    name = nuevoNombre
                },
                placeholder = {
                    Text(
                        text = "Tu nombre o apodo",
                        color = Color.Gray
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = glassCard,
                    unfocusedContainerColor = glassCard,
                    focusedBorderColor = neonCyan,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = neonCyan
                ),
                singleLine = true
            )
        }

        Button(
            onClick = {
                val nombreLimpio = name.trim()

                if (nombreLimpio.isNotEmpty()) {
                    onNextClick(nombreLimpio)
                }
            },
            enabled = name.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(30.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = neonCyan,
                disabledContainerColor = glassCard
            )
        ) {
            Text(
                text = "SIGUIENTE",
                color = if (name.isNotBlank()) {
                    Color.Black
                } else {
                    Color.Gray
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
        }
    }
}