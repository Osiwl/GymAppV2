package com.example.gymappv2.ui.screens

import android.widget.Toast

import androidx.activity.compose.BackHandler

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person

import androidx.compose.material3.*

import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import coil.ImageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

import org.json.JSONArray

import com.example.gymappv2.ui.amigos.PantallaAmigos

import com.example.gymappv2.ui.perfil.PerfilUsuario
import com.example.gymappv2.ui.perfil.PerfilUsuarioScreen

import com.example.gymappv2.ui.rutinas.*


// ==========================================================
// CACHE GLOBAL DE EJERCICIOS
// ==========================================================

var cachéEjerciciosGlobal by mutableStateOf<
        Map<String, EjercicioApp>
        >(
    emptyMap()
)

var datosYaCargados = false


fun buscarEjercicioEnCache(
    ejercicio: EjercicioApp
): EjercicioApp? {

    val idLimpio =
        ejercicio.idFirebase
            .lowercase()
            .trim()


    val porId =
        cachéEjerciciosGlobal[
            idLimpio
        ]


    if (
        porId != null
    ) {

        return porId
    }


    return cachéEjerciciosGlobal
        .values
        .find {

            it.nombreEs.equals(
                ejercicio.nombreEs,
                ignoreCase = true
            )
        }
}


// ==========================================================
// HOME
// ==========================================================

