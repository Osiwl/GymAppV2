package com.example.gymappv2.ui.perfil

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilUsuarioScreen(
    perfilUsuario: PerfilUsuario,
    onBackClick: () -> Unit,
    onGuardarPerfil: (PerfilUsuario) -> Unit
) {

    val bgDark = Color(0xFF07090F)
    val cardDark = Color(0xFF141A29)
    val cyanAccent = Color(0xFF4AC4CF)

    val context = LocalContext.current


    // =========================================================
    // DATOS FÍSICOS
    // =========================================================

    var edad by remember(perfilUsuario.edad) {

        mutableStateOf(
            if (perfilUsuario.edad > 0) {
                perfilUsuario.edad.toString()
            } else {
                ""
            }
        )
    }


    var pesoCorporal by remember(perfilUsuario.pesoKg) {

        mutableStateOf(
            if (perfilUsuario.pesoKg > 0f) {
                perfilUsuario.pesoKg
                    .roundToInt()
                    .toString()
            } else {
                ""
            }
        )
    }


    var altura by remember(perfilUsuario.alturaCm) {

        mutableStateOf(
            if (perfilUsuario.alturaCm > 0f) {
                perfilUsuario.alturaCm
                    .roundToInt()
                    .toString()
            } else {
                ""
            }
        )
    }


    // =========================================================
    // RÉCORDS PERSONALES
    // =========================================================

    var benchPress by remember(
        perfilUsuario.pressBancaKg
    ) {

        mutableStateOf(
            if (perfilUsuario.pressBancaKg > 0.0) {
                perfilUsuario.pressBancaKg
                    .toString()
                    .removeSuffix(".0")
            } else {
                ""
            }
        )
    }


    var deadlift by remember(
        perfilUsuario.pesoMuertoKg
    ) {

        mutableStateOf(
            if (perfilUsuario.pesoMuertoKg > 0.0) {
                perfilUsuario.pesoMuertoKg
                    .toString()
                    .removeSuffix(".0")
            } else {
                ""
            }
        )
    }


    var squat by remember(
        perfilUsuario.sentadillaKg
    ) {

        mutableStateOf(
            if (perfilUsuario.sentadillaKg > 0.0) {
                perfilUsuario.sentadillaKg
                    .toString()
                    .removeSuffix(".0")
            } else {
                ""
            }
        )
    }


    BackHandler(
        onBack = onBackClick
    )


    // =========================================================
    // PANTALLA
    // =========================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgDark)
            .padding(horizontal = 16.dp)
            .verticalScroll(
                rememberScrollState()
            )
    ) {


        Spacer(
            modifier = Modifier.height(16.dp)
        )


        // =====================================================
        // CABECERA
        // =====================================================

        Row(
            verticalAlignment =
                Alignment.CenterVertically,
            modifier =
                Modifier.fillMaxWidth()
        ) {

            IconButton(
                onClick = onBackClick,
                modifier =
                    Modifier.offset(
                        x = (-8).dp
                    )
            ) {

                Icon(
                    imageVector =
                        Icons.AutoMirrored
                            .Filled
                            .ArrowBack,
                    contentDescription =
                        "Volver",
                    tint =
                        Color.White
                )
            }


            Text(
                text = "Mi Perfil",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight =
                    FontWeight.Bold
            )
        }


        Spacer(
            modifier = Modifier.height(24.dp)
        )


        // =====================================================
        // FOTO + NOMBRE
        // =====================================================

        Column(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {


            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(cardDark),
                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Person,
                    contentDescription =
                        "Perfil",
                    tint =
                        cyanAccent,
                    modifier =
                        Modifier.size(50.dp)
                )
            }


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            Text(
                text =
                    perfilUsuario.nombre
                        .ifBlank {
                            "Atleta"
                        },
                color =
                    Color.White,
                fontSize =
                    22.sp,
                fontWeight =
                    FontWeight.ExtraBold
            )


            Text(
                text =
                    "Nivel: ${
                        perfilUsuario.experiencia
                            .ifBlank {
                                "Sin definir"
                            }
                    }",
                color =
                    cyanAccent,
                fontSize =
                    14.sp,
                fontWeight =
                    FontWeight.Medium
            )
        }


        Spacer(
            modifier = Modifier.height(32.dp)
        )


        // =====================================================
        // INFORMACIÓN DEL PLAN
        // =====================================================

        Text(
            text = "Mi Plan",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier =
                Modifier.padding(
                    bottom = 8.dp
                )
        )


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(
                        16.dp
                    )
                )
                .background(cardDark)
                .padding(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(
                    14.dp
                )
        ) {


            DatoPerfil(
                titulo =
                    "Objetivo",
                valor =
                    perfilUsuario.objetivo
                        .ifBlank {
                            "Sin definir"
                        },
                color =
                    cyanAccent
            )


            DatoPerfil(
                titulo =
                    "Días de entrenamiento",
                valor =
                    if (
                        perfilUsuario
                            .diasEntrenamiento > 0
                    ) {

                        "${perfilUsuario.diasEntrenamiento} días"

                    } else {

                        "Sin definir"
                    },
                color =
                    cyanAccent
            )


            DatoPerfil(
                titulo =
                    "Género",
                valor =
                    perfilUsuario.genero
                        .ifBlank {
                            "Sin definir"
                        },
                color =
                    cyanAccent
            )


            DatoPerfil(
                titulo =
                    "Tipo de rutina",
                valor =
                    if (
                        perfilUsuario
                            .rutinaAutomatica
                    ) {

                        "Rutina inteligente"

                    } else {

                        "Rutina personalizada"
                    },
                color =
                    cyanAccent
            )
        }


        Spacer(
            modifier = Modifier.height(24.dp)
        )


        // =====================================================
        // DATOS FÍSICOS
        // =====================================================

        Text(
            text = "Datos Físicos",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier =
                Modifier.padding(
                    bottom = 8.dp
                )
        )


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(
                        16.dp
                    )
                )
                .background(cardDark)
                .padding(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(
                    16.dp
                )
        ) {


            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(
                        16.dp
                    )
            ) {


                OutlinedTextField(
                    value =
                        pesoCorporal,

                    onValueChange = {

                        pesoCorporal =
                            it.filter {
                                    char ->

                                char.isDigit() ||
                                        char == '.'
                            }
                    },

                    label = {

                        Text(
                            "Peso (kg)",
                            color =
                                Color.Gray
                        )
                    },

                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Decimal
                        ),

                    colors =
                        OutlinedTextFieldDefaults
                            .colors(
                                focusedTextColor =
                                    Color.White,
                                unfocusedTextColor =
                                    Color.White,
                                focusedBorderColor =
                                    cyanAccent,
                                unfocusedBorderColor =
                                    Color.DarkGray
                            ),

                    modifier =
                        Modifier.weight(1f),

                    singleLine =
                        true
                )


                OutlinedTextField(
                    value =
                        altura,

                    onValueChange = {

                        altura =
                            it.filter {
                                    char ->

                                char.isDigit() ||
                                        char == '.'
                            }
                    },

                    label = {

                        Text(
                            "Altura (cm)",
                            color =
                                Color.Gray
                        )
                    },

                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Decimal
                        ),

                    colors =
                        OutlinedTextFieldDefaults
                            .colors(
                                focusedTextColor =
                                    Color.White,
                                unfocusedTextColor =
                                    Color.White,
                                focusedBorderColor =
                                    cyanAccent,
                                unfocusedBorderColor =
                                    Color.DarkGray
                            ),

                    modifier =
                        Modifier.weight(1f),

                    singleLine =
                        true
                )
            }


            OutlinedTextField(
                value =
                    edad,

                onValueChange = {

                    edad =
                        it.filter {
                                char ->

                            char.isDigit()
                        }
                            .take(3)
                },

                label = {

                    Text(
                        "Edad (años)",
                        color =
                            Color.Gray
                    )
                },

                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Number
                    ),

                colors =
                    OutlinedTextFieldDefaults
                        .colors(
                            focusedTextColor =
                                Color.White,
                            unfocusedTextColor =
                                Color.White,
                            focusedBorderColor =
                                cyanAccent,
                            unfocusedBorderColor =
                                Color.DarkGray
                        ),

                modifier =
                    Modifier.fillMaxWidth(),

                singleLine =
                    true
            )
        }


        Spacer(
            modifier = Modifier.height(24.dp)
        )


        // =====================================================
        // RÉCORDS
        // =====================================================

        Text(
            text =
                "Fuerza Máxima (1RM)",
            color =
                Color.Gray,
            fontSize =
                14.sp,
            modifier =
                Modifier.padding(
                    bottom = 8.dp
                )
        )


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(
                        16.dp
                    )
                )
                .background(cardDark)
                .padding(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(
                    16.dp
                )
        ) {


            OutlinedTextField(
                value =
                    benchPress,

                onValueChange = {

                    benchPress =
                        it.filter {
                                char ->

                            char.isDigit() ||
                                    char == '.'
                        }
                },

                label = {

                    Text(
                        "Press de Banca (kg)",
                        color =
                            Color.Gray
                    )
                },

                leadingIcon = {

                    Icon(
                        Icons.Default
                            .MonitorWeight,
                        contentDescription =
                            null,
                        tint =
                            cyanAccent
                    )
                },

                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Decimal
                    ),

                colors =
                    OutlinedTextFieldDefaults
                        .colors(
                            focusedTextColor =
                                Color.White,
                            unfocusedTextColor =
                                Color.White,
                            focusedBorderColor =
                                cyanAccent,
                            unfocusedBorderColor =
                                Color.DarkGray
                        ),

                modifier =
                    Modifier.fillMaxWidth(),

                singleLine =
                    true
            )


            OutlinedTextField(
                value =
                    deadlift,

                onValueChange = {

                    deadlift =
                        it.filter {
                                char ->

                            char.isDigit() ||
                                    char == '.'
                        }
                },

                label = {

                    Text(
                        "Peso Muerto (kg)",
                        color =
                            Color.Gray
                    )
                },

                leadingIcon = {

                    Icon(
                        Icons.Default
                            .MonitorWeight,
                        contentDescription =
                            null,
                        tint =
                            cyanAccent
                    )
                },

                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Decimal
                    ),

                colors =
                    OutlinedTextFieldDefaults
                        .colors(
                            focusedTextColor =
                                Color.White,
                            unfocusedTextColor =
                                Color.White,
                            focusedBorderColor =
                                cyanAccent,
                            unfocusedBorderColor =
                                Color.DarkGray
                        ),

                modifier =
                    Modifier.fillMaxWidth(),

                singleLine =
                    true
            )


            OutlinedTextField(
                value =
                    squat,

                onValueChange = {

                    squat =
                        it.filter {
                                char ->

                            char.isDigit() ||
                                    char == '.'
                        }
                },

                label = {

                    Text(
                        "Sentadilla (kg)",
                        color =
                            Color.Gray
                    )
                },

                leadingIcon = {

                    Icon(
                        Icons.Default
                            .MonitorWeight,
                        contentDescription =
                            null,
                        tint =
                            cyanAccent
                    )
                },

                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Decimal
                    ),

                colors =
                    OutlinedTextFieldDefaults
                        .colors(
                            focusedTextColor =
                                Color.White,
                            unfocusedTextColor =
                                Color.White,
                            focusedBorderColor =
                                cyanAccent,
                            unfocusedBorderColor =
                                Color.DarkGray
                        ),

                modifier =
                    Modifier.fillMaxWidth(),

                singleLine =
                    true
            )
        }


        Spacer(
            modifier = Modifier.height(32.dp)
        )


        // =====================================================
        // GUARDAR
        // =====================================================

        Button(

            onClick = {

                val pesoNumero =
                    pesoCorporal
                        .toFloatOrNull()

                val alturaNumero =
                    altura
                        .toFloatOrNull()

                val edadNumero =
                    edad
                        .toIntOrNull()


                if (
                    pesoNumero == null ||
                    pesoNumero <= 0f
                ) {

                    Toast.makeText(
                        context,
                        "Ingresa un peso válido",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@Button
                }


                if (
                    alturaNumero == null ||
                    alturaNumero <= 0f
                ) {

                    Toast.makeText(
                        context,
                        "Ingresa una altura válida",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@Button
                }


                if (
                    edadNumero == null ||
                    edadNumero !in 1..120
                ) {

                    Toast.makeText(
                        context,
                        "Ingresa una edad válida",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@Button
                }


                val nuevoPerfil =
                    perfilUsuario.copy(

                        pesoKg =
                            pesoNumero,

                        alturaCm =
                            alturaNumero,

                        edad =
                            edadNumero,

                        pressBancaKg =
                            benchPress
                                .toDoubleOrNull()
                                ?: 0.0,

                        pesoMuertoKg =
                            deadlift
                                .toDoubleOrNull()
                                ?: 0.0,

                        sentadillaKg =
                            squat
                                .toDoubleOrNull()
                                ?: 0.0
                    )


                onGuardarPerfil(
                    nuevoPerfil
                )


                Toast.makeText(
                    context,
                    "Perfil actualizado",
                    Toast.LENGTH_SHORT
                ).show()
            },

            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        cyanAccent
                ),

            shape =
                RoundedCornerShape(
                    12.dp
                ),

            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {


            Icon(
                Icons.Default.Save,
                contentDescription =
                    null,
                tint =
                    bgDark
            )


            Spacer(
                modifier =
                    Modifier.width(
                        8.dp
                    )
            )


            Text(
                text =
                    "GUARDAR PERFIL",
                color =
                    bgDark,
                fontSize =
                    16.sp,
                fontWeight =
                    FontWeight.ExtraBold
            )
        }


        Spacer(
            modifier =
                Modifier.height(
                    100.dp
                )
        )
    }
}


// =============================================================
// FILA DE INFORMACIÓN
// =============================================================

@Composable
private fun DatoPerfil(
    titulo: String,
    valor: String,
    color: Color
) {

    Row(
        modifier =
            Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.SpaceBetween,
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(
            text =
                titulo,
            color =
                Color.Gray,
            fontSize =
                14.sp
        )

        Text(
            text =
                valor,
            color =
                color,
            fontSize =
                14.sp,
            fontWeight =
                FontWeight.Bold
        )
    }
}