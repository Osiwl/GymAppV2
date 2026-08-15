package com.example.gymappv2.ui.perfil

data class PerfilUsuario(

    // Datos básicos
    val nombre: String = "",
    val experiencia: String = "",
    val objetivo: String = "",
    val diasEntrenamiento: Int = 0,
    val genero: String = "",

    // Datos físicos
    val pesoKg: Float = 0f,
    val alturaCm: Float = 0f,
    val edad: Int = 0,

    // Preferencia de rutina
    val rutinaAutomatica: Boolean = true,

    // Récords personales
    val pressBancaKg: Double = 0.0,
    val pesoMuertoKg: Double = 0.0,
    val sentadillaKg: Double = 0.0,

    // Indica que el usuario terminó todas las preguntas
    val onboardingCompletado: Boolean = false
)