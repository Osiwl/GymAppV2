package com.example.gymappv2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AgeScreen(
    onBackClick: () -> Unit,
    onNextClick: (Int) -> Unit
) {
    val bgDark = Color(0xFF07090F)
    val neonCyan = Color(0xFF00F0FF)
    val glassCard = Color(0xFF141A29)

    var age by remember { mutableStateOf("") }

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

            // Botón volver
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                TextButton(onClick = onBackClick) {
                    Text(text = "← Volver", color = Color.Gray, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // BARRA DE PROGRESO (Progreso: 95%)
            LinearProgressIndicator(
                progress = { 0.95f },
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = neonCyan,
                trackColor = glassCard
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "¡Último paso!",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "¿Cuántos años tienes?",
                color = neonCyan,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Input de Edad (Números gigantes centrados)
            OutlinedTextField(
                value = age,
                onValueChange = {
                    // Solo permite números y máximo 3 caracteres
                    if (it.length <= 3) {
                        age = it.filter { char -> char.isDigit() }
                    }
                },
                placeholder = {
                    Text("Ej. 25", color = Color.Gray, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(0.6f), // Lo hacemos un poco más estrecho para que luzca mejor
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                ),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = glassCard,
                    unfocusedContainerColor = glassCard,
                    focusedBorderColor = neonCyan,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )
        }

        // Lógica para que el botón solo se active si la edad es un número mayor a 0
        val edadNumero = age.toIntOrNull()

        val edadValida =
            edadNumero != null &&
                    edadNumero > 0 &&
                    edadNumero <= 120

        Button(
            onClick = {

                edadNumero?.let { edadFinal ->
                    onNextClick(edadFinal)
                }

            },
            enabled = edadValida,
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
                text = "CONTINUAR",
                color = if (edadValida) Color.Black else Color.Gray,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
        }
    }
}