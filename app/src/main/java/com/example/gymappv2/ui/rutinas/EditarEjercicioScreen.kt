package com.example.gymappv2.ui.rutinas

import android.os.Build.VERSION.SDK_INT
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import kotlinx.coroutines.delay

// 🔥 IMPORTAMOS LA FUNCIÓN GLOBAL A PRUEBA DE FALLOS
import com.example.gymappv2.ui.screens.buscarEjercicioEnCache

data class SerieData(var reps: String, var peso: String)

@Composable
fun EditarEjercicioScreen(
    ejercicio: EjercicioApp,
    esDesdeBuscador: Boolean = false,
    nombresRutinas: List<String> = emptyList(),
    onAgregarARutina: (String) -> Unit = {},
    onBackClick: () -> Unit
) {
    val fondoOscuro = Color(0xFF07090F)
    val tarjetaColor = Color(0xFF141A29)
    val acentoCyan = Color(0xFF3BA0E3)
    val context = LocalContext.current

    BackHandler(onBack = onBackClick)

    // 🔥 UTILIZAMOS LA FUNCIÓN SEGURA PARA ENCONTRAR EL GIF AL INSTANTE
    val dataEnCache = buscarEjercicioEnCache(ejercicio)
    val urlGifFinal = ejercicio.urlGif.ifEmpty { dataEnCache?.urlGif ?: "" }
    val musculoFinal = ejercicio.musculo.ifEmpty { dataEnCache?.musculo ?: "" }

    var notaTexto by remember { mutableStateOf("") }
    var mostrarDialogoNota by remember { mutableStateOf(false) }

    var tiempoMaximoDescanso by remember { mutableStateOf(60) }
    var tiempoRestante by remember { mutableStateOf(60) }
    var cronometroActivo by remember { mutableStateOf(false) }

    var mostrarDialogoTiempo by remember { mutableStateOf(false) }
    var inputMinutos by remember { mutableStateOf("") }
    var inputSegundos by remember { mutableStateOf("") }

    var mostrarDialogoRutinas by remember { mutableStateOf(false) }
    var nuevaRutinaTexto by remember { mutableStateOf("") }

    // 🔥 ESTADO PARA LA ANIMACIÓN DE ENTRADA
    var mostrarPantalla by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        mostrarPantalla = true
    }

    LaunchedEffect(cronometroActivo, tiempoRestante) {
        if (cronometroActivo && tiempoRestante > 0) {
            delay(1000L)
            tiempoRestante--
        } else if (tiempoRestante == 0) {
            cronometroActivo = false
            tiempoRestante = tiempoMaximoDescanso
        }
    }

    val textoCronometro = String.format("%02d:%02d", tiempoRestante / 60, tiempoRestante % 60)

    val imageLoader = remember {
        ImageLoader.Builder(context).components {
            if (SDK_INT >= 28) add(ImageDecoderDecoder.Factory()) else add(GifDecoder.Factory())
        }.build()
    }

    val listaSeries = remember {
        mutableStateListOf(SerieData("10", "0"), SerieData("10", "0"), SerieData("10", "0"))
    }

    // ANIMACIÓN SHIMMER (BRILLO) PARA CARGA PREMIUM
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alphaShimmer by transition.animateFloat(
        initialValue = 0.2f, targetValue = 0.6f,
        animationSpec = infiniteRepeatable(animation = tween(800, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "shimmer_alpha"
    )

    // 🔥 ANIMACIÓN DE ENTRADA (SlideUp + FadeIn)
    AnimatedVisibility(
        visible = mostrarPantalla,
        enter = slideInVertically(initialOffsetY = { it / 6 }, animationSpec = tween(400, easing = EaseOutQuart)) + fadeIn(tween(400))
    ) {
        Column(modifier = Modifier.fillMaxSize().background(fondoOscuro).padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onBackClick, modifier = Modifier.offset(x = (-12).dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White)
                }
                Text(text = if(esDesdeBuscador) "Detalle del ejercicio" else "Registrar serie", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TARJETA DEL EJERCICIO Y GIF CON SHIMMER
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(tarjetaColor).padding(16.dp)
            ) {
                Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)).background(Color.White), contentAlignment = Alignment.Center) {
                    if (urlGifFinal.isNotEmpty()) {
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(context).data(urlGifFinal).crossfade(true).build(),
                            imageLoader = imageLoader,
                            contentDescription = ejercicio.nombreEs,
                            modifier = Modifier.fillMaxSize().padding(4.dp),
                            contentScale = ContentScale.Fit
                        ) {
                            if (painter.state is AsyncImagePainter.State.Loading) {
                                // 🔥 EFECTO SHIMMER EN LUGAR DE CARGADOR GRIS
                                Box(modifier = Modifier.fillMaxSize().background(Color.LightGray.copy(alpha = alphaShimmer)))
                            } else {
                                SubcomposeAsyncImageContent()
                            }
                        }
                    } else {
                        Icon(Icons.Default.FitnessCenter, "Sin imagen", tint = Color.Gray)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = ejercicio.nombreEs, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = musculoFinal, color = Color.Gray, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // BOTONES DE CONTROL (Cronómetro y Nota)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                Row(
                    modifier = Modifier
                        .weight(1.2f)
                        .background(if (cronometroActivo) acentoCyan.copy(alpha = 0.2f) else tarjetaColor, RoundedCornerShape(10.dp))
                        .border(1.dp, if (cronometroActivo) acentoCyan else Color.Transparent, RoundedCornerShape(10.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { cronometroActivo = !cronometroActivo }
                            .padding(vertical = 10.dp, horizontal = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Timer, null, tint = if (cronometroActivo) acentoCyan else Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(textoCronometro, color = if (cronometroActivo) acentoCyan else Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .clickable {
                                inputMinutos = (tiempoMaximoDescanso / 60).toString()
                                inputSegundos = (tiempoMaximoDescanso % 60).toString()
                                mostrarDialogoTiempo = true
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Icon(Icons.Default.Edit, "Editar", tint = Color.Gray, modifier = Modifier.size(18.dp))
                    }
                }

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .background(if (notaTexto.isNotEmpty()) acentoCyan.copy(alpha = 0.2f) else tarjetaColor, RoundedCornerShape(10.dp))
                        .border(1.dp, if (notaTexto.isNotEmpty()) acentoCyan else Color.Transparent, RoundedCornerShape(10.dp))
                        .clickable { mostrarDialogoNota = true }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(if (notaTexto.isNotEmpty()) Icons.Default.Edit else Icons.Default.Add, null, tint = if (notaTexto.isNotEmpty()) acentoCyan else Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (notaTexto.isNotEmpty()) "Ver nota" else "Nota", color = if (notaTexto.isNotEmpty()) acentoCyan else Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (esDesdeBuscador) {
                Button(
                    onClick = { mostrarDialogoRutinas = true },
                    colors = ButtonDefaults.buttonColors(containerColor = acentoCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Icon(Icons.Default.Add, null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AGREGAR A MI RUTINA", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // LISTA DE SERIES
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                itemsIndexed(listaSeries) { index, serie ->
                    ItemSerieInteractiva(
                        numeroSerie = index + 1, reps = serie.reps, peso = serie.peso, esUltimo = index == listaSeries.lastIndex,
                        tarjetaColor = tarjetaColor, acentoColor = acentoCyan,
                        onRepsChange = { nuevoValor -> listaSeries[index] = serie.copy(reps = nuevoValor) },
                        onPesoChange = { nuevoValor -> listaSeries[index] = serie.copy(peso = nuevoValor) }
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 80.dp).clickable { listaSeries.add(SerieData("10", "0")) },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, null, tint = acentoCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Añadir serie", color = acentoCyan, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (mostrarDialogoTiempo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoTiempo = false },
            containerColor = tarjetaColor,
            title = { Text("Tiempo de descanso", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = inputMinutos, onValueChange = { inputMinutos = it },
                        label = { Text("Minutos", color = Color.Gray) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = inputSegundos, onValueChange = { inputSegundos = it },
                        label = { Text("Segundos", color = Color.Gray) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.weight(1f)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val m = inputMinutos.toIntOrNull() ?: 0
                    val s = inputSegundos.toIntOrNull() ?: 0
                    if (m >= 0 && s >= 0 && (m > 0 || s > 0)) {
                        tiempoMaximoDescanso = (m * 60) + s
                        tiempoRestante = tiempoMaximoDescanso
                    }
                    mostrarDialogoTiempo = false
                }) { Text("Aplicar", color = acentoCyan, fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { mostrarDialogoTiempo = false }) { Text("Cancelar", color = Color.Gray) } }
        )
    }

    if (mostrarDialogoRutinas) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoRutinas = false; nuevaRutinaTexto = "" },
            containerColor = tarjetaColor,
            title = { Text("Agregar a rutina", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Selecciona una de tus rutinas:", color = Color.Gray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(modifier = Modifier.heightIn(max = 200.dp)) {
                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            items(nombresRutinas) { nombre ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color(0xFF0C101A)).clickable {
                                        mostrarDialogoRutinas = false; onAgregarARutina(nombre)
                                    }.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.FitnessCenter, null, tint = acentoCyan, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(nombre, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("O crea una nueva:", color = Color.Gray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = nuevaRutinaTexto, onValueChange = { nuevaRutinaTexto = it },
                        placeholder = { Text("Ej: Día 5: Glúteo", color = Color.Gray) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = acentoCyan, unfocusedBorderColor = Color.DarkGray),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = { TextButton(onClick = { if (nuevaRutinaTexto.isNotBlank()) { mostrarDialogoRutinas = false; onAgregarARutina(nuevaRutinaTexto) } }) { Text("Crear y agregar", color = acentoCyan, fontWeight = FontWeight.Bold) } },
            dismissButton = { TextButton(onClick = { mostrarDialogoRutinas = false; nuevaRutinaTexto = "" }) { Text("Cancelar", color = Color.Gray) } }
        )
    }

    if (mostrarDialogoNota) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoNota = false },
            containerColor = tarjetaColor,
            title = { Text("Nota del ejercicio", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = notaTexto, onValueChange = { notaTexto = it },
                    placeholder = { Text("Ej: Bajar más lento...", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = acentoCyan, unfocusedBorderColor = Color.DarkGray),
                    modifier = Modifier.fillMaxWidth().height(120.dp)
                )
            },
            confirmButton = { TextButton(onClick = { mostrarDialogoNota = false }) { Text("Guardar", color = acentoCyan, fontWeight = FontWeight.Bold) } },
            dismissButton = { TextButton(onClick = { notaTexto = ""; mostrarDialogoNota = false }) { Text("Borrar", color = Color.Red) } }
        )
    }
}

@Composable
fun ItemSerieInteractiva(numeroSerie: Int, reps: String, peso: String, esUltimo: Boolean, tarjetaColor: Color, acentoColor: Color, onRepsChange: (String) -> Unit, onPesoChange: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(40.dp)) {
            Box(modifier = Modifier.size(28.dp).background(tarjetaColor, CircleShape), contentAlignment = Alignment.Center) {
                Text(text = numeroSerie.toString(), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            if (!esUltimo) {
                Box(modifier = Modifier.width(2.dp).fillMaxHeight().background(tarjetaColor).padding(vertical = 4.dp))
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Row(
            modifier = Modifier.weight(1f).padding(bottom = 16.dp).border(1.dp, tarjetaColor, RoundedCornerShape(12.dp)).background(Color(0xFF0C101A), RoundedCornerShape(12.dp)).padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                BasicTextField(value = reps, onValueChange = onRepsChange, textStyle = TextStyle(color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.width(IntrinsicSize.Min).defaultMinSize(minWidth = 28.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "reps", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(bottom = 3.dp))
            }
            Text(text = "•", color = Color.DarkGray, fontSize = 24.sp)
            Row(verticalAlignment = Alignment.Bottom) {
                BasicTextField(value = peso, onValueChange = onPesoChange, textStyle = TextStyle(color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.width(IntrinsicSize.Min).defaultMinSize(minWidth = 28.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "kg", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(bottom = 3.dp))
            }
        }
    }
}