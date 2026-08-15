package com.example.gymappv2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
fun CustomRoutinePromptScreen(
    onBackClick: () -> Unit,
    onYesClick: () -> Unit, // Ruta si quiere rutina automática
    onNoClick: () -> Unit   // Ruta si quiere armarla él mismo
) {
    val bgDark = Color(0xFF07090F)
    val neonCyan = Color(0xFF00F0FF)
    val glassCard = Color(0xFF141A29)

    var selectedOption by remember { mutableStateOf("") }
    val optionYes = "Armar mi rutina inteligente"
    val optionNo = "Prefiero crear la mía"

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

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                TextButton(onClick = onBackClick) {
                    Text(text = "← Volver", color = Color.Gray, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Barra en 35% (Entre Experiencia y Metas)
            LinearProgressIndicator(
                progress = { 0.35f },
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = neonCyan,
                trackColor = glassCard
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "¡Excelente nivel! 🔥",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "¿Cómo quieres continuar?",
                color = neonCyan,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Opciones de Cajas de Cristal
            val options = listOf(optionYes, optionNo)

            options.forEach { option ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (selectedOption == option) neonCyan else glassCard)
                        .clickable { selectedOption = option }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option,
                        color = if (selectedOption == option) Color.Black else Color.Gray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // Botón Siguiente que detecta qué elegiste y lanza la ruta correcta
        Button(
            onClick = {
                if (selectedOption == optionYes) onYesClick() else onNoClick()
            },
            enabled = selectedOption.isNotEmpty(),
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
                color = if (selectedOption.isNotEmpty()) Color.Black else Color.Gray,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
        }
    }
}