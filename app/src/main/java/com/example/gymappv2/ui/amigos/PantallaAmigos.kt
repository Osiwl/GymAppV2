package com.example.gymappv2.ui.amigos

import android.graphics.Paint
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Addchart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

import com.example.gymappv2.ui.perfil.PerfilUsuario
import com.example.gymappv2.ui.rutinas.EjercicioApp
import com.example.gymappv2.ui.screens.cachéEjerciciosGlobal

import java.text.Normalizer


// =============================================================
// MODELO DE ATLETA
// =============================================================

data class AtletaRadar(

    val nombre: String,
    val iniciales: String,
    val colorNeon: Color,

    var ptosPecho: Float,
    var ptosEspalda: Float,
    var ptosPierna: Float,
    var ptosBrazo: Float,

    val isMe: Boolean = false

) {

    val totalPuntos: Float
        get() =
            ptosPecho +
                    ptosEspalda +
                    ptosPierna +
                    ptosBrazo
}


// =============================================================
// MODELO DE RETO
// =============================================================

data class EjercicioCompetencia(

    val ejercicioApp: EjercicioApp,

    val idEje: String,

    var miRecordPeso: String = "0",

    var miRecordReps: String = "0"
)


// =============================================================
// QUITAR ACENTOS
// =============================================================

fun quitarAcentosAmigos(
    texto: String
): String {

    return Normalizer
        .normalize(
            texto,
            Normalizer.Form.NFD
        )
        .replace(
            "\\p{InCombiningDiacriticalMarks}+".toRegex(),
            ""
        )
}


// =============================================================
// INICIALES DEL USUARIO
// =============================================================

fun obtenerInicialesAmigos(
    nombre: String
): String {

    val partes =
        nombre
            .trim()
            .split(
                Regex("\\s+")
            )
            .filter {
                it.isNotBlank()
            }


    if (partes.isEmpty()) {
        return "AT"
    }


    if (partes.size == 1) {

        return partes
            .first()
            .take(2)
            .uppercase()
    }


    return (
            partes.first()
                .take(1) +
                    partes.last()
                        .take(1)
            )
        .uppercase()
}


// =============================================================
// DETERMINAR EJE DEL RADAR
// =============================================================

fun determinarEjeDeRadar(
    musculo: String
): String {

    val m =
        quitarAcentosAmigos(
            musculo.lowercase()
        )


    return when {

        m.contains("pech") ||
                m.contains("pectoral") -> {

            "Pecho"
        }


        m.contains("espalda") ||
                m.contains("dorsal") ||
                m.contains("lat") ||
                m.contains("remo") -> {

            "Espalda"
        }


        m.contains("pierna") ||
                m.contains("cuadriceps") ||
                m.contains("femoral") ||
                m.contains("gluteo") ||
                m.contains("pantorrilla") ||
                m.contains("isquio") -> {

            "Pierna"
        }


        else -> {

            "Brazo"
        }
    }
}


