package com.example.gymappv2.ui.rutinas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun DetalleRutinaScreen(
    tituloRutina: String,
    ejerciciosDinamicos: List<EjercicioApp>, // <--- 🔥 Ahora recibe la lista de objetos EjercicioApp
    onBackClick: () -> Unit,
    onEjercicioClick: (EjercicioApp) -> Unit // <--- 🔥 Ahora devuelve el objeto completo al hacer clic
) {
    val fondoOscuro = Color(0xFF07090F)
    val tarjetaColor = Color(0xFF141A29)
    val acentoCyan = Color(0xFF4AC4CF)

    val context = LocalContext.current
    val imageLoader = remember { ImageLoader(context) }

    // PRE-CARGA SILENCIOSA USANDO LA LISTA DINÁMICA
    LaunchedEffect(ejerciciosDinamicos) {
        val db = FirebaseFirestore.getInstance()

        ejerciciosDinamicos.forEach { ejercicio ->

            // ¡Adiós al traductor gigante! Aquí usamos directamente el idFirebase que viene del generador.
            db.collection("exercises")
                .whereEqualTo("name", ejercicio.idFirebase)
                .limit(1)
                .get()
                .addOnSuccessListener { documentos ->
                    if (!documentos.isEmpty) {
                        val docEncontrado = documentos.documents[0]
                        val videoArchivo = docEncontrado.getString("gifUrl") ?: docEncontrado.getString("video") ?: docEncontrado.getString("gif_url") ?: ""

                        if (videoArchivo.isNotEmpty()) {
                            val urlFinal = if (videoArchivo.startsWith("http")) {
                                videoArchivo.replace("http://", "https://")
                            } else {
                                val rutaLimpia = videoArchivo.removePrefix("videos/")
                                "https://cdn.jsdelivr.net/gh/hasaneyldrm/exercises-dataset@main/videos/$rutaLimpia"
                            }

                            val request = ImageRequest.Builder(context)
                                .data(urlFinal)
                                .memoryCachePolicy(CachePolicy.ENABLED)
                                .diskCachePolicy(CachePolicy.ENABLED)
                                .build()

                            imageLoader.enqueue(request)
                        }
                    }
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoOscuro)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBackClick, modifier = Modifier.offset(x = (-12).dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
            Text(
                text = tituloRutina,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MetricaItem(icon = Icons.Default.Timer, titulo = "Duración", valor = "40-50 min", color = acentoCyan)
            MetricaItem(icon = Icons.Default.MonitorWeight, titulo = "Carga", valor = "- kg", color = acentoCyan)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { /* Iniciar entrenamiento */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = acentoCyan),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Iniciar Entrenamiento", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Lista de ejercicios",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(ejerciciosDinamicos.size) { index ->
                val ejercicio = ejerciciosDinamicos[index]
                ItemEjercicio(
                    nombre = ejercicio.nombreEs, // 🔥 En la pantalla mostramos el nombre bonito en Español
                    series = 3,
                    reps = 10,
                    tarjetaColor = tarjetaColor,
                    acentoColor = acentoCyan,
                    onClick = { onEjercicioClick(ejercicio) } // 🔥 Pasamos el objeto completo al hacer clic
                )
            }
        }
    }
}

@Composable
fun MetricaItem(icon: androidx.compose.ui.graphics.vector.ImageVector, titulo: String, valor: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = titulo, color = Color.Gray, fontSize = 12.sp)
            Text(text = valor, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ItemEjercicio(nombre: String, series: Int, reps: Int, tarjetaColor: Color, acentoColor: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(tarjetaColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = acentoColor)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(text = nombre, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "$series series • $reps reps", color = Color.Gray, fontSize = 14.sp)
        }
    }
}