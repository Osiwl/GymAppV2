package com.example.gymappv2.ui.rutinas

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.gymappv2.ui.screens.cachéEjerciciosGlobal
import java.text.Normalizer

// 🔥 HERRAMIENTA 1: Quitamos acentos para búsquedas perfectas
fun quitarAcentos(texto: String): String {
    return Normalizer.normalize(texto, Normalizer.Form.NFD)
        .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
}

// 🔥 HERRAMIENTA 2: Clasificador de músculos (Ultra específico)
fun coincideCategoria(ejercicio: EjercicioApp, categoria: String): Boolean {
    if (categoria == "Todos") return true

    val txt = quitarAcentos("${ejercicio.musculo} ${ejercicio.nombreEs} ${ejercicio.idFirebase}").lowercase()

    return when (categoria) {
        "Cuádriceps" -> txt.contains("cuadriceps") || txt.contains("quad") || txt.contains("sentadilla") || txt.contains("squat") || (txt.contains("prensa") && !txt.contains("pecho"))
        "Femoral" -> txt.contains("femoral") || txt.contains("isquio") || txt.contains("hamstring") || txt.contains("peso muerto") || txt.contains("deadlift")
        "Glúteo" -> txt.contains("glute") || txt.contains("hip thrust") || txt.contains("puente")
        "Pantorrilla" -> txt.contains("pantorrilla") || txt.contains("calf") || txt.contains("gemelo")
        "Pecho" -> txt.contains("pecho") || txt.contains("chest") || txt.contains("pectoral") || txt.contains("pec")
        "Espalda" -> txt.contains("espalda") || txt.contains("back") || txt.contains("lat") || txt.contains("dorsal") || txt.contains("row") || txt.contains("remo")
        "Hombro Frontal" -> (txt.contains("hombro") || txt.contains("delt")) && (txt.contains("anterior") || txt.contains("front") || txt.contains("press"))
        "Hombro Medio" -> (txt.contains("hombro") || txt.contains("delt")) && (txt.contains("lateral") || txt.contains("medio") || txt.contains("side") || txt.contains("vuelo"))
        "Hombro Posterior" -> (txt.contains("hombro") || txt.contains("delt")) && (txt.contains("posterior") || txt.contains("rear") || txt.contains("pajaro"))
        "Tríceps" -> txt.contains("tricep")
        "Bíceps" -> txt.contains("bicep") || txt.contains("curl")
        "Abdomen" -> txt.contains("abdom") || txt.contains("core") || txt.contains("oblique") || txt.contains("crunch")
        else -> false
    }
}