// =============================================================
// PANTALLA AMIGOS
// =============================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAmigos(
    perfilUsuario: PerfilUsuario
) {

    val bgDark =
        Color(0xFF07090F)

    val cardDark =
        Color(0xFF141A29)

    val cyanAccent =
        Color(0xFF4AC4CF)

    val redAccent =
        Color(0xFFEF5350)

    val goldGradient =
        Brush.horizontalGradient(
            listOf(
                Color(0xFFFFD700),
                Color(0xFFFFA500)
            )
        )


    val context =
        LocalContext.current


    // =========================================================
    // DATOS REALES DEL USUARIO
    // =========================================================

    val miNombre =
        perfilUsuario.nombre
            .ifBlank {
                "Atleta"
            }


    val misIniciales =
        remember(miNombre) {

            obtenerInicialesAmigos(
                miNombre
            )
        }


    val miPesoCorporalKg =
        perfilUsuario.pesoKg
            .toDouble()


    val esHombre =
        perfilUsuario.genero.equals(
            "Hombre",
            ignoreCase = true
        )


    // =========================================================
    // NAVEGACIÓN
    // =========================================================

    var vistaActual by remember {
        mutableStateOf(
            "dashboard"
        )
    }


    var mostrarDialogoInvitacion by remember {
        mutableStateOf(false)
    }


    var mostrarDialogoEdicion by remember {
        mutableStateOf(false)
    }


    // =========================================================
    // ESTADOS
    // =========================================================

    var queryBusqueda by remember {
        mutableStateOf("")
    }


    var ejercicioActivo by remember {
        mutableStateOf<
                EjercicioCompetencia?
                >(null)
    }


    var inputPeso by remember {
        mutableStateOf("")
    }


    var inputReps by remember {
        mutableStateOf("")
    }


    // =========================================================
    // CÍRCULO DE FUERZA
    // =========================================================

    val miCirculo =
        remember(
            miNombre,
            misIniciales
        ) {

            mutableStateListOf(

                AtletaRadar(
                    nombre =
                        "$miNombre (Tú)",
                    iniciales =
                        misIniciales,
                    colorNeon =
                        Color(0xFF3BA0E3),
                    ptosPecho =
                        0f,
                    ptosEspalda =
                        0f,
                    ptosPierna =
                        0f,
                    ptosBrazo =
                        0f,
                    isMe =
                        true
                ),

                // Por ahora estos dos usuarios
                // siguen siendo ejemplos locales.

                AtletaRadar(
                    nombre =
                        "Ana G.",
                    iniciales =
                        "AG",
                    colorNeon =
                        Color(0xFFFF4081),
                    ptosPecho =
                        150f,
                    ptosEspalda =
                        180f,
                    ptosPierna =
                        220f,
                    ptosBrazo =
                        130f
                ),

                AtletaRadar(
                    nombre =
                        "Luis F.",
                    iniciales =
                        "LF",
                    colorNeon =
                        Color(0xFFFFD700),
                    ptosPecho =
                        210f,
                    ptosEspalda =
                        190f,
                    ptosPierna =
                        160f,
                    ptosBrazo =
                        200f
                )
            )
        }


    // =========================================================
    // RETOS
    // =========================================================

    val rutinaCompetencia =
        remember {

            mutableStateListOf<
                    EjercicioCompetencia
                    >()
        }


    val elMasFuerte =
        miCirculo.maxByOrNull {

            it.totalPuntos
        }


    // =========================================================
    // RECALCULAR PUNTOS DEL USUARIO
    // =========================================================

    fun recalcularMisPuntos() {

        var pPecho =
            0f

        var pEspalda =
            0f

        var pPierna =
            0f

        var pBrazo =
            0f


        rutinaCompetencia
            .forEach { ejercicio ->


                val peso =
                    ejercicio
                        .miRecordPeso
                        .toFloatOrNull()
                        ?: 0f


                val reps =
                    ejercicio
                        .miRecordReps
                        .toFloatOrNull()
                        ?: 0f


                if (
                    peso > 0 &&
                    reps > 0
                ) {

                    // Fórmula de Epley
                    // para aproximar 1RM

                    val rm1 =
                        peso *
                                (
                                        1f +
                                                (
                                                        reps /
                                                                30f
                                                        )
                                        )


                    val wilks =
                        CalculadoraFuerza
                            .calcularPuntaje(

                                pesoCorporalKg =
                                    miPesoCorporalKg,

                                totalLevantadoKg =
                                    rm1.toDouble(),

                                esHombre =
                                    esHombre
                            )
                            .toFloat()


                    when (
                        ejercicio.idEje
                    ) {

                        "Pecho" -> {

                            pPecho +=
                                wilks
                        }


                        "Espalda" -> {

                            pEspalda +=
                                wilks
                        }


                        "Pierna" -> {

                            pPierna +=
                                wilks
                        }


                        "Brazo" -> {

                            pBrazo +=
                                wilks
                        }
                    }
                }
            }


        val indexYo =
            miCirculo
                .indexOfFirst {

                    it.isMe
                }


        if (
            indexYo != -1
        ) {

            val yoActual =
                miCirculo[
                    indexYo
                ]


            miCirculo[
                indexYo
            ] = yoActual.copy(

                ptosPecho =
                    pPecho,

                ptosEspalda =
                    pEspalda,

                ptosPierna =
                    pPierna,

                ptosBrazo =
                    pBrazo
            )
        }
    }


    // =========================================================
    // BOTÓN ATRÁS
    // =========================================================

    BackHandler(
        enabled =
            vistaActual !=
                    "dashboard"
    ) {

        if (
            vistaActual ==
            "buscador_ranking"
        ) {

            vistaActual =
                "rutina_competencia"

        } else {

            vistaActual =
                "dashboard"
        }
    }


    // =========================================================
    // PANTALLA 1 - RADAR
    // =========================================================

    if (
        vistaActual ==
        "dashboard"
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        bgDark
                    )
                    .padding(
                        horizontal =
                            16.dp
                    )
        ) {


            Spacer(
                modifier =
                    Modifier.height(
                        24.dp
                    )
            )


            Row(
                modifier =
                    Modifier
                        .fillMaxWidth(),

                horizontalArrangement =
                    Arrangement
                        .SpaceBetween,

                verticalAlignment =
                    Alignment
                        .CenterVertically
            ) {


                Text(
                    text =
                        "Círculo de Fuerza",

                    color =
                        Color.White,

                    fontSize =
                        28.sp,

                    fontWeight =
                        FontWeight.Bold
                )


                IconButton(
                    onClick = {

                        mostrarDialogoInvitacion =
                            true
                    }
                ) {

                    Icon(
                        imageVector =
                            Icons.Default
                                .PersonAdd,

                        contentDescription =
                            "Invitar",

                        tint =
                            cyanAccent
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(
                        8.dp
                    )
            )


            Text(
                text =
                    "Ranking de fuerza relativa basado en tu peso corporal.",

                color =
                    Color.Gray,

                fontSize =
                    14.sp
            )


            Spacer(
                modifier =
                    Modifier.height(
                        8.dp
                    )
            )


            // Dato visible para comprobar que
            // estamos usando el perfil verdadero.

            Text(
                text =
                    "${
                        perfilUsuario.pesoKg
                            .toInt()
                    } kg · ${
                        perfilUsuario.genero
                    }",

                color =
                    cyanAccent,

                fontSize =
                    13.sp,

                fontWeight =
                    FontWeight.Medium
            )


            Spacer(
                modifier =
                    Modifier.height(
                        24.dp
                    )
            )


            LazyColumn(
                modifier =
                    Modifier.fillMaxSize(),

                contentPadding =
                    PaddingValues(
                        bottom =
                            100.dp
                    )
            ) {


                // =================================================
                // REY DEL CÍRCULO
                // =================================================

                if (
                    elMasFuerte != null &&
                    elMasFuerte
                        .totalPuntos >
                    0
                ) {

                    item {

                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        bottom =
                                            24.dp
                                    )
                                    .clip(
                                        RoundedCornerShape(
                                            16.dp
                                        )
                                    )
                                    .background(
                                        goldGradient
                                    )
                                    .padding(
                                        2.dp
                                    )
                        ) {


                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .clip(
                                            RoundedCornerShape(
                                                14.dp
                                            )
                                        )
                                        .background(
                                            cardDark
                                        )
                                        .padding(
                                            16.dp
                                        ),

                                verticalAlignment =
                                    Alignment
                                        .CenterVertically
                            ) {


                                Icon(
                                    Icons.Default
                                        .EmojiEvents,

                                    contentDescription =
                                        "Corona",

                                    tint =
                                        Color(
                                            0xFFFFD700
                                        ),

                                    modifier =
                                        Modifier.size(
                                            40.dp
                                        )
                                )


                                Spacer(
                                    modifier =
                                        Modifier.width(
                                            16.dp
                                        )
                                )


                                Column {

                                    Text(
                                        text =
                                            "REY DEL CÍRCULO",

                                        color =
                                            Color(
                                                0xFFFFD700
                                            ),

                                        fontSize =
                                            12.sp,

                                        fontWeight =
                                            FontWeight.Black,

                                        letterSpacing =
                                            1.sp
                                    )


                                    Text(
                                        text =
                                            elMasFuerte
                                                .nombre,

                                        color =
                                            Color.White,

                                        fontSize =
                                            22.sp,

                                        fontWeight =
                                            FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }


                // =================================================
                // RADAR
                // =================================================

                item {


                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(
                                    300.dp
                                )
                                .clip(
                                    RoundedCornerShape(
                                        20.dp
                                    )
                                )
                                .background(
                                    cardDark
                                )
                                .padding(
                                    24.dp
                                ),

                        contentAlignment =
                            Alignment.Center
                    ) {


                        Canvas(
                            modifier =
                                Modifier
                                    .fillMaxSize()
                        ) {


                            val center =
                                Offset(
                                    size.width /
                                            2,
                                    size.height /
                                            2
                                )


                            val maxRadius =
                                (
                                        size.minDimension /
                                                2
                                        ) -
                                        30.dp
                                            .toPx()


                            val numAxes =
                                4


                            val labels =
                                listOf(
                                    "Pecho",
                                    "Espalda",
                                    "Pierna",
                                    "Brazo"
                                )


                            val maxScore =
                                miCirculo
                                    .flatMap {

                                        listOf(
                                            it.ptosPecho,
                                            it.ptosEspalda,
                                            it.ptosPierna,
                                            it.ptosBrazo
                                        )
                                    }
                                    .maxOrNull()
                                    ?.coerceAtLeast(
                                        100f
                                    )
                                    ?: 100f


                            // CUADRÍCULA

                            for (
                            step in 1..4
                            ) {

                                val radius =
                                    maxRadius *
                                            (
                                                    step /
                                                            4f
                                                    )


                                val path =
                                    Path()


                                for (
                                i in 0 until
                                        numAxes
                                ) {

                                    val angle =
                                        (
                                                i *
                                                        2 *
                                                        PI /
                                                        numAxes
                                                ) -
                                                (
                                                        PI /
                                                                2
                                                        )


                                    val x =
                                        center.x +
                                                radius *
                                                cos(
                                                    angle
                                                )
                                                    .toFloat()


                                    val y =
                                        center.y +
                                                radius *
                                                sin(
                                                    angle
                                                )
                                                    .toFloat()


                                    if (
                                        i == 0
                                    ) {

                                        path.moveTo(
                                            x,
                                            y
                                        )

                                    } else {

                                        path.lineTo(
                                            x,
                                            y
                                        )
                                    }
                                }


                                path.close()


                                drawPath(
                                    path =
                                        path,

                                    color =
                                        Color.DarkGray
                                            .copy(
                                                alpha =
                                                    0.4f
                                            ),

                                    style =
                                        Stroke(
                                            width =
                                                2f
                                        )
                                )
                            }


                            // ETIQUETAS

                            val textPaint =
                                Paint().apply {

                                    color =
                                        android.graphics
                                            .Color
                                            .WHITE

                                    textSize =
                                        34f

                                    textAlign =
                                        Paint.Align
                                            .CENTER

                                    isFakeBoldText =
                                        true
                                }


                            for (
                            i in 0 until
                                    numAxes
                            ) {

                                val angle =
                                    (
                                            i *
                                                    2 *
                                                    PI /
                                                    numAxes
                                            ) -
                                            (
                                                    PI /
                                                            2
                                                    )


                                val x =
                                    center.x +
                                            maxRadius *
                                            cos(
                                                angle
                                            )
                                                .toFloat()


                                val y =
                                    center.y +
                                            maxRadius *
                                            sin(
                                                angle
                                            )
                                                .toFloat()


                                drawLine(

                                    color =
                                        Color.DarkGray,

                                    start =
                                        center,

                                    end =
                                        Offset(
                                            x,
                                            y
                                        ),

                                    strokeWidth =
                                        2f
                                )


                                val labelX =
                                    center.x +
                                            (
                                                    maxRadius +
                                                            24.dp
                                                                .toPx()
                                                    ) *
                                            cos(
                                                angle
                                            )
                                                .toFloat()


                                val labelY =
                                    center.y +
                                            (
                                                    maxRadius +
                                                            24.dp
                                                                .toPx()
                                                    ) *
                                            sin(
                                                angle
                                            )
                                                .toFloat() +
                                            10f


                                drawContext
                                    .canvas
                                    .nativeCanvas
                                    .drawText(

                                        labels[i],

                                        labelX,

                                        labelY,

                                        textPaint
                                    )
                            }


                            // POLÍGONOS

                            miCirculo
                                .forEach {
                                        atleta ->


                                    val scores =
                                        listOf(

                                            atleta
                                                .ptosPecho,

                                            atleta
                                                .ptosEspalda,

                                            atleta
                                                .ptosPierna,

                                            atleta
                                                .ptosBrazo
                                        )


                                    val path =
                                        Path()


                                    for (
                                    i in 0 until
                                            numAxes
                                    ) {

                                        val angle =
                                            (
                                                    i *
                                                            2 *
                                                            PI /
                                                            numAxes
                                                    ) -
                                                    (
                                                            PI /
                                                                    2
                                                            )


                                        val scoreRadius =
                                            maxRadius *
                                                    (
                                                            scores[i] /
                                                                    maxScore
                                                            )


                                        val x =
                                            center.x +
                                                    scoreRadius *
                                                    cos(
                                                        angle
                                                    )
                                                        .toFloat()


                                        val y =
                                            center.y +
                                                    scoreRadius *
                                                    sin(
                                                        angle
                                                    )
                                                        .toFloat()


                                        if (
                                            i == 0
                                        ) {

                                            path.moveTo(
                                                x,
                                                y
                                            )

                                        } else {

                                            path.lineTo(
                                                x,
                                                y
                                            )
                                        }
                                    }


                                    path.close()


                                    val alphaFill =
                                        if (
                                            atleta.isMe
                                        ) {
                                            0.6f
                                        } else {
                                            0.15f
                                        }


                                    drawPath(

                                        path =
                                            path,

                                        color =
                                            atleta.colorNeon
                                                .copy(
                                                    alpha =
                                                        alphaFill
                                                ),

                                        style =
                                            Fill
                                    )


                                    drawPath(

                                        path =
                                            path,

                                        color =
                                            atleta.colorNeon,

                                        style =
                                            Stroke(

                                                width =
                                                    if (
                                                        atleta.isMe
                                                    ) {
                                                        8f
                                                    } else {
                                                        4f
                                                    }
                                            )
                                    )
                                }
                        }
                    }
                }


                item {

                    Spacer(
                        modifier =
                            Modifier.height(
                                24.dp
                            )
                    )
                }


                // =================================================
                // BOTÓN RETOS
                // =================================================

                item {

                    Button(
                        onClick = {

                            vistaActual =
                                "rutina_competencia"
                        },

                        colors =
                            ButtonDefaults
                                .buttonColors(
                                    containerColor =
                                        cyanAccent
                                ),

                        shape =
                            RoundedCornerShape(
                                16.dp
                            ),

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(
                                    60.dp
                                )
                    ) {


                        Icon(
                            Icons.Default
                                .Addchart,

                            contentDescription =
                                null,

                            tint =
                                bgDark,

                            modifier =
                                Modifier.size(
                                    24.dp
                                )
                        )


                        Spacer(
                            modifier =
                                Modifier.width(
                                    12.dp
                                )
                        )


                        Text(
                            text =
                                "VER RETOS DEL GRUPO",

                            color =
                                bgDark,

                            fontWeight =
                                FontWeight.ExtraBold,

                            fontSize =
                                16.sp
                        )
                    }
                }


                item {

                    Spacer(
                        modifier =
                            Modifier.height(
                                24.dp
                            )
                    )
                }


                // =================================================
                // RANKING
                // =================================================

                itemsIndexed(
                    miCirculo
                        .sortedByDescending {

                            it.totalPuntos
                        }
                ) {
                        index,
                        atleta ->


                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    bottom =
                                        16.dp
                                )
                                .clip(
                                    RoundedCornerShape(
                                        20.dp
                                    )
                                )
                                .background(
                                    cardDark
                                )
                                .padding(
                                    20.dp
                                ),

                        verticalAlignment =
                            Alignment
                                .CenterVertically
                    ) {


                        Text(
                            text =
                                "#${index + 1}",

                            color =
                                Color.Gray,

                            fontSize =
                                20.sp,

                            fontWeight =
                                FontWeight.Black,

                            modifier =
                                Modifier.width(
                                    36.dp
                                )
                        )


                        Box(
                            modifier =
                                Modifier
                                    .size(
                                        18.dp
                                    )
                                    .clip(
                                        CircleShape
                                    )
                                    .background(
                                        atleta
                                            .colorNeon
                                    )
                        )


                        Spacer(
                            modifier =
                                Modifier.width(
                                    12.dp
                                )
                        )


                        Box(
                            modifier =
                                Modifier
                                    .size(
                                        48.dp
                                    )
                                    .clip(
                                        CircleShape
                                    )
                                    .background(
                                        Color(
                                            0xFF2C3549
                                        )
                                    ),

                            contentAlignment =
                                Alignment.Center
                        ) {


                            Text(
                                text =
                                    atleta
                                        .iniciales,

                                color =
                                    Color.White,

                                fontWeight =
                                    FontWeight.Bold,

                                fontSize =
                                    18.sp
                            )
                        }


                        Spacer(
                            modifier =
                                Modifier.width(
                                    16.dp
                                )
                        )


                        Column(
                            modifier =
                                Modifier.weight(
                                    1f
                                )
                        ) {


                            Text(
                                text =
                                    atleta.nombre,

                                color =
                                    if (
                                        atleta.isMe
                                    ) {
                                        cyanAccent
                                    } else {
                                        Color.White
                                    },

                                fontWeight =
                                    FontWeight.Bold,

                                fontSize =
                                    18.sp
                            )


                            Text(
                                text =
                                    "Score: ${atleta.totalPuntos.toInt()}",

                                color =
                                    cyanAccent,

                                fontSize =
                                    14.sp,

                                fontWeight =
                                    FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }


        // =========================================================
        // PANTALLA 2 - RETOS
        // =========================================================

    } else if (
        vistaActual ==
        "rutina_competencia"
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        bgDark
                    )
                    .padding(
                        horizontal =
                            16.dp
                    )
        ) {


            Spacer(
                modifier =
                    Modifier.height(
                        24.dp
                    )
            )


            Row(
                verticalAlignment =
                    Alignment.CenterVertically,

                modifier =
                    Modifier.fillMaxWidth()
            ) {


                IconButton(
                    onClick = {

                        vistaActual =
                            "dashboard"
                    },

                    modifier =
                        Modifier.offset(
                            x =
                                (-8).dp
                        )
                ) {


                    Icon(
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
                    text =
                        "Retos del Grupo",

                    color =
                        Color.White,

                    fontSize =
                        24.sp,

                    fontWeight =
                        FontWeight.Bold
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        8.dp
                    )
            )


            Text(
                text =
                    "Cualquier integrante puede agregar ejercicios aquí para competir. Toca un reto para registrar TUS récords.",

                color =
                    Color.Gray,

                fontSize =
                    14.sp,

                lineHeight =
                    20.sp
            )


            Spacer(
                modifier =
                    Modifier.height(
                        24.dp
                    )
            )


            if (
                rutinaCompetencia
                    .isEmpty()
            ) {


                Box(
                    modifier =
                        Modifier
                            .weight(
                                1f
                            )
                            .fillMaxWidth(),

                    contentAlignment =
                        Alignment.Center
                ) {


                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {


                        Icon(
                            Icons.Default
                                .FitnessCenter,

                            contentDescription =
                                null,

                            tint =
                                Color.DarkGray,

                            modifier =
                                Modifier.size(
                                    72.dp
                                )
                        )


                        Spacer(
                            modifier =
                                Modifier.height(
                                    16.dp
                                )
                        )


                        Text(
                            text =
                                "Aún no hay retos activos.",

                            color =
                                Color.Gray,

                            fontSize =
                                16.sp
                        )
                    }
                }


            } else {


                LazyColumn(
                    modifier =
                        Modifier.weight(
                            1f
                        ),

                    contentPadding =
                        PaddingValues(
                            bottom =
                                24.dp
                        ),

                    verticalArrangement =
                        Arrangement
                            .spacedBy(
                                16.dp
                            )
                ) {


                    items(
                        rutinaCompetencia
                    ) {
                            ejercicioCompetencia ->


                        val ejApp =
                            ejercicioCompetencia
                                .ejercicioApp


                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .clip(
                                        RoundedCornerShape(
                                            20.dp
                                        )
                                    )
                                    .background(
                                        cardDark
                                    )
                                    .clickable {

                                        ejercicioActivo =
                                            ejercicioCompetencia


                                        inputPeso =
                                            if (
                                                ejercicioCompetencia
                                                    .miRecordPeso ==
                                                "0"
                                            ) {
                                                ""
                                            } else {
                                                ejercicioCompetencia
                                                    .miRecordPeso
                                            }


                                        inputReps =
                                            if (
                                                ejercicioCompetencia
                                                    .miRecordReps ==
                                                "0"
                                            ) {
                                                ""
                                            } else {
                                                ejercicioCompetencia
                                                    .miRecordReps
                                            }


                                        mostrarDialogoEdicion =
                                            true
                                    }
                                    .padding(
                                        16.dp
                                    ),

                            verticalAlignment =
                                Alignment
                                    .CenterVertically
                        ) {


                            Box(
                                modifier =
                                    Modifier
                                        .size(
                                            60.dp
                                        )
                                        .clip(
                                            RoundedCornerShape(
                                                12.dp
                                            )
                                        )
                                        .background(
                                            Color.White
                                        ),

                                contentAlignment =
                                    Alignment.Center
                            ) {


                                if (
                                    ejApp.urlImagen
                                        .isNotEmpty()
                                ) {


                                    AsyncImage(
                                        model =
                                            ejApp.urlImagen,

                                        contentDescription =
                                            null,

                                        modifier =
                                            Modifier
                                                .fillMaxSize()
                                                .padding(
                                                    6.dp
                                                ),

                                        contentScale =
                                            ContentScale.Fit
                                    )


                                } else {


                                    Icon(
                                        Icons.Default
                                            .FitnessCenter,

                                        contentDescription =
                                            null,

                                        tint =
                                            Color.Gray
                                    )
                                }
                            }


                            Spacer(
                                modifier =
                                    Modifier.width(
                                        16.dp
                                    )
                            )


                            Column(
                                modifier =
                                    Modifier.weight(
                                        1f
                                    )
                            ) {


                                Text(
                                    text =
                                        ejApp.nombreEs,

                                    color =
                                        Color.White,

                                    fontSize =
                                        16.sp,

                                    fontWeight =
                                        FontWeight.Bold,

                                    maxLines =
                                        1,

                                    overflow =
                                        TextOverflow
                                            .Ellipsis
                                )


                                Text(
                                    text =
                                        "Compite en: ${ejercicioCompetencia.idEje}",

                                    color =
                                        cyanAccent,

                                    fontSize =
                                        13.sp
                                )


                                Spacer(
                                    modifier =
                                        Modifier.height(
                                            8.dp
                                        )
                                )


                                Text(
                                    text =
                                        "Tu marca: ${ejercicioCompetencia.miRecordPeso}kg x ${ejercicioCompetencia.miRecordReps}",

                                    color =
                                        Color.Gray,

                                    fontSize =
                                        13.sp,

                                    fontWeight =
                                        FontWeight.Medium
                                )
                            }


                            IconButton(
                                onClick = {


                                    rutinaCompetencia
                                        .remove(
                                            ejercicioCompetencia
                                        )


                                    recalcularMisPuntos()


                                    Toast.makeText(
                                        context,
                                        "Reto eliminado",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },

                                modifier =
                                    Modifier.size(
                                        36.dp
                                    )
                            ) {


                                Icon(
                                    Icons.Default
                                        .DeleteOutline,

                                    contentDescription =
                                        "Quitar",

                                    tint =
                                        redAccent
                                )
                            }
                        }
                    }
                }
            }


            // =================================================
            // PROPONER RETO
            // =================================================

            Button(
                onClick = {

                    vistaActual =
                        "buscador_ranking"
                },

                colors =
                    ButtonDefaults
                        .buttonColors(
                            containerColor =
                                cardDark
                        ),

                shape =
                    RoundedCornerShape(
                        16.dp
                    ),

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(
                            60.dp
                        )
                        .padding(
                            bottom =
                                16.dp
                        )
                        .border(
                            width =
                                1.dp,
                            color =
                                cyanAccent,
                            shape =
                                RoundedCornerShape(
                                    16.dp
                                )
                        )
            ) {


                Icon(
                    Icons.Default.Add,

                    contentDescription =
                        null,

                    tint =
                        cyanAccent
                )


                Spacer(
                    modifier =
                        Modifier.width(
                            12.dp
                        )
                )


                Text(
                    text =
                        "PROPONER UN RETO",

                    color =
                        cyanAccent,

                    fontWeight =
                        FontWeight.Bold,

                    fontSize =
                        16.sp
                )
            }
        }


        // =========================================================
        // PANTALLA 3 - BUSCADOR
        // =========================================================

    } else if (
        vistaActual ==
        "buscador_ranking"
    ) {


        val listaFiltrada =
            remember(
                queryBusqueda,
                cachéEjerciciosGlobal
            ) {


                val queryLimpia =
                    quitarAcentosAmigos(
                        queryBusqueda
                            .lowercase()
                            .trim()
                    )


                var queryExpandida =
                    queryLimpia

                        .replace(
                            "remo en t",
                            "t-bar row"
                        )

                        .replace(
                            "press militar",
                            "mancuerna sentado hombro press"
                        )

                        .replace(
                            "sentadilla bulgara",
                            "bulgarian split squat"
                        )

                        .replace(
                            "peso muerto",
                            "deadlift"
                        )

                        .replace(
                            "press frances",
                            "skullcrusher"
                        )

                        .replace(
                            "press banca",
                            "bench press"
                        )

                        .replace(
                            "press de banca",
                            "bench press"
                        )


                val terminos =
                    queryExpandida
                        .split(" ")
                        .filter {

                            it.isNotBlank() &&
                                    it !in listOf(
                                "en",
                                "con",
                                "de",
                                "el",
                                "la",
                                "los",
                                "las",
                                "a",
                                "para",
                                "y"
                            )
                        }


                cachéEjerciciosGlobal
                    .values
                    .filter {
                            ejercicio ->


                        val esAbdomen =
                            ejercicio
                                .musculo
                                .contains(
                                    "Abdomen",
                                    true
                                ) ||
                                    ejercicio
                                        .musculo
                                        .contains(
                                            "Core",
                                            true
                                        )


                        if (
                            esAbdomen
                        ) {

                            return@filter false
                        }


                        val textoEj =
                            quitarAcentosAmigos(

                                "${ejercicio.nombreEs} ${ejercicio.idFirebase} ${ejercicio.musculo}"

                            ).lowercase()


                        val coincideTexto =
                            terminos.isEmpty() ||
                                    terminos.all {
                                            termino ->


                                        var matched =
                                            textoEj
                                                .contains(
                                                    termino
                                                )


                                        if (
                                            !matched
                                        ) {


                                            val sinonimos =
                                                when (
                                                    termino
                                                ) {

                                                    "lagartijas",
                                                    "flexiones" ->
                                                        listOf(
                                                            "push-up",
                                                            "push up"
                                                        )


                                                    "cristos",
                                                    "aperturas",
                                                    "mariposas" ->
                                                        listOf(
                                                            "fly",
                                                            "pec deck"
                                                        )


                                                    "vuelos" ->
                                                        listOf(
                                                            "lateral raise"
                                                        )


                                                    "gemelos",
                                                    "chamorros" ->
                                                        listOf(
                                                            "calf",
                                                            "pantorrilla"
                                                        )


                                                    "copa" ->
                                                        listOf(
                                                            "triceps extension"
                                                        )


                                                    "pajaros" ->
                                                        listOf(
                                                            "rear delt"
                                                        )


                                                    "dominadas",
                                                    "barras" ->
                                                        listOf(
                                                            "pull-up",
                                                            "chin up"
                                                        )


                                                    "fondos" ->
                                                        listOf(
                                                            "dips",
                                                            "dip"
                                                        )


                                                    "remo" ->
                                                        listOf(
                                                            "row"
                                                        )


                                                    "cuadriceps" ->
                                                        listOf(
                                                            "quad"
                                                        )


                                                    "pecho" ->
                                                        listOf(
                                                            "chest",
                                                            "pectoral"
                                                        )


                                                    "espalda" ->
                                                        listOf(
                                                            "back",
                                                            "lat"
                                                        )


                                                    "hombro" ->
                                                        listOf(
                                                            "shoulder",
                                                            "delt"
                                                        )


                                                    else ->
                                                        emptyList()
                                                }


                                            matched =
                                                sinonimos
                                                    .any {

                                                        textoEj
                                                            .contains(
                                                                it
                                                            )
                                                    }
                                        }


                                        matched
                                    }


                        coincideTexto
                    }
                    .sortedBy {

                        it.nombreEs
                    }
            }


        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        bgDark
                    )
                    .padding(
                        horizontal =
                            16.dp
                    )
        ) {


            Spacer(
                modifier =
                    Modifier.height(
                        24.dp
                    )
            )


            Row(
                verticalAlignment =
                    Alignment.CenterVertically,

                modifier =
                    Modifier.fillMaxWidth()
            ) {


                IconButton(
                    onClick = {

                        vistaActual =
                            "rutina_competencia"
                    },

                    modifier =
                        Modifier.offset(
                            x =
                                (-8).dp
                        )
                ) {


                    Icon(
                        Icons.AutoMirrored
                            .Filled
                            .ArrowBack,

                        contentDescription =
                            "Volver",

                        tint =
                            Color.White
                    )
                }


                OutlinedTextField(

                    value =
                        queryBusqueda,

                    onValueChange = {

                        queryBusqueda =
                            it
                    },

                    placeholder = {

                        Text(
                            text =
                                "Ej: Press banca, vuelos...",

                            color =
                                Color.Gray
                        )
                    },

                    leadingIcon = {

                        Icon(
                            Icons.Default
                                .Search,

                            contentDescription =
                                null,

                            tint =
                                Color.Gray
                        )
                    },

                    trailingIcon = {

                        if (
                            queryBusqueda
                                .isNotEmpty()
                        ) {

                            IconButton(
                                onClick = {

                                    queryBusqueda =
                                        ""
                                }
                            ) {

                                Icon(
                                    Icons.Default.Close,

                                    contentDescription =
                                        null,

                                    tint =
                                        Color.Gray
                                )
                            }
                        }
                    },

                    singleLine =
                        true,

                    keyboardOptions =
                        KeyboardOptions(
                            imeAction =
                                ImeAction.Search
                        ),

                    colors =
                        OutlinedTextFieldDefaults
                            .colors(

                                focusedTextColor =
                                    Color.White,

                                unfocusedTextColor =
                                    Color.White,

                                focusedContainerColor =
                                    cardDark,

                                unfocusedContainerColor =
                                    cardDark,

                                focusedBorderColor =
                                    Color.Transparent,

                                unfocusedBorderColor =
                                    Color.Transparent
                            ),

                    shape =
                        RoundedCornerShape(
                            16.dp
                        ),

                    modifier =
                        Modifier.fillMaxWidth()
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        24.dp
                    )
            )


            LazyColumn(
                modifier =
                    Modifier.fillMaxSize(),

                contentPadding =
                    PaddingValues(
                        bottom =
                            24.dp
                    ),

                verticalArrangement =
                    Arrangement
                        .spacedBy(
                            12.dp
                        )
            ) {


                items(
                    listaFiltrada
                ) {
                        ejercicio ->


                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .clip(
                                    RoundedCornerShape(
                                        16.dp
                                    )
                                )
                                .background(
                                    cardDark
                                )
                                .clickable {


                                    val ejeCorrespondiente =
                                        determinarEjeDeRadar(
                                            ejercicio
                                                .musculo
                                        )


                                    val nuevoEj =
                                        EjercicioCompetencia(

                                            ejercicioApp =
                                                ejercicio,

                                            idEje =
                                                ejeCorrespondiente
                                        )


                                    rutinaCompetencia
                                        .add(
                                            nuevoEj
                                        )


                                    Toast.makeText(
                                        context,
                                        "Reto añadido. ¡Registra tu récord!",
                                        Toast.LENGTH_SHORT
                                    ).show()


                                    queryBusqueda =
                                        ""


                                    vistaActual =
                                        "rutina_competencia"
                                }
                                .padding(
                                    16.dp
                                ),

                        verticalAlignment =
                            Alignment
                                .CenterVertically
                    ) {


                        Box(
                            modifier =
                                Modifier
                                    .size(
                                        56.dp
                                    )
                                    .clip(
                                        RoundedCornerShape(
                                            12.dp
                                        )
                                    )
                                    .background(
                                        Color.White
                                    ),

                            contentAlignment =
                                Alignment.Center
                        ) {


                            if (
                                ejercicio
                                    .urlImagen
                                    .isNotEmpty()
                            ) {


                                AsyncImage(
                                    model =
                                        ejercicio
                                            .urlImagen,

                                    contentDescription =
                                        null,

                                    modifier =
                                        Modifier
                                            .fillMaxSize()
                                            .padding(
                                                6.dp
                                            ),

                                    contentScale =
                                        ContentScale.Fit
                                )


                            } else {


                                Icon(
                                    Icons.Default
                                        .FitnessCenter,

                                    contentDescription =
                                        null,

                                    tint =
                                        Color.Gray
                                )
                            }
                        }


                        Spacer(
                            modifier =
                                Modifier.width(
                                    16.dp
                                )
                        )


                        Column(
                            modifier =
                                Modifier.weight(
                                    1f
                                )
                        ) {


                            Text(
                                text =
                                    ejercicio.nombreEs,

                                color =
                                    Color.White,

                                fontSize =
                                    16.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                maxLines =
                                    1,

                                overflow =
                                    TextOverflow
                                        .Ellipsis
                            )


                            Text(
                                text =
                                    ejercicio.musculo,

                                color =
                                    Color.Gray,

                                fontSize =
                                    14.sp
                            )
                        }


                        Icon(
                            Icons.Default.Add,

                            contentDescription =
                                "Añadir",

                            tint =
                                cyanAccent
                        )
                    }
                }
            }
        }
    }


    // =========================================================
    // DIÁLOGO REGISTRAR RÉCORD
    // =========================================================

    if (
        mostrarDialogoEdicion &&
        ejercicioActivo != null
    ) {


        val ejApp =
            ejercicioActivo!!
                .ejercicioApp


        AlertDialog(

            onDismissRequest = {

                mostrarDialogoEdicion =
                    false
            },

            containerColor =
                cardDark,

            title = {

                Text(
                    text =
                        "Registrar Mi Récord",

                    color =
                        Color.White,

                    fontWeight =
                        FontWeight.Bold,

                    fontSize =
                        20.sp
                )
            },

            text = {


                Column(
                    verticalArrangement =
                        Arrangement
                            .spacedBy(
                                20.dp
                            )
                ) {


                    Text(
                        text =
                            ejApp.nombreEs,

                        color =
                            cyanAccent,

                        fontWeight =
                            FontWeight.Bold,

                        fontSize =
                            16.sp
                    )


                    Row(
                        horizontalArrangement =
                            Arrangement
                                .spacedBy(
                                    16.dp
                                )
                    ) {


                        OutlinedTextField(

                            value =
                                inputPeso,

                            onValueChange = {

                                inputPeso =
                                    it
                            },

                            label = {

                                Text(
                                    text =
                                        "Peso (kg)",

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
                                Modifier.weight(
                                    1f
                                ),

                            singleLine =
                                true,

                            shape =
                                RoundedCornerShape(
                                    12.dp
                                )
                        )


                        OutlinedTextField(

                            value =
                                inputReps,

                            onValueChange = {

                                inputReps =
                                    it
                            },

                            label = {

                                Text(
                                    text =
                                        "Reps",

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
                                Modifier.weight(
                                    1f
                                ),

                            singleLine =
                                true,

                            shape =
                                RoundedCornerShape(
                                    12.dp
                                )
                        )
                    }
                }
            },

            confirmButton = {


                TextButton(
                    onClick = {


                        val indexRutina =
                            rutinaCompetencia
                                .indexOf(
                                    ejercicioActivo
                                )


                        if (
                            indexRutina != -1
                        ) {


                            rutinaCompetencia[
                                indexRutina
                            ] = ejercicioActivo!!
                                .copy(

                                    miRecordPeso =
                                        inputPeso,

                                    miRecordReps =
                                        inputReps
                                )
                        }


                        recalcularMisPuntos()


                        mostrarDialogoEdicion =
                            false


                        Toast.makeText(
                            context,
                            "Marca personal actualizada",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {


                    Text(
                        text =
                            "Guardar",

                        color =
                            cyanAccent,

                        fontWeight =
                            FontWeight.ExtraBold,

                        fontSize =
                            16.sp
                    )
                }
            },

            dismissButton = {


                TextButton(
                    onClick = {

                        mostrarDialogoEdicion =
                            false
                    }
                ) {


                    Text(
                        text =
                            "Cancelar",

                        color =
                            Color.Gray,

                        fontSize =
                            16.sp
                    )
                }
            }
        )
    }


    // =========================================================
    // DIÁLOGO INVITAR
    // =========================================================

    if (
        mostrarDialogoInvitacion
    ) {


        AlertDialog(

            onDismissRequest = {

                mostrarDialogoInvitacion =
                    false
            },

            containerColor =
                cardDark,

            title = {


                Text(
                    text =
                        "Invitar a un amigo",

                    color =
                        Color.White,

                    fontWeight =
                        FontWeight.Bold
                )
            },

            text = {


                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally,

                    modifier =
                        Modifier.fillMaxWidth()
                ) {


                    Text(
                        text =
                            "Comparte este código para que se unan a tu Círculo.",

                        color =
                            Color.Gray,

                        fontSize =
                            15.sp,

                        textAlign =
                            TextAlign.Center
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                20.dp
                            )
                    )


                    Box(
                        modifier =
                            Modifier
                                .clip(
                                    RoundedCornerShape(
                                        12.dp
                                    )
                                )
                                .background(
                                    bgDark
                                )
                                .padding(
                                    20.dp
                                )
                    ) {


                        // Temporal.
                        // Más adelante será generado por Firebase.

                        Text(
                            text =
                                "GZ8PT",

                            color =
                                cyanAccent,

                            fontSize =
                                32.sp,

                            fontWeight =
                                FontWeight.Black,

                            letterSpacing =
                                6.sp
                        )
                    }
                }
            },

            confirmButton = {


                TextButton(
                    onClick = {

                        mostrarDialogoInvitacion =
                            false
                    }
                ) {


                    Icon(
                        Icons.Default
                            .ContentCopy,

                        contentDescription =
                            null,

                        tint =
                            cyanAccent,

                        modifier =
                            Modifier.size(
                                18.dp
                            )
                    )


                    Spacer(
                        modifier =
                            Modifier.width(
                                6.dp
                            )
                    )


                    Text(
                        text =
                            "Copiar",

                        color =
                            cyanAccent,

                        fontWeight =
                            FontWeight.Bold,

                        fontSize =
                            16.sp
                    )
                }
            },

            dismissButton = {


                TextButton(
                    onClick = {

                        mostrarDialogoInvitacion =
                            false
                    }
                ) {


                    Text(
                        text =
                            "Cerrar",

                        color =
                            Color.Gray,

                        fontSize =
                            16.sp
                    )
                }
            }
        )
    }
}