package com.example.gymappv2.ui.rutinas

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest

// 🔥 IMPORTAMOS LA FUNCIÓN GLOBAL A PRUEBA DE FALLOS
import com.example.gymappv2.ui.screens.buscarEjercicioEnCache

@Composable
fun EntrenamientoActivoScreen(
    rutina: RutinaDia,
    tiempoSegundos: Int,
    onEjercicioClick: (EjercicioApp) -> Unit,
    onFinalizarClick: () -> Unit
) {
    val fondoOscuro = Color(0xFF07090F)
    val tarjetaColor = Color(0xFF141A29)
    val colorRojoLive = Color(0xFFEF5350)
    val acentoCyan = Color(0xFF3BA0E3)
    val context = LocalContext.current

    val minutos = tiempoSegundos / 60
    val segundos = tiempoSegundos % 60
    val tiempoFormat = String.format("%02d:%02d", minutos, segundos)

    val infiniteTransition = rememberInfiniteTransition(label = "PuntoRojo")
    val alphaPunto by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "AlphaPunto"
    )

    // 🔥 EFECTO SHIMMER PARA MANTENER EL DISEÑO PREMIUM AQUÍ TAMBIÉN
    val transitionShimmer = rememberInfiniteTransition(label = "shimmer")
    val alphaShimmer by transitionShimmer.animateFloat(
        initialValue = 0.2f, targetValue = 0.6f,
        animationSpec = infiniteRepeatable(animation = tween(800, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "shimmer_alpha"
    )

    Column(modifier = Modifier.fillMaxSize().background(fondoOscuro).padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(colorRojoLive.copy(alpha = alphaPunto)))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ENTRENAMIENTO EN CURSO", color = colorRojoLive, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(rutina.titulo, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            }

            Text(tiempoFormat, color = acentoCyan, fontSize = 28.sp, fontWeight = FontWeight.Black)
        }

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(rutina.ejercicios) { ejercicio ->
                // 🔥 USAMOS LA FUNCIÓN SEGURA PARA LAS IMÁGENES
                val dataEnCache = buscarEjercicioEnCache(ejercicio)
                val imagenFinal = ejercicio.urlImagen.ifEmpty { dataEnCache?.urlImagen ?: "" }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(tarjetaColor)
                        .clickable { onEjercicioClick(ejercicio) }
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier.size(60.dp).clip(CircleShape).background(Color(0xFF07090F)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (imagenFinal.isNotEmpty()) {
                            SubcomposeAsyncImage(
                                model = ImageRequest.Builder(context).data(imagenFinal).crossfade(true).build(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            ) {
                                if (painter.state is AsyncImagePainter.State.Loading) {
                                    // 🔥 ANIMACIÓN DE BRILLO EN LUGAR DE CARGADOR GRIS
                                    Box(modifier = Modifier.fillMaxSize().background(Color.LightGray.copy(alpha = alphaShimmer)))
                                } else {
                                    SubcomposeAsyncImageContent()
                                }
                            }
                        } else {
                            Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(ejercicio.nombreEs, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Toca para registrar series", color = Color.Gray, fontSize = 14.sp)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = "Entrar", tint = acentoCyan)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onFinalizarClick,
            colors = ButtonDefaults.buttonColors(containerColor = colorRojoLive),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(56.dp).padding(bottom = 8.dp)
        ) {
            Text("FINALIZAR ENTRENAMIENTO", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}