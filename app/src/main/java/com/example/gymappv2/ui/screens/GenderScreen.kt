package com.example.gymappv2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
fun GenderScreen(
    onBackClick: () -> Unit,
    onNextClick: (String) -> Unit
) {
    val bgDark = Color(0xFF07090F)
    val neonCyan = Color(0xFF00F0FF)
    val glassCard = Color(0xFF141A29)

    var selectedOption by remember {
        mutableStateOf("")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgDark)
            .padding(24.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )

            Box(
                modifier =
                    Modifier.fillMaxWidth(),
                contentAlignment =
                    Alignment.CenterStart
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

            Spacer(
                modifier =
                    Modifier.height(32.dp)
            )

            LinearProgressIndicator(
                progress = { 0.70f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = neonCyan,
                trackColor = glassCard
            )

            Spacer(
                modifier =
                    Modifier.height(48.dp)
            )

            Text(
                text =
                    "¡Muy bien! Vamos a poner algo de información básica.",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight =
                    FontWeight.Bold,
                textAlign =
                    TextAlign.Start,
                modifier =
                    Modifier.fillMaxWidth()
            )

            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )

            Text(
                text = "Yo soy...",
                color = neonCyan,
                fontSize = 20.sp,
                fontWeight =
                    FontWeight.Bold,
                textAlign =
                    TextAlign.Start,
                modifier =
                    Modifier.fillMaxWidth()
            )

            Spacer(
                modifier =
                    Modifier.height(32.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                // HOMBRE
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(0.9f)
                        .padding(end = 8.dp)
                        .clip(
                            RoundedCornerShape(
                                24.dp
                            )
                        )
                        .background(
                            glassCard
                        )
                        .border(
                            width =
                                if (
                                    selectedOption ==
                                    "Hombre"
                                ) {
                                    2.dp
                                } else {
                                    0.dp
                                },
                            color =
                                if (
                                    selectedOption ==
                                    "Hombre"
                                ) {
                                    neonCyan
                                } else {
                                    Color.Transparent
                                },
                            shape =
                                RoundedCornerShape(
                                    24.dp
                                )
                        )
                        .clickable {
                            selectedOption =
                                "Hombre"
                        },
                    contentAlignment =
                        Alignment.Center
                ) {

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "♂",
                            color =
                                Color(0xFF00E676),
                            fontSize = 64.sp
                        )

                        Spacer(
                            modifier =
                                Modifier.height(
                                    8.dp
                                )
                        )

                        Text(
                            text = "HOMBRE",
                            color = Color.White,
                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }

                // MUJER
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(0.9f)
                        .padding(start = 8.dp)
                        .clip(
                            RoundedCornerShape(
                                24.dp
                            )
                        )
                        .background(
                            glassCard
                        )
                        .border(
                            width =
                                if (
                                    selectedOption ==
                                    "Mujer"
                                ) {
                                    2.dp
                                } else {
                                    0.dp
                                },
                            color =
                                if (
                                    selectedOption ==
                                    "Mujer"
                                ) {
                                    neonCyan
                                } else {
                                    Color.Transparent
                                },
                            shape =
                                RoundedCornerShape(
                                    24.dp
                                )
                        )
                        .clickable {
                            selectedOption =
                                "Mujer"
                        },
                    contentAlignment =
                        Alignment.Center
                ) {

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "♀",
                            color =
                                Color(0xFFD500F9),
                            fontSize = 64.sp
                        )

                        Spacer(
                            modifier =
                                Modifier.height(
                                    8.dp
                                )
                        )

                        Text(
                            text = "MUJER",
                            color = Color.White,
                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                if (
                    selectedOption.isNotEmpty()
                ) {
                    onNextClick(
                        selectedOption
                    )
                }
            },
            enabled =
                selectedOption.isNotEmpty(),
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
                    if (
                        selectedOption.isNotEmpty()
                    ) {
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