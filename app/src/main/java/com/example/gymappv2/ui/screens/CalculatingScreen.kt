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
import kotlinx.coroutines.delay

@Composable
fun CalculatingScreen(onFinishClick: () -> Unit) {
    val bgDark = Color(0xFF07090F)
    val neonCyan = Color(0xFF00F0FF)

    // Estados para controlar la animación y los textos
    var loadingText by remember { mutableStateOf("Analizando tu perfil...") }
    var isFinished by remember { mutableStateOf(false) }

    // LaunchedEffect nos permite ejecutar rutinas en segundo plano (como timers)
    LaunchedEffect(Unit) {
        delay(1500) // Espera 1.5 segundos
        loadingText = "Ajustando rutinas a tu experiencia..."
        delay(1500)
        loadingText = "Calculando métricas de peso y altura..."
        delay(1500)
        loadingText = "¡Tu plan personalizado está listo!"
        isFinished = true // Terminamos la carga
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgDark)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Si no ha terminado, mostramos el círculo girando, si ya terminó, un emoji/icono
            if (!isFinished) {
                CircularProgressIndicator(
                    color = neonCyan,
                    strokeWidth = 6.dp,
                    modifier = Modifier.size(80.dp)
                )
            } else {
                Text(
                    text = "🔥",
                    fontSize = 80.sp
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Texto dinámico que va cambiando
            Text(
                text = loadingText,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // El botón solo aparece cuando la animación termina
        if (isFinished) {
            Button(
                onClick = onFinishClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(30.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = neonCyan)
            ) {
                Text(
                    text = "IR A MI DASHBOARD",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            }
        }
    }
}