@Composable
fun BuscadorEjerciciosScreen(
    onBackClick: () -> Unit,
    onEjercicioSeleccionado: (EjercicioApp) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("Todos") }

    val categorias = listOf(
        "Todos", "Pecho", "Espalda", "Cuádriceps", "Femoral", "Glúteo", "Pantorrilla",
        "Hombro Frontal", "Hombro Medio", "Hombro Posterior", "Tríceps", "Bíceps", "Abdomen"
    )

    val bgDark = Color(0xFF07090F)
    val cardDark = Color(0xFF141A29)
    val cyanAccent = Color(0xFF4AC4CF)

    BackHandler(onBack = onBackClick)

    val listaFiltrada = remember(searchQuery, categoriaSeleccionada, cachéEjerciciosGlobal) {
        val queryLimpia = quitarAcentos(searchQuery.lowercase().trim())

        // 🔥 TRADUCTOR MAGISTRAL DE FRASES EXACTAS
        var queryExpandida = queryLimpia
            .replace("remo en t", "t-bar row")
            .replace("remo t", "t-bar row")
            .replace("press militar", "mancuerna sentado hombro press")
            .replace("sentadilla bulgara", "bulgarian split squat")
            .replace("peso muerto", "deadlift")
            .replace("press frances", "skullcrusher")
            .replace("rompecraneos", "skullcrusher")
            .replace("patada de mula", "triceps kickback")
            .replace("patada de triceps", "triceps kickback")
            .replace("buenos dias", "good morning")

        val terminosBusqueda = queryExpandida.split(" ").filter {
            it.isNotBlank() && it !in listOf("en", "con", "de", "el", "la", "los", "las", "a", "para", "y", "al", "del")
        }

        cachéEjerciciosGlobal.values.filter { ej ->
            val textoBusquedaEj = quitarAcentos("${ej.nombreEs} ${ej.idFirebase} ${ej.musculo}").lowercase()

            // 1. DICCIONARIO DE SINÓNIMOS PALABRA POR PALABRA
            val coincideTexto = terminosBusqueda.isEmpty() || terminosBusqueda.all { termino ->
                var matched = textoBusquedaEj.contains(termino)

                if (!matched) {
                    val sinonimos = when (termino) {
                        "lagartijas", "lagartija", "flexiones", "flexion" -> listOf("push-up", "push up")
                        "desplantes", "desplante", "zancadas", "zancada" -> listOf("lunge")
                        "cristos", "aperturas", "mariposas" -> listOf("fly", "pec deck", "flye")
                        "vuelos" -> listOf("lateral raise", "elevacion lateral")
                        "gemelos", "chamorros", "chamorro" -> listOf("calf", "pantorrilla")
                        "copa" -> listOf("triceps extension", "overhead")
                        "pajaros", "pajaro" -> listOf("rear delt", "posterior")
                        "jalon", "jalones" -> listOf("pulldown")
                        "dominadas", "dominada", "barras" -> listOf("pull-up", "chin up")
                        "fondos", "fondo" -> listOf("dip", "dips")
                        "puente", "hip" -> listOf("glute bridge", "hip thrust")
                        "remo" -> listOf("row")
                        "cuadriceps" -> listOf("quad")
                        "femoral", "isquios", "isquiotibial" -> listOf("hamstring")
                        "gluteo", "nalga" -> listOf("glute")
                        "pecho" -> listOf("chest", "pectoral")
                        "espalda" -> listOf("back", "lat")
                        "hombro", "hombros" -> listOf("shoulder", "delt")
                        "bicep", "biceps" -> listOf("curl")
                        "tricep", "triceps" -> listOf("extension", "pushdown")
                        "abdomen", "abs", "core" -> listOf("crunch", "sit-up", "plank")
                        "barra" -> listOf("barbell")
                        "mancuerna", "mancuernas" -> listOf("dumbbell")
                        "polea", "cable" -> listOf("cable")
                        "maquina" -> listOf("lever", "machine", "smith")
                        else -> emptyList()
                    }
                    matched = sinonimos.any { textoBusquedaEj.contains(it) }
                }
                matched
            }

            // 2. VERIFICA EL FILTRO (CHIP)
            val coincideCat = coincideCategoria(ej, categoriaSeleccionada)

            coincideTexto && coincideCat
        }.sortedBy { it.nombreEs }
    }

    Column(modifier = Modifier.fillMaxSize().background(bgDark).padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onBackClick, modifier = Modifier.offset(x = (-8).dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White)
            }
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Ej: Remo en T, cristos, vuelos...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) { Icon(Icons.Default.Close, null, tint = Color.Gray) }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                    focusedContainerColor = cardDark, unfocusedContainerColor = cardDark,
                    focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categorias) { categoria ->
                val isSelected = categoriaSeleccionada == categoria
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) cyanAccent else cardDark)
                        .clickable { categoriaSeleccionada = categoria }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = categoria,
                        color = if (isSelected) bgDark else Color.White,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (listaFiltrada.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(top = 40.dp), contentAlignment = Alignment.TopCenter) {
                Text("No se encontraron ejercicios 🤔", color = Color.Gray, fontSize = 16.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(listaFiltrada) { ejercicio ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onEjercicioSeleccionado(ejercicio) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
                            if (ejercicio.urlImagen.isNotEmpty()) {
                                AsyncImage(
                                    model = ejercicio.urlImagen,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize().padding(4.dp),
                                    contentScale = ContentScale.Fit
                                )
                            } else {
                                Icon(Icons.Default.FitnessCenter, null, tint = Color.Gray)
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = ejercicio.nombreEs, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(text = ejercicio.musculo, color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}