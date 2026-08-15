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
fun DaysScreen(
    onBackClick: () -> Unit,
    onNextClick: (Int) -> Unit
) {
    val bgDark = Color(0xFF07090F)
    val neonCyan = Color(0xFF00F0FF)
    val glassCard = Color(0xFF141A29)

    var selectedDays by remember {
        mutableIntStateOf(0)
    }

    val options = listOf(
        3,
        4,
        5,
        6
    )

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

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {

                TextButton(
                    onClick = onBackClick
                ) {

                    Text(
                        text = "← Volver",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            LinearProgressIndicator(
                progress = { 0.60f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = neonCyan,
                trackColor = glassCard
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Construyamos el hábito.",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Días a la semana que entrenaré...",
                color = neonCyan,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            options.forEach { dias ->

                val seleccionado =
                    selectedDays == dias

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clip(
                            RoundedCornerShape(50)
                        )
                        .background(
                            if (seleccionado) {
                                neonCyan
                            } else {
                                glassCard
                            }
                        )
                        .clickable {
                            selectedDays = dias
                        }
                        .padding(
                            vertical = 16.dp
                        ),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text = "$dias días",
                        color =
                            if (seleccionado) {
                                Color.Black
                            } else {
                                Color.Gray
                            },
                        fontWeight =
                            FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        Button(
            onClick = {
                if (selectedDays > 0) {
                    onNextClick(
                        selectedDays
                    )
                }
            },
            enabled =
                selectedDays > 0,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .align(
                    Alignment.BottomCenter
                )
                .clip(
                    RoundedCornerShape(30.dp)
                ),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        neonCyan,
                    disabledContainerColor =
                        glassCard
                )
        ) {

            Text(
                text = "SIGUIENTE",
                color =
                    if (selectedDays > 0) {
                        Color.Black
                    } else {
                        Color.Gray
                    },
                fontSize = 16.sp,
                fontWeight =
                    FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
        }
    }
}