@Composable
fun HomeScreen(

    perfilUsuario: PerfilUsuario,

    onPerfilActualizado:
        (PerfilUsuario) -> Unit,

    onLogoutClick: () -> Unit
) {

    val context =
        LocalContext.current


    // ======================================================
    // PESTAÑA
    // ======================================================

    var selectedTab by remember {
        mutableStateOf(0)
    }


    // ======================================================
    // NAVEGACIÓN INTERNA DE RUTINAS
    // ======================================================

    var vistaActualRutina by remember {
        mutableStateOf("lista")
    }


    var vistaAnterior by remember {
        mutableStateOf("lista")
    }


    var ejercicioSeleccionado by remember {
        mutableStateOf<EjercicioApp?>(
            null
        )
    }


    var rutinaDestinoAgregar by remember {
        mutableStateOf<RutinaDia?>(
            null
        )
    }


    // ======================================================
    // ENTRENAMIENTO ACTIVO
    // ======================================================

    var rutinaEnVivo by remember {
        mutableStateOf<RutinaDia?>(
            null
        )
    }


    var entrenamientoEnCurso by remember {
        mutableStateOf(false)
    }


    var tiempoGlobal by remember {
        mutableIntStateOf(0)
    }


    var mostrarResumenFinal by remember {
        mutableStateOf(false)
    }


    // ======================================================
    // PERFIL COMPLETO
    // ======================================================

    val perfilTieneDatos =

        perfilUsuario
            .experiencia
            .isNotBlank() &&

                perfilUsuario
                    .objetivo
                    .isNotBlank() &&

                perfilUsuario
                    .diasEntrenamiento in 3..6


    // ======================================================
    // PLAN INICIAL SEGÚN PERFIL
    // ======================================================

    val planInicial =
        remember(

            perfilUsuario.experiencia,
            perfilUsuario.objetivo,
            perfilUsuario.diasEntrenamiento,
            perfilUsuario.rutinaAutomatica

        ) {

            when {


                // ============================================
                // RUTINA INTELIGENTE
                // ============================================

                perfilUsuario
                    .rutinaAutomatica &&
                        perfilTieneDatos -> {


                    calcularRutinaIdeal(
                        perfilUsuario
                    )
                }


                // ============================================
                // RUTINA MANUAL
                // ============================================

                !perfilUsuario
                    .rutinaAutomatica -> {

                    null
                }


                // ============================================
                // PERFIL INCOMPLETO
                // ============================================

                else -> {

                    null
                }
            }
        }


    // ======================================================
    // LISTA DE RUTINAS
    // ======================================================

    var misRutinasActivas by remember {

        mutableStateOf(

            planInicial
                ?.diasRutina
                ?: emptyList()
        )
    }


    // ======================================================
    // ESTADO FIREBASE
    // ======================================================

    var rutinasFirebaseCargadas by remember {
        mutableStateOf(false)
    }


    var cargandoRutinasFirebase by remember {
        mutableStateOf(true)
    }


    // ======================================================
    // FUNCIÓN ÚNICA PARA CAMBIAR RUTINAS
    // ======================================================

    fun actualizarRutinas(
        nuevasRutinas:
        List<RutinaDia>
    ) {

        // ----------------------------------------------
        // ACTUALIZAMOS UI
        // ----------------------------------------------

        misRutinasActivas =
            nuevasRutinas


        // ----------------------------------------------
        // FIREBASE
        // ----------------------------------------------

        if (
            rutinasFirebaseCargadas
        ) {

            RutinasFirebaseStorage
                .guardarRutinas(
                    nuevasRutinas
                ) {
                        guardado ->


                    if (
                        !guardado
                    ) {

                        Toast
                            .makeText(
                                context,
                                "No se pudieron sincronizar las rutinas",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
                }
        }
    }


    // ======================================================
    // CARGAR RUTINAS DESDE FIREBASE
    // ======================================================

    LaunchedEffect(Unit) {

        RutinasFirebaseStorage
            .cargarRutinas {
                    rutinasRemotas ->


                // ============================================
                // FIREBASE RESPONDIÓ CORRECTAMENTE
                // ============================================

                if (
                    rutinasRemotas != null
                ) {


                    // ----------------------------------------
                    // EXISTEN RUTINAS GUARDADAS
                    // ----------------------------------------

                    if (
                        rutinasRemotas
                            .isNotEmpty()
                    ) {

                        misRutinasActivas =
                            rutinasRemotas


                        // ----------------------------------------
                        // NO EXISTEN TODAVÍA
                        // ----------------------------------------

                    } else {

                        val rutinaNueva =

                            planInicial
                                ?.diasRutina
                                ?: emptyList()


                        misRutinasActivas =
                            rutinaNueva


                        /*
                         * Si es la rutina inteligente
                         * recién creada, la guardamos
                         * por primera vez.
                         */

                        if (
                            rutinaNueva
                                .isNotEmpty()
                        ) {

                            RutinasFirebaseStorage
                                .guardarRutinas(
                                    rutinaNueva
                                )
                        }
                    }


                    rutinasFirebaseCargadas =
                        true


                    // ============================================
                    // ERROR DE FIREBASE
                    // ============================================

                } else {

                    /*
                     * Conservamos las rutinas generadas
                     * localmente para que la app siga
                     * funcionando aunque haya un problema
                     * de conexión.
                     */

                    misRutinasActivas =

                        planInicial
                            ?.diasRutina
                            ?: emptyList()


                    rutinasFirebaseCargadas =
                        true
                }


                cargandoRutinasFirebase =
                    false
            }
    }


    // ======================================================
    // CARGAR EJERCICIOS DEL JSON
    // ======================================================

    LaunchedEffect(Unit) {

        if (
            !datosYaCargados
        ) {

            withContext(
                Dispatchers.IO
            ) {

                try {

                    val jsonString =

                        context
                            .assets
                            .open(
                                "ejercicios_espanol.json"
                            )
                            .bufferedReader()
                            .use {

                                it.readText()
                            }


                    val jsonArray =
                        JSONArray(
                            jsonString
                        )


                    val baseUrl =

                        "https://cdn.jsdelivr.net/gh/hasaneyldrm/exercises-dataset@main/"


                    val mapaTemporal =

                        mutableMapOf<
                                String,
                                EjercicioApp
                                >()


                    for (
                    i in 0 until
                            jsonArray.length()
                    ) {

                        val obj =

                            jsonArray
                                .getJSONObject(
                                    i
                                )


                        val name =

                            obj
                                .optString(
                                    "name",
                                    ""
                                )
                                .trim()


                        val jsonGif =

                            obj.optString(

                                "gifUrl",

                                obj.optString(
                                    "gif_url",
                                    ""
                                )
                            )


                        val jsonImg =

                            obj.optString(

                                "image",

                                obj.optString(

                                    "imageUrl",

                                    jsonGif
                                )
                            )


                        val imgFull =

                            if (
                                jsonImg
                                    .startsWith(
                                        "http"
                                    )
                            ) {

                                jsonImg
                                    .replace(
                                        " ",
                                        "%20"
                                    )

                            } else if (
                                jsonImg
                                    .isNotEmpty()
                            ) {

                                (
                                        baseUrl +
                                                jsonImg
                                        )
                                    .replace(
                                        " ",
                                        "%20"
                                    )

                            } else {

                                ""
                            }


                        val gifFull =

                            if (
                                jsonGif
                                    .startsWith(
                                        "http"
                                    )
                            ) {

                                jsonGif
                                    .replace(
                                        " ",
                                        "%20"
                                    )

                            } else if (
                                jsonGif
                                    .isNotEmpty()
                            ) {

                                (
                                        baseUrl +
                                                jsonGif
                                        )
                                    .replace(
                                        " ",
                                        "%20"
                                    )

                            } else {

                                ""
                            }


                        val musculoJSON =

                            obj
                                .optString(

                                    "target_es",

                                    obj.optString(
                                        "target",
                                        "Varios"
                                    )
                                )
                                .replaceFirstChar {

                                    it.uppercase()
                                }


                        mapaTemporal[
                            name.lowercase()
                        ] =

                            EjercicioApp(

                                idFirebase =
                                    name,

                                nombreEs =
                                    obj.optString(
                                        "name_es",
                                        name
                                    ),

                                urlImagen =
                                    imgFull,

                                urlGif =
                                    gifFull,

                                musculo =
                                    musculoJSON
                            )
                    }


                    withContext(
                        Dispatchers.Main
                    ) {

                        cachéEjerciciosGlobal =
                            mapaTemporal


                        datosYaCargados =
                            true
                    }


                } catch (
                    error: Exception
                ) {

                    error.printStackTrace()
                }
            }
        }
    }


    // ======================================================
    // PRECARGAR IMÁGENES DE RUTINAS
    // ======================================================

    LaunchedEffect(
        misRutinasActivas,
        datosYaCargados
    ) {

        if (
            datosYaCargados
        ) {

            val imageLoader =

                ImageLoader
                    .Builder(
                        context
                    )
                    .memoryCachePolicy(
                        CachePolicy.ENABLED
                    )
                    .diskCachePolicy(
                        CachePolicy.ENABLED
                    )
                    .build()


            misRutinasActivas
                .flatMap {

                    it.ejercicios
                }
                .forEach {
                        ejercicio ->


                    val datos =

                        buscarEjercicioEnCache(
                            ejercicio
                        )


                    datos
                        ?.urlImagen
                        ?.takeIf {

                            it.isNotEmpty()
                        }
                        ?.let {
                                url ->


                            imageLoader.enqueue(

                                ImageRequest
                                    .Builder(
                                        context
                                    )
                                    .data(
                                        url
                                    )
                                    .build()
                            )
                        }
                }
        }
    }


    // ======================================================
    // CRONÓMETRO
    // ======================================================

    LaunchedEffect(
        entrenamientoEnCurso
    ) {

        while (
            entrenamientoEnCurso
        ) {

            delay(
                1000L
            )

            tiempoGlobal++
        }
    }


    // ======================================================
    // BOTÓN ATRÁS
    // ======================================================

    BackHandler(

        enabled =

            selectedTab != 0 ||

                    vistaActualRutina !=
                    "lista"

    ) {

        if (
            selectedTab != 0
        ) {

            selectedTab =
                0


            vistaActualRutina =

                if (
                    entrenamientoEnCurso
                ) {

                    "entrenamiento_dashboard"

                } else {

                    "lista"
                }


        } else {

            when (
                vistaActualRutina
            ) {


                "editar" -> {

                    vistaActualRutina =
                        vistaAnterior
                }


                "buscador" -> {

                    rutinaDestinoAgregar =
                        null


                    vistaActualRutina =

                        if (
                            entrenamientoEnCurso
                        ) {

                            "entrenamiento_dashboard"

                        } else {

                            "lista"
                        }
                }


                "entrenamiento_dashboard" -> {

                    Toast
                        .makeText(

                            context,

                            "Usa el botón FINALIZAR para salir",

                            Toast.LENGTH_SHORT
                        )
                        .show()
                }
            }
        }
    }


    // ======================================================
    // COLORES
    // ======================================================

    val bgDark =
        Color(
            0xFF07090F
        )


    val accentCyan =
        Color(
            0xFF4AC4CF
        )


    // ======================================================
    // SCAFFOLD
    // ======================================================

    Scaffold(

        bottomBar = {

            Column {


                // ==================================================
                // ENTRENAMIENTO ACTIVO
                // ==================================================

                if (
                    entrenamientoEnCurso &&

                    (
                            selectedTab != 0 ||

                                    vistaActualRutina !=
                                    "entrenamiento_dashboard"
                            )
                ) {

                    val mins =
                        tiempoGlobal / 60


                    val segs =
                        tiempoGlobal % 60


                    Row(

                        modifier =

                            Modifier
                                .fillMaxWidth()
                                .background(
                                    Color(
                                        0xFFEF5350
                                    )
                                )
                                .clickable {

                                    selectedTab =
                                        0


                                    vistaActualRutina =
                                        "entrenamiento_dashboard"
                                }
                                .padding(
                                    16.dp
                                ),

                        horizontalArrangement =
                            Arrangement
                                .SpaceBetween,

                        verticalAlignment =
                            Alignment
                                .CenterVertically
                    ) {


                        Row(

                            verticalAlignment =
                                Alignment
                                    .CenterVertically
                        ) {

                            Icon(

                                Icons.Default
                                    .FitnessCenter,

                                contentDescription =
                                    null,

                                tint =
                                    Color.White
                            )


                            Spacer(

                                modifier =
                                    Modifier.width(
                                        8.dp
                                    )
                            )


                            Text(

                                text =
                                    "Entrenamiento en curso",

                                color =
                                    Color.White,

                                fontWeight =
                                    FontWeight.Bold
                            )
                        }


                        Text(

                            text =

                                String.format(

                                    "%02d:%02d",

                                    mins,

                                    segs
                                ),

                            color =
                                Color.White,

                            fontWeight =
                                FontWeight.Black
                        )
                    }
                }


                // ==================================================
                // NAVEGACIÓN INFERIOR
                // ==================================================

                NavigationBar(

                    containerColor =
                        Color(
                            0xFF101726
                        ),

                    contentColor =
                        Color.White
                ) {


                    // ==============================================
                    // RUTINA
                    // ==============================================

                    NavigationBarItem(

                        icon = {

                            Icon(

                                Icons.Default
                                    .FitnessCenter,

                                contentDescription =
                                    "Rutina"
                            )
                        },

                        label = {

                            Text(
                                "Rutina"
                            )
                        },

                        selected =
                            selectedTab == 0,

                        onClick = {

                            selectedTab =
                                0


                            vistaActualRutina =

                                if (
                                    entrenamientoEnCurso
                                ) {

                                    "entrenamiento_dashboard"

                                } else {

                                    "lista"
                                }
                        },

                        colors =

                            NavigationBarItemDefaults
                                .colors(

                                    selectedIconColor =
                                        accentCyan,

                                    selectedTextColor =
                                        accentCyan,

                                    unselectedIconColor =
                                        Color.Gray,

                                    unselectedTextColor =
                                        Color.Gray,

                                    indicatorColor =
                                        accentCyan.copy(
                                            alpha =
                                                0.2f
                                        )
                                )
                    )


                    // ==============================================
                    // AMIGOS
                    // ==============================================

                    NavigationBarItem(

                        icon = {

                            Icon(

                                Icons.Default.Group,

                                contentDescription =
                                    "Amigos"
                            )
                        },

                        label = {

                            Text(
                                "Amigos"
                            )
                        },

                        selected =
                            selectedTab == 1,

                        onClick = {

                            selectedTab =
                                1
                        },

                        colors =

                            NavigationBarItemDefaults
                                .colors(

                                    selectedIconColor =
                                        accentCyan,

                                    selectedTextColor =
                                        accentCyan,

                                    unselectedIconColor =
                                        Color.Gray,

                                    unselectedTextColor =
                                        Color.Gray,

                                    indicatorColor =
                                        accentCyan.copy(
                                            alpha =
                                                0.2f
                                        )
                                )
                    )


                    // ==============================================
                    // PERFIL
                    // ==============================================

                    NavigationBarItem(

                        icon = {

                            Icon(

                                Icons.Default.Person,

                                contentDescription =
                                    "Perfil"
                            )
                        },

                        label = {

                            Text(
                                "Perfil"
                            )
                        },

                        selected =
                            selectedTab == 2,

                        onClick = {

                            selectedTab =
                                2
                        },

                        colors =

                            NavigationBarItemDefaults
                                .colors(

                                    selectedIconColor =
                                        accentCyan,

                                    selectedTextColor =
                                        accentCyan,

                                    unselectedIconColor =
                                        Color.Gray,

                                    unselectedTextColor =
                                        Color.Gray,

                                    indicatorColor =
                                        accentCyan.copy(
                                            alpha =
                                                0.2f
                                        )
                                )
                    )
                }
            }
        }

    ) {
            innerPadding ->


        Box(

            modifier =

                Modifier
                    .fillMaxSize()
                    .background(
                        bgDark
                    )
                    .padding(
                        innerPadding
                    )
        ) {


            // ==================================================
            // CARGANDO FIREBASE
            // ==================================================

            if (
                selectedTab == 0 &&
                vistaActualRutina ==
                "lista" &&
                cargandoRutinasFirebase
            ) {

                Box(

                    modifier =
                        Modifier
                            .fillMaxSize(),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Column(

                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        CircularProgressIndicator(

                            color =
                                accentCyan
                        )


                        Spacer(

                            modifier =
                                Modifier.height(
                                    16.dp
                                )
                        )


                        Text(

                            text =
                                "Cargando tus rutinas...",

                            color =
                                Color.Gray
                        )
                    }
                }


            } else {


                when (
                    selectedTab
                ) {


                    // ==================================================
                    // RUTINAS
                    // ==================================================

                    0 -> {

                        when (
                            vistaActualRutina
                        ) {


                            // ==========================================
                            // LISTA
                            // ==========================================

                            "lista" -> {

                                PantallaRutinas(

                                    listaDias =
                                        misRutinasActivas,


                                    // ==================================
                                    // AJUSTES / LOGOUT
                                    // ==================================

                                    onAjustesClick = {

                                        onLogoutClick()
                                    },


                                    // ==================================
                                    // INICIAR
                                    // ==================================

                                    onIniciarClick = {
                                            rutinaDia ->


                                        rutinaEnVivo =
                                            rutinaDia


                                        tiempoGlobal =
                                            0


                                        entrenamientoEnCurso =
                                            true


                                        vistaActualRutina =
                                            "entrenamiento_dashboard"
                                    },


                                    // ==================================
                                    // EJERCICIO
                                    // ==================================

                                    onEjercicioClick = {
                                            ejercicio ->


                                        ejercicioSeleccionado =
                                            ejercicio


                                        vistaAnterior =
                                            "lista"


                                        vistaActualRutina =
                                            "editar"
                                    },


                                    // ==================================
                                    // ELIMINAR RUTINA
                                    // ==================================

                                    onEliminarClick = {
                                            rutinaBorrar ->


                                        val nuevaLista =

                                            misRutinasActivas
                                                .filter {

                                                    it !=
                                                            rutinaBorrar
                                                }


                                        actualizarRutinas(
                                            nuevaLista
                                        )
                                    },


                                    // ==================================
                                    // CREAR RUTINA
                                    // ==================================

                                    onAgregarRutinaClick = {

                                        rutinaDestinoAgregar =
                                            null


                                        vistaActualRutina =
                                            "buscador"
                                    },


                                    // ==================================
                                    // RENOMBRAR
                                    // ==================================

                                    onRenombrarClick = {
                                            rutinaAnterior,
                                            nuevoNombre ->


                                        val nuevaLista =

                                            misRutinasActivas
                                                .map {

                                                    if (
                                                        it ==
                                                        rutinaAnterior
                                                    ) {

                                                        it.copy(

                                                            titulo =
                                                                nuevoNombre
                                                        )

                                                    } else {

                                                        it
                                                    }
                                                }


                                        actualizarRutinas(
                                            nuevaLista
                                        )
                                    },


                                    // ==================================
                                    // AGREGAR EJERCICIO
                                    // ==================================

                                    onAgregarEjercicioClick = {
                                            rutina ->


                                        rutinaDestinoAgregar =
                                            rutina


                                        vistaActualRutina =
                                            "buscador"
                                    },


                                    // ==================================
                                    // QUITAR EJERCICIO
                                    // ==================================

                                    onQuitarEjercicioClick = {
                                            rutina,
                                            ejercicioBorrar ->


                                        val nuevaLista =

                                            misRutinasActivas
                                                .map {


                                                    if (
                                                        it ==
                                                        rutina
                                                    ) {


                                                        it.copy(

                                                            ejercicios =

                                                                it
                                                                    .ejercicios
                                                                    .filter {
                                                                            ejercicio ->


                                                                        ejercicio !=
                                                                                ejercicioBorrar
                                                                    }
                                                        )


                                                    } else {


                                                        it
                                                    }
                                                }


                                        actualizarRutinas(
                                            nuevaLista
                                        )
                                    }
                                )
                            }


                            // ==========================================
                            // ENTRENAMIENTO
                            // ==========================================

                            "entrenamiento_dashboard" -> {


                                rutinaEnVivo
                                    ?.let {
                                            rutina ->


                                        EntrenamientoActivoScreen(

                                            rutina =
                                                rutina,

                                            tiempoSegundos =
                                                tiempoGlobal,

                                            onEjercicioClick = {
                                                    ejercicio ->


                                                ejercicioSeleccionado =
                                                    ejercicio


                                                vistaAnterior =
                                                    "entrenamiento_dashboard"


                                                vistaActualRutina =
                                                    "editar"
                                            },

                                            onFinalizarClick = {

                                                entrenamientoEnCurso =
                                                    false


                                                mostrarResumenFinal =
                                                    true


                                                vistaActualRutina =
                                                    "lista"
                                            }
                                        )
                                    }
                            }


                            // ==========================================
                            // BUSCADOR
                            // ==========================================

                            "buscador" -> {

                                BuscadorEjerciciosScreen(

                                    onBackClick = {

                                        rutinaDestinoAgregar =
                                            null


                                        vistaActualRutina =

                                            if (
                                                entrenamientoEnCurso
                                            ) {

                                                "entrenamiento_dashboard"

                                            } else {

                                                "lista"
                                            }
                                    },


                                    onEjercicioSeleccionado = {
                                            ejercicio ->


                                        // ==================================
                                        // AGREGAR A RUTINA EXISTENTE
                                        // ==================================

                                        if (
                                            rutinaDestinoAgregar !=
                                            null
                                        ) {

                                            val rutinaDestino =
                                                rutinaDestinoAgregar


                                            val nuevaLista =

                                                misRutinasActivas
                                                    .map {

                                                        if (
                                                            it ==
                                                            rutinaDestino
                                                        ) {

                                                            it.copy(

                                                                ejercicios =

                                                                    it.ejercicios +
                                                                            ejercicio
                                                            )

                                                        } else {

                                                            it
                                                        }
                                                    }


                                            actualizarRutinas(
                                                nuevaLista
                                            )


                                            Toast
                                                .makeText(

                                                    context,

                                                    "Agregado a ${rutinaDestino!!.titulo}",

                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()


                                            rutinaDestinoAgregar =
                                                null


                                            vistaActualRutina =
                                                "lista"


                                            // ==================================
                                            // EJERCICIO NUEVO
                                            // ==================================

                                        } else {

                                            ejercicioSeleccionado =
                                                ejercicio


                                            vistaAnterior =
                                                "buscador"


                                            vistaActualRutina =
                                                "editar"
                                        }
                                    }
                                )
                            }


                            // ==========================================
                            // EDITAR
                            // ==========================================

                            "editar" -> {


                                ejercicioSeleccionado
                                    ?.let {
                                            ejercicio ->


                                        EditarEjercicioScreen(

                                            ejercicio =
                                                ejercicio,


                                            esDesdeBuscador =

                                                (
                                                        vistaAnterior ==
                                                                "buscador" &&

                                                                rutinaDestinoAgregar ==
                                                                null
                                                        ),


                                            nombresRutinas =

                                                misRutinasActivas
                                                    .map {

                                                        it.titulo
                                                    },


                                            onAgregarARutina = {
                                                    nombreRutina ->


                                                val existe =

                                                    misRutinasActivas
                                                        .any {

                                                            it.titulo ==
                                                                    nombreRutina
                                                        }


                                                val nuevaLista =

                                                    if (
                                                        existe
                                                    ) {

                                                        misRutinasActivas
                                                            .map {

                                                                if (
                                                                    it.titulo ==
                                                                    nombreRutina
                                                                ) {

                                                                    it.copy(

                                                                        ejercicios =

                                                                            it.ejercicios +
                                                                                    ejercicio
                                                                    )

                                                                } else {

                                                                    it
                                                                }
                                                            }


                                                    } else {


                                                        misRutinasActivas +

                                                                RutinaDia(

                                                                    titulo =
                                                                        nombreRutina,

                                                                    ejercicios =
                                                                        listOf(
                                                                            ejercicio
                                                                        )
                                                                )
                                                    }


                                                actualizarRutinas(
                                                    nuevaLista
                                                )


                                                Toast
                                                    .makeText(

                                                        context,

                                                        "Agregado a $nombreRutina",

                                                        Toast.LENGTH_SHORT
                                                    )
                                                    .show()


                                                vistaActualRutina =
                                                    "lista"
                                            },


                                            onBackClick = {

                                                vistaActualRutina =
                                                    vistaAnterior
                                            }
                                        )
                                    }
                            }
                        }
                    }


                    // ==================================================
                    // AMIGOS
                    // ==================================================

                    1 -> {

                        PantallaAmigos(

                            perfilUsuario =
                                perfilUsuario
                        )
                    }


                    // ==================================================
                    // PERFIL
                    // ==================================================

                    2 -> {

                        PerfilUsuarioScreen(

                            perfilUsuario =
                                perfilUsuario,


                            onBackClick = {

                                selectedTab =
                                    0
                            },


                            onGuardarPerfil = {
                                    nuevoPerfil ->


                                onPerfilActualizado(
                                    nuevoPerfil
                                )
                            }
                        )
                    }
                }
            }


            // ======================================================
            // RESUMEN FINAL
            // ======================================================

            if (
                mostrarResumenFinal &&
                rutinaEnVivo != null
            ) {

                val minsTotales =
                    tiempoGlobal / 60


                AlertDialog(

                    onDismissRequest = {

                        mostrarResumenFinal =
                            false


                        rutinaEnVivo =
                            null


                        tiempoGlobal =
                            0
                    },

                    containerColor =
                        Color(
                            0xFF141A29
                        ),

                    title = {

                        Text(

                            text =
                                "¡Entrenamiento Completado! 🏆",

                            color =
                                Color.White,

                            fontWeight =
                                FontWeight.Bold
                        )
                    },

                    text = {

                        Column {

                            Text(

                                text =
                                    rutinaEnVivo!!
                                        .titulo,

                                color =
                                    accentCyan,

                                fontSize =
                                    20.sp,

                                fontWeight =
                                    FontWeight.ExtraBold
                            )


                            Spacer(

                                modifier =
                                    Modifier.height(
                                        16.dp
                                    )
                            )


                            Text(

                                text =
                                    "⏱ Tiempo total: $minsTotales minutos",

                                color =
                                    Color.White,

                                fontSize =
                                    16.sp
                            )


                            Text(

                                text =
                                    "💪 Ejercicios: ${rutinaEnVivo!!.ejercicios.size}",

                                color =
                                    Color.White,

                                fontSize =
                                    16.sp
                            )
                        }
                    },

                    confirmButton = {

                        Button(

                            onClick = {

                                mostrarResumenFinal =
                                    false


                                rutinaEnVivo =
                                    null


                                tiempoGlobal =
                                    0
                            },

                            colors =

                                ButtonDefaults
                                    .buttonColors(

                                        containerColor =
                                            accentCyan
                                    )
                        ) {

                            Text(

                                text =
                                    "Genial",

                                color =
                                    Color.White
                            )
                        }
                    }
                )
            }
        }
    }
}


// ==========================================================
// PLACEHOLDER
// ==========================================================

@Composable
fun PantallaPlaceholder(
    mensaje: String
) {

    Box(

        modifier =
            Modifier.fillMaxSize(),

        contentAlignment =
            Alignment.Center
    ) {

        Text(

            text =
                mensaje,

            color =
                Color.Gray,

            fontSize =
                16.sp
        )
    }
}