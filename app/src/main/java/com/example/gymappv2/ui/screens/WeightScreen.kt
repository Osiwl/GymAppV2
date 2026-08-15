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
import kotlin.math.roundToInt

@Composable
fun WeightScreen(
    onBackClick: () -> Unit,
    onNextClick: (Float) -> Unit
) {
    val bgDark = Color(0xFF07090F)
    val neonCyan = Color(0xFF00F0FF)
    val glassCard = Color(0xFF141A29)

    var isKg by remember {
        mutableStateOf(true)
    }

    var weightKg by remember {
        mutableFloatStateOf(70f)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgDark)
            .padding(24.dp)
    ) {

        Column(
            modifier =
                Modifier.fillMaxSize(),
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
                progress = { 0.80f },
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
                    "¿Cuál es tu peso actual?",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight =
                    FontWeight.Bold,
                textAlign =
                    TextAlign.Center
            )

            Spacer(
                modifier =
                    Modifier.height(32.dp)
            )

            Row(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            50
                        )
                    )
                    .background(
                        glassCard
                    )
                    .padding(4.dp)
            ) {

                // KG
                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(
                                50
                            )
                        )
                        .background(
                            if (isKg) {
                                neonCyan
                            } else {
                                Color.Transparent
                            }
                        )
                        .clickable {
                            isKg = true
                        }
                        .padding(
                            horizontal =
                                32.dp,
                            vertical =
                                12.dp
                        ),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text = "kg",
                        color =
                            if (isKg) {
                                Color.Black
                            } else {
                                Color.Gray
                            },
                        fontWeight =
                            FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                // LB
                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(
                                50
                            )
                        )
                        .background(
                            if (!isKg) {
                                neonCyan
                            } else {
                                Color.Transparent
                            }
                        )
                        .clickable {
                            isKg = false
                        }
                        .padding(
                            horizontal =
                                32.dp,
                            vertical =
                                12.dp
                        ),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text = "lb",
                        color =
                            if (!isKg) {
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

            Spacer(
                modifier =
                    Modifier.height(60.dp)
            )

            Row(
                verticalAlignment =
                    Alignment.Bottom
            ) {

                if (isKg) {

                    Text(
                        text =
                            weightKg
                                .roundToInt()
                                .toString(),
                        color = Color.White,
                        fontSize = 72.sp,
                        fontWeight =
                            FontWeight.Bold
                    )

                    Text(
                        text = "kg",
                        color = neonCyan,
                        fontSize = 24.sp,
                        fontWeight =
                            FontWeight.Medium,
                        modifier =
                            Modifier.padding(
                                bottom = 12.dp,
                                start = 8.dp
                            )
                    )

                } else {

                    val weightLb =
                        (weightKg * 2.20462f)
                            .roundToInt()

                    Text(
                        text =
                            weightLb.toString(),
                        color = Color.White,
                        fontSize = 72.sp,
                        fontWeight =
                            FontWeight.Bold
                    )

                    Text(
                        text = "lb",
                        color = neonCyan,
                        fontSize = 24.sp,
                        fontWeight =
                            FontWeight.Medium,
                        modifier =
                            Modifier.padding(
                                bottom = 12.dp,
                                start = 8.dp
                            )
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )

            Slider(
                value = weightKg,
                onValueChange = {
                    weightKg = it
                },
                valueRange =
                    40f..150f,
                modifier =
                    Modifier.fillMaxWidth(
                        0.9f
                    ),
                colors =
                    SliderDefaults.colors(
                        thumbColor =
                            neonCyan,
                        activeTrackColor =
                            neonCyan,
                        inactiveTrackColor =
                            glassCard
                    )
            )
        }

        Button(
            onClick = {
                onNextClick(
                    weightKg
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .align(
                    Alignment.BottomCenter
                )
                .clip(
                    RoundedCornerShape(
                        30.dp
                    )
                ),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        neonCyan
                )
        ) {

            Text(
                text = "SIGUIENTE",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight =
                    FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
        }
    }
}