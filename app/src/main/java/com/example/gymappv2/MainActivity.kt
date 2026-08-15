package com.example.gymappv2

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import com.example.gymappv2.ui.perfil.PerfilFirebaseStorage
import com.example.gymappv2.ui.perfil.PerfilLocalStorage
import com.example.gymappv2.ui.perfil.PerfilUsuario

import com.example.gymappv2.ui.screens.*

import com.google.firebase.auth.FirebaseAuth


class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContent {

            MaterialTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF07090F)
                ) {

                    // =================================================
                    // CONTEXTO
                    // =================================================

                    val appContext =
                        applicationContext


                    // =================================================
                    // PERFIL LOCAL
                    // =================================================

                    var perfilUsuario by remember {

                        mutableStateOf(

                            PerfilLocalStorage.cargar(
                                appContext
                            )
                        )
                    }


                    // =================================================
                    // ACTUALIZAR PERFIL
                    // =================================================

                    fun actualizarPerfil(
                        nuevoPerfil: PerfilUsuario
                    ) {

                        // 1. Memoria de la app
                        perfilUsuario =
                            nuevoPerfil


                        // 2. Guardado local
                        PerfilLocalStorage.guardar(
                            appContext,
                            nuevoPerfil
                        )


                        // 3. Guardado en Firebase
                        // solo si ya existe una sesión
                        if (
                            FirebaseAuth
                                .getInstance()
                                .currentUser != null
                        ) {

                            PerfilFirebaseStorage.guardar(
                                nuevoPerfil
                            )
                        }
                    }


                    // =================================================
                    // APLICAR PERFIL DESCARGADO DE FIREBASE
                    // =================================================

                    fun aplicarPerfilFirebase(
                        perfilRemoto: PerfilUsuario
                    ) {

                        // Actualizamos Compose
                        perfilUsuario =
                            perfilRemoto


                        // También hacemos una copia local
                        // para poder abrir rápido la próxima vez.
                        PerfilLocalStorage.guardar(
                            appContext,
                            perfilRemoto
                        )
                    }


                    // =================================================
                    // NAVEGACIÓN
                    // =================================================

                    val backStack =
                        remember {

                            mutableStateListOf(
                                "splash_screen"
                            )
                        }


                    val currentScreen =
                        backStack.lastOrNull()
                            ?: "splash_screen"


                    fun navigateTo(
                        screen: String
                    ) {

                        if (
                            backStack.lastOrNull() !=
                            screen
                        ) {

                            backStack.add(
                                screen
                            )
                        }
                    }


                    fun navigateAndClear(
                        screen: String
                    ) {

                        backStack.clear()

                        backStack.add(
                            screen
                        )
                    }


                    fun goBack() {

                        if (
                            backStack.size > 1
                        ) {

                            backStack.removeAt(
                                backStack.lastIndex
                            )
                        }
                    }


                    BackHandler(
                        enabled =
                            backStack.size > 1
                    ) {

                        goBack()
                    }


                    // =================================================
                    // PANTALLAS
                    // =================================================

                    Crossfade(
                        targetState =
                            currentScreen,
                        label =
                            "navegacion_principal"
                    ) {
                            screen ->


                        when (screen) {


                            // =================================================
                            // SPLASH
                            // =================================================

                            "splash_screen" -> {

                                SplashScreen(

                                    onNavigateNext = {

                                        val usuarioFirebase =
                                            FirebaseAuth
                                                .getInstance()
                                                .currentUser


                                        // =====================================
                                        // HAY UNA SESIÓN INICIADA
                                        // =====================================

                                        if (
                                            usuarioFirebase != null
                                        ) {

                                            /*
                                             * Primero intentamos recuperar
                                             * el perfil desde Firestore.
                                             */

                                            PerfilFirebaseStorage.cargar {
                                                    perfilRemoto ->


                                                // -----------------------------
                                                // PERFIL ENCONTRADO EN FIREBASE
                                                // -----------------------------

                                                if (
                                                    perfilRemoto != null &&
                                                    perfilRemoto
                                                        .onboardingCompletado
                                                ) {

                                                    aplicarPerfilFirebase(
                                                        perfilRemoto
                                                    )


                                                    navigateAndClear(
                                                        "home_dashboard"
                                                    )


                                                    // -----------------------------
                                                    // NO SE PUDO OBTENER FIREBASE
                                                    // PERO HAY PERFIL LOCAL
                                                    // -----------------------------

                                                } else if (
                                                    perfilUsuario
                                                        .onboardingCompletado
                                                ) {

                                                    navigateAndClear(
                                                        "home_dashboard"
                                                    )


                                                    // -----------------------------
                                                    // NO HAY PERFIL COMPLETO
                                                    // -----------------------------

                                                } else {

                                                    navigateAndClear(
                                                        "welcome"
                                                    )
                                                }
                                            }


                                            // =====================================
                                            // NO HAY SESIÓN
                                            // =====================================

                                        } else {

                                            navigateAndClear(
                                                "welcome"
                                            )
                                        }
                                    }
                                )
                            }


                            // =================================================
                            // BIENVENIDA
                            // =================================================

                            "welcome" -> {

                                WelcomeScreen(

                                    // =========================================
                                    // NUEVO USUARIO
                                    // =========================================

                                    onStartClick = {

                                        /*
                                         * Cerramos cualquier sesión anterior
                                         * antes de comenzar un onboarding nuevo.
                                         */

                                        FirebaseAuth
                                            .getInstance()
                                            .signOut()


                                        val nuevoPerfil =
                                            PerfilUsuario()


                                        perfilUsuario =
                                            nuevoPerfil


                                        PerfilLocalStorage.guardar(
                                            appContext,
                                            nuevoPerfil
                                        )


                                        navigateTo(
                                            "name_input"
                                        )
                                    },


                                    // =========================================
                                    // USUARIO EXISTENTE CON GOOGLE
                                    // =========================================

                                    onLoginSuccess = {

                                        /*
                                         * Google ya inició sesión.
                                         *
                                         * Ahora buscamos el perfil
                                         * correspondiente al UID.
                                         */

                                        PerfilFirebaseStorage.cargar {
                                                perfilRemoto ->


                                            // ---------------------------------
                                            // EXISTE PERFIL EN FIREBASE
                                            // ---------------------------------

                                            if (
                                                perfilRemoto != null &&
                                                perfilRemoto
                                                    .onboardingCompletado
                                            ) {

                                                aplicarPerfilFirebase(
                                                    perfilRemoto
                                                )


                                                navigateAndClear(
                                                    "home_dashboard"
                                                )


                                                // ---------------------------------
                                                // NO EXISTE PERFIL EN FIREBASE
                                                // ---------------------------------

                                            } else {

                                                /*
                                                 * Significa que probablemente
                                                 * es una cuenta que todavía
                                                 * no completó el onboarding.
                                                 */

                                                val nuevoPerfil =
                                                    PerfilUsuario()


                                                perfilUsuario =
                                                    nuevoPerfil


                                                PerfilLocalStorage.guardar(
                                                    appContext,
                                                    nuevoPerfil
                                                )


                                                navigateAndClear(
                                                    "name_input"
                                                )
                                            }
                                        }
                                    }
                                )
                            }


                            // =================================================
                            // NOMBRE
                            // =================================================

                            "name_input" -> {

                                NameScreen(

                                    onBackClick = {

                                        goBack()
                                    },

                                    onNextClick = {
                                            nombre ->


                                        actualizarPerfil(

                                            perfilUsuario.copy(

                                                nombre =
                                                    nombre
                                            )
                                        )


                                        navigateTo(
                                            "experience_input"
                                        )
                                    }
                                )
                            }


                            // =================================================
                            // EXPERIENCIA
                            // =================================================

                            "experience_input" -> {

                                ExperienceScreen(

                                    onBackClick = {

                                        goBack()
                                    },

                                    onNextClick = {
                                            experiencia ->


                                        actualizarPerfil(

                                            perfilUsuario.copy(

                                                experiencia =
                                                    experiencia
                                            )
                                        )


                                        if (
                                            experiencia.equals(
                                                "Principiante",
                                                ignoreCase = true
                                            )
                                        ) {

                                            actualizarPerfil(

                                                perfilUsuario.copy(

                                                    experiencia =
                                                        experiencia,

                                                    rutinaAutomatica =
                                                        true
                                                )
                                            )


                                            navigateTo(
                                                "goal_input"
                                            )

                                        } else {

                                            navigateTo(
                                                "custom_routine_prompt"
                                            )
                                        }
                                    }
                                )
                            }


                            // =================================================
                            // TIPO DE RUTINA
                            // =================================================

                            "custom_routine_prompt" -> {

                                CustomRoutinePromptScreen(

                                    onBackClick = {

                                        goBack()
                                    },


                                    onYesClick = {

                                        actualizarPerfil(

                                            perfilUsuario.copy(

                                                rutinaAutomatica =
                                                    true
                                            )
                                        )


                                        navigateTo(
                                            "goal_input"
                                        )
                                    },


                                    onNoClick = {

                                        actualizarPerfil(

                                            perfilUsuario.copy(

                                                rutinaAutomatica =
                                                    false
                                            )
                                        )


                                        navigateTo(
                                            "gender_input"
                                        )
                                    }
                                )
                            }


                            // =================================================
                            // OBJETIVO
                            // =================================================

                            "goal_input" -> {

                                GoalScreen(

                                    onBackClick = {

                                        goBack()
                                    },

                                    onNextClick = {
                                            objetivo ->


                                        actualizarPerfil(

                                            perfilUsuario.copy(

                                                objetivo =
                                                    objetivo
                                            )
                                        )


                                        navigateTo(
                                            "days_input"
                                        )
                                    }
                                )
                            }


                            // =================================================
                            // DÍAS DE ENTRENAMIENTO
                            // =================================================

                            "days_input" -> {

                                DaysScreen(

                                    onBackClick = {

                                        goBack()
                                    },

                                    onNextClick = {
                                            dias ->


                                        actualizarPerfil(

                                            perfilUsuario.copy(

                                                diasEntrenamiento =
                                                    dias
                                            )
                                        )


                                        navigateTo(
                                            "gender_input"
                                        )
                                    }
                                )
                            }


                            // =================================================
                            // GÉNERO
                            // =================================================

                            "gender_input" -> {

                                GenderScreen(

                                    onBackClick = {

                                        goBack()
                                    },

                                    onNextClick = {
                                            genero ->


                                        actualizarPerfil(

                                            perfilUsuario.copy(

                                                genero =
                                                    genero
                                            )
                                        )


                                        navigateTo(
                                            "weight_input"
                                        )
                                    }
                                )
                            }


                            // =================================================
                            // PESO
                            // =================================================

                            "weight_input" -> {

                                WeightScreen(

                                    onBackClick = {

                                        goBack()
                                    },

                                    onNextClick = {
                                            peso ->


                                        actualizarPerfil(

                                            perfilUsuario.copy(

                                                pesoKg =
                                                    peso
                                            )
                                        )


                                        navigateTo(
                                            "height_input"
                                        )
                                    }
                                )
                            }


                            // =================================================
                            // ALTURA
                            // =================================================

                            "height_input" -> {

                                HeightScreen(

                                    onBackClick = {

                                        goBack()
                                    },

                                    onNextClick = {
                                            altura ->


                                        actualizarPerfil(

                                            perfilUsuario.copy(

                                                alturaCm =
                                                    altura
                                            )
                                        )


                                        navigateTo(
                                            "age_input"
                                        )
                                    }
                                )
                            }


                            // =================================================
                            // EDAD
                            // =================================================

                            "age_input" -> {

                                AgeScreen(

                                    onBackClick = {

                                        goBack()
                                    },

                                    onNextClick = {
                                            edad ->


                                        actualizarPerfil(

                                            perfilUsuario.copy(

                                                edad =
                                                    edad,

                                                onboardingCompletado =
                                                    true
                                            )
                                        )


                                        navigateTo(
                                            "signup_screen"
                                        )
                                    }
                                )
                            }


                            // =================================================
                            // REGISTRO
                            // =================================================

                            "signup_screen" -> {

                                SignupScreen(

                                    onBackClick = {

                                        goBack()
                                    },


                                    // =========================================
                                    // GOOGLE
                                    // =========================================

                                    onGoogleClick = {

                                        /*
                                         * SignupScreen ya autenticó
                                         * al usuario antes de llegar aquí.
                                         */


                                        PerfilLocalStorage.guardar(
                                            appContext,
                                            perfilUsuario
                                        )


                                        PerfilFirebaseStorage.guardar(
                                            perfilUsuario
                                        ) {
                                                guardado ->


                                            if (guardado) {

                                                println(
                                                    "PERFIL_FIREBASE: Perfil guardado correctamente"
                                                )

                                            } else {

                                                println(
                                                    "PERFIL_FIREBASE: Error al guardar perfil"
                                                )
                                            }
                                        }


                                        navigateTo(
                                            "calculating_plan"
                                        )
                                    },


                                    // =========================================
                                    // EMAIL
                                    // =========================================

                                    onEmailClick = {

                                        navigateTo(
                                            "email_login"
                                        )
                                    }
                                )
                            }


                            // =================================================
                            // LOGIN EMAIL
                            // =================================================

                            "email_login" -> {

                                EmailLoginScreen(

                                    onBackClick = {

                                        goBack()
                                    },

                                    onLoginSuccess = {

                                        /*
                                         * Tu pantalla de correo todavía
                                         * no usa Firebase Auth de verdad.
                                         * La corregiremos más adelante.
                                         */

                                        PerfilLocalStorage.guardar(
                                            appContext,
                                            perfilUsuario
                                        )


                                        navigateTo(
                                            "calculating_plan"
                                        )
                                    }
                                )
                            }


                            // =================================================
                            // CALCULANDO PLAN
                            // =================================================

                            "calculating_plan" -> {

                                CalculatingScreen(

                                    onFinishClick = {

                                        PerfilLocalStorage.guardar(
                                            appContext,
                                            perfilUsuario
                                        )


                                        if (
                                            FirebaseAuth
                                                .getInstance()
                                                .currentUser != null
                                        ) {

                                            PerfilFirebaseStorage.guardar(
                                                perfilUsuario
                                            )
                                        }


                                        navigateAndClear(
                                            "home_dashboard"
                                        )
                                    }
                                )
                            }


                            // =================================================
                            // HOME
                            // =================================================

                            "home_dashboard" -> {

                                HomeScreen(

                                    perfilUsuario =
                                        perfilUsuario,


                                    onPerfilActualizado = {
                                            nuevoPerfil ->


                                        /*
                                         * Al editar Perfil:
                                         *
                                         * - actualiza pantalla
                                         * - guarda local
                                         * - guarda Firestore
                                         */

                                        actualizarPerfil(
                                            nuevoPerfil
                                        )
                                    },


                                    onLogoutClick = {

                                        // -------------------------------------
                                        // CERRAR FIREBASE
                                        // -------------------------------------

                                        FirebaseAuth
                                            .getInstance()
                                            .signOut()


                                        // -------------------------------------
                                        // ELIMINAR SOLO COPIA LOCAL
                                        // -------------------------------------
                                        //
                                        // Firestore NO se elimina.
                                        //
                                        // Así puede recuperarse al volver
                                        // a iniciar sesión.
                                        // -------------------------------------

                                        PerfilLocalStorage.limpiar(
                                            appContext
                                        )


                                        perfilUsuario =
                                            PerfilUsuario()


                                        navigateAndClear(
                                            "welcome"
                                        )
                                    }
                                )
                            }


                            // =================================================
                            // SEGURIDAD
                            // =================================================

                            else -> {

                                navigateAndClear(
                                    "welcome"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}