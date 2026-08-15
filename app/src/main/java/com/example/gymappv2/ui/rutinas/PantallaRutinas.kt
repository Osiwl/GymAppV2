package com.example.gymappv2.ui.rutinas

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

import com.example.gymappv2.ui.screens.buscarEjercicioEnCache

@Composable
fun PantallaRutinas(
    listaDias: List<RutinaDia>,
    onAjustesClick: () -> Unit,
    onIniciarClick: (RutinaDia) -> Unit,
    onEjercicioClick: (EjercicioApp) -> Unit,
    onEliminarClick: (RutinaDia) -> Unit,
    onAgregarRutinaClick: () -> Unit,
    onRenombrarClick: (RutinaDia, String) -> Unit,
    onAgregarEjercicioClick: (RutinaDia) -> Unit,
    onQuitarEjercicioClick: (RutinaDia, EjercicioApp) -> Unit
) {
    val fondoOscuro = Color(0xFF07090F)
    var mostrarMenuAjustes by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(fondoOscuro).padding(horizontal = 16.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Mis Rutinas", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Row {
                    Box {
                        IconButton(onClick = { mostrarMenuAjustes = true }) {
                            Icon(Icons.Default.Settings, contentDescription = "Ajustes", tint = Color.White)
                        }
                        DropdownMenu(
                            expanded = mostrarMenuAjustes,
                            onDismissRequest = { mostrarMenuAjustes = false },
                            modifier = Modifier.background(Color(0xFF1A1F2E))
                        ) {
                            DropdownMenuItem(
                                text = { Text("Cerrar Sesión", color = Color(0xFFEF5350)) },
                                onClick = { mostrarMenuAjustes = false; onAjustesClick() }
                            )
                        }
                    }
                    IconButton(onClick = onAgregarRutinaClick) {
                        Icon(Icons.Default.Add, contentDescription = "Nueva", tint = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (listaDias.isEmpty()) {
                Text("No tienes rutinas activas.", color = Color.Gray, modifier = Modifier.padding(top = 20.dp))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(listaDias) { rutinaDia ->
                        ItemRutinaExpandida(
                            rutinaDia = rutinaDia,
                            onIniciar = { onIniciarClick(rutinaDia) },
                            onEjercicioSeleccionado = onEjercicioClick,
                            onDelete = { onEliminarClick(rutinaDia) },
                            onRename = { nuevoNombre -> onRenombrarClick(rutinaDia, nuevoNombre) },
                            onAgregarEj = { onAgregarEjercicioClick(rutinaDia) },
                            onQuitarEj = { ej -> onQuitarEjercicioClick(rutinaDia, ej) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemRutinaExpandida(
    rutinaDia: RutinaDia,
    onIniciar: () -> Unit,
    onEjercicioSeleccionado: (EjercicioApp) -> Unit,
    onDelete: () -> Unit,
    onRename: (String) -> Unit,
    onAgregarEj: () -> Unit,
    onQuitarEj: (EjercicioApp) -> Unit
) {
    val tarjetaColor = Color(0xFF141A29)
    val botonAzul = Color(0xFF3BA0E3)
    val colorRojo = Color(0xFFEF5350)

    var menuExpandido by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }

    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    var mostrarDialogoRenombrar by remember { mutableStateOf(false) }
    var inputNuevoNombre by remember { mutableStateOf(rutinaDia.titulo) }

    val ejerciciosAMostrar = if (isExpanded || isEditing) rutinaDia.ejercicios else rutinaDia.ejercicios.take(3)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(tarjetaColor)
            .animateContentSize(animationSpec = tween(300))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { if (!isEditing) isExpanded = !isExpanded }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = rutinaDia.titulo,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { inputNuevoNombre = rutinaDia.titulo; mostrarDialogoRenombrar = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Renombrar", tint = Color.Gray)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${rutinaDia.ejercicios.size} ejercicios • ${rutinaDia.ejercicios.size * 3} series", color = Color.Gray, fontSize = 14.sp)
            }

            Box {
                IconButton(onClick = { menuExpandido = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Opciones", tint = Color.Gray)
                }

                DropdownMenu(
                    expanded = menuExpandido,
                    onDismissRequest = { menuExpandido = false },
                    modifier = Modifier.background(Color(0xFF1A1F2E))
                ) {
                    DropdownMenuItem(
                        text = { Text("Editar rutina", color = Color.White) },
                        onClick = {
                            menuExpandido = false
                            isEditing = true
                            isExpanded = true
                        },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White) }
                    )
                    DropdownMenuItem(
                        text = { Text("Eliminar rutina", color = colorRojo) },
                        onClick = { menuExpandido = false; mostrarDialogoEliminar = true }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ejerciciosAMostrar.forEach { ejercicio ->
            val dataEnCache = buscarEjercicioEnCache(ejercicio)
            val imagenFinal = ejercicio.urlImagen.ifEmpty { dataEnCache?.urlImagen ?: "" }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { if (!isEditing) onEjercicioSeleccionado(ejercicio) }
                    .padding(vertical = 8.dp, horizontal = 4.dp)
            ) {
                Box(
                    modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    if (imagenFinal.isNotEmpty()) {
                        // 🔥 CARGA DIRECTA Y PURA (Elimina el parpadeo y los retrasos)
                        AsyncImage(
                            model = imagenFinal,
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize().padding(4.dp)
                        )
                    } else {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = ejercicio.nombreEs, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(text = "3 series", color = Color.Gray, fontSize = 14.sp)
                }
                if (isEditing) {
                    IconButton(onClick = { onQuitarEj(ejercicio) }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Quitar", tint = colorRojo)
                    }
                }
            }
        }

        if (isEditing) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onAgregarEj() }
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = botonAzul)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Agregar ejercicio", color = botonAzul, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { if (!isEditing) isExpanded = !isExpanded }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val restantes = rutinaDia.ejercicios.size - 3
                if (!isEditing && !isExpanded && restantes > 0) {
                    Text(text = "$restantes más", color = botonAzul, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Icon(Icons.Rounded.KeyboardArrowDown, null, tint = botonAzul)
                } else if (!isEditing && isExpanded && restantes > 0) {
                    Text(text = "Ver menos", color = botonAzul, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Icon(Icons.Rounded.KeyboardArrowUp, null, tint = botonAzul)
                }
            }

            if (isEditing) {
                Button(
                    onClick = { isEditing = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4AC4CF)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("LISTO", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                }
            } else {
                Button(
                    onClick = onIniciar,
                    colors = ButtonDefaults.buttonColors(containerColor = botonAzul),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("INICIAR", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
    // ... [Los AlertDialogs de eliminar y renombrar se mantienen iguales al final del archivo original]
    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            containerColor = tarjetaColor,
            title = { Text("¿Eliminar rutina?", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text("Perderás todos los ejercicios de esta rutina.", color = Color.Gray) },
            confirmButton = { TextButton(onClick = { mostrarDialogoEliminar = false; onDelete() }) { Text("Sí, eliminar", color = colorRojo, fontWeight = FontWeight.Bold) } },
            dismissButton = { TextButton(onClick = { mostrarDialogoEliminar = false }) { Text("Cancelar", color = Color.Gray) } }
        )
    }

    if (mostrarDialogoRenombrar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoRenombrar = false },
            containerColor = tarjetaColor,
            title = { Text("Renombrar rutina", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { OutlinedTextField(value = inputNuevoNombre, onValueChange = { inputNuevoNombre = it }, singleLine = true, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = botonAzul, unfocusedBorderColor = Color.DarkGray), modifier = Modifier.fillMaxWidth()) },
            confirmButton = { TextButton(onClick = { mostrarDialogoRenombrar = false; if (inputNuevoNombre.isNotBlank()) onRename(inputNuevoNombre) }) { Text("Guardar", color = botonAzul, fontWeight = FontWeight.Bold) } },
            dismissButton = { TextButton(onClick = { mostrarDialogoRenombrar = false }) { Text("Cancelar", color = Color.Gray) } }
        )
    }
}