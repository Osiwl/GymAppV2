package com.example.gymappv2.ui.perfil

import android.content.Context

object PerfilLocalStorage {

    private const val PREFS_NAME = "gymbros_perfil_usuario"

    private const val KEY_NOMBRE = "nombre"
    private const val KEY_EXPERIENCIA = "experiencia"
    private const val KEY_OBJETIVO = "objetivo"
    private const val KEY_DIAS = "dias_entrenamiento"
    private const val KEY_GENERO = "genero"

    private const val KEY_PESO = "peso_kg"
    private const val KEY_ALTURA = "altura_cm"
    private const val KEY_EDAD = "edad"

    private const val KEY_RUTINA_AUTOMATICA = "rutina_automatica"

    private const val KEY_PRESS_BANCA = "press_banca"
    private const val KEY_PESO_MUERTO = "peso_muerto"
    private const val KEY_SENTADILLA = "sentadilla"

    private const val KEY_ONBOARDING_COMPLETADO =
        "onboarding_completado"


    fun guardar(
        context: Context,
        perfil: PerfilUsuario
    ) {

        val prefs = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        prefs.edit()
            .putString(KEY_NOMBRE, perfil.nombre)
            .putString(KEY_EXPERIENCIA, perfil.experiencia)
            .putString(KEY_OBJETIVO, perfil.objetivo)
            .putInt(KEY_DIAS, perfil.diasEntrenamiento)
            .putString(KEY_GENERO, perfil.genero)
            .putFloat(KEY_PESO, perfil.pesoKg)
            .putFloat(KEY_ALTURA, perfil.alturaCm)
            .putInt(KEY_EDAD, perfil.edad)
            .putBoolean(
                KEY_RUTINA_AUTOMATICA,
                perfil.rutinaAutomatica
            )
            .putString(
                KEY_PRESS_BANCA,
                perfil.pressBancaKg.toString()
            )
            .putString(
                KEY_PESO_MUERTO,
                perfil.pesoMuertoKg.toString()
            )
            .putString(
                KEY_SENTADILLA,
                perfil.sentadillaKg.toString()
            )
            .putBoolean(
                KEY_ONBOARDING_COMPLETADO,
                perfil.onboardingCompletado
            )
            .apply()
    }


    fun cargar(
        context: Context
    ): PerfilUsuario {

        val prefs = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        return PerfilUsuario(

            nombre =
                prefs.getString(
                    KEY_NOMBRE,
                    ""
                ) ?: "",

            experiencia =
                prefs.getString(
                    KEY_EXPERIENCIA,
                    ""
                ) ?: "",

            objetivo =
                prefs.getString(
                    KEY_OBJETIVO,
                    ""
                ) ?: "",

            diasEntrenamiento =
                prefs.getInt(
                    KEY_DIAS,
                    0
                ),

            genero =
                prefs.getString(
                    KEY_GENERO,
                    ""
                ) ?: "",

            pesoKg =
                prefs.getFloat(
                    KEY_PESO,
                    0f
                ),

            alturaCm =
                prefs.getFloat(
                    KEY_ALTURA,
                    0f
                ),

            edad =
                prefs.getInt(
                    KEY_EDAD,
                    0
                ),

            rutinaAutomatica =
                prefs.getBoolean(
                    KEY_RUTINA_AUTOMATICA,
                    true
                ),

            pressBancaKg =
                prefs.getString(
                    KEY_PRESS_BANCA,
                    "0.0"
                )?.toDoubleOrNull() ?: 0.0,

            pesoMuertoKg =
                prefs.getString(
                    KEY_PESO_MUERTO,
                    "0.0"
                )?.toDoubleOrNull() ?: 0.0,

            sentadillaKg =
                prefs.getString(
                    KEY_SENTADILLA,
                    "0.0"
                )?.toDoubleOrNull() ?: 0.0,

            onboardingCompletado =
                prefs.getBoolean(
                    KEY_ONBOARDING_COMPLETADO,
                    false
                )
        )
    }


    fun limpiar(
        context: Context
    ) {

        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
            .edit()
            .clear()
            .apply()
    }
}