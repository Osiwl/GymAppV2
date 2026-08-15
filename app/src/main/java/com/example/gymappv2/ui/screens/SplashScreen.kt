package com.example.gymappv2.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateNext: () -> Unit) {
    val accentCyan = Color(0xFF4AC4CF)
    val glassCard = Color(0xFF141A29)

    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2500)
        onNavigateNext()
    }

    // Animación de entrada con rebote
    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.4f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "alpha"
    )

    // Animación de rotación infinita para el anillo orbital
    val infiniteTransition = rememberInfiniteTransition(label = "orbit")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle"
    )

    // Gradiente de fondo profundo
    val deepSpaceGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF131C31),
            Color(0xFF0A0F1D),
            Color(0xFF040609)
        )
    )

    val ambientGlow = Brush.radialGradient(
        colors = listOf(
            accentCyan.copy(alpha = 0.2f),
            Color.Transparent
        ),
        radius = 500f
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(deepSpaceGradient),
        contentAlignment = Alignment.Center
    ) {
        // Brillo ambiental de fondo compacto
        Box(
            modifier = Modifier
                .size(240.dp)
                .background(ambientGlow, shape = CircleShape)
        )

        Box(
            modifier = Modifier
                .scale(scaleAnim)
                .alpha(alphaAnim),
            contentAlignment = Alignment.Center
        ) {
            // 1. ÓRBITA EXTERIOR (Ajustada proporcionalmente a 130.dp para albergar el círculo más grande)
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .border(width = 1.dp, color = accentCyan.copy(alpha = 0.25f), shape = CircleShape)
                    .rotate(angle),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = Modifier
                        .offset(y = (-3).dp)
                        .size(6.dp)
                        .background(accentCyan, shape = CircleShape)
                )
            }

            // 2. ANILLO INTERMEDIO FIJO
            Box(
                modifier = Modifier
                    .size(105.dp)
                    .border(width = 1.2.dp, color = accentCyan.copy(alpha = 0.4f), shape = CircleShape)
            )

            // 3. EL CÍRCULO CENTRAL PRINCIPAL (Más grande: 80.dp con mancuerna de 44.dp)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(glassCard, shape = CircleShape)
                    .border(width = 2.dp, color = accentCyan, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = "Fitness Icon",
                    tint = accentCyan,
                    modifier = Modifier.size(44.dp) // <- Mancuerna más grande y dominante
                )
            }
        }
    }
}