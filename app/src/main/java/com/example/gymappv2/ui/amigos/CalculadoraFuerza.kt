package com.example.gymappv2.ui.amigos

import kotlin.math.pow

object CalculadoraFuerza {

    /**
     * Calcula el puntaje relativo exacto usando la Fórmula Oficial de Wilks.
     * Toma en cuenta el peso corporal del atleta contra el total de peso que puede levantar.
     */
    fun calcularPuntaje(pesoCorporalKg: Double, totalLevantadoKg: Double, esHombre: Boolean = true): Int {
        // Validación de seguridad para evitar que la app colapse si el usuario no pone su peso
        if (pesoCorporalKg <= 10.0 || totalLevantadoKg <= 0.0) return 0

        // Coeficientes oficiales mundiales
        val a: Double
        val b: Double
        val c: Double
        val d: Double
        val e: Double
        val f: Double

        if (esHombre) {
            a = -216.0475144
            b = 16.2606339
            c = -0.002388645
            d = -0.00113732
            e = 0.00000701863
            f = -0.00000001291
        } else {
            a = 594.31747775582
            b = -27.23842536447
            c = 0.82112226871
            d = -0.00930733913
            e = 0.00004731582
            f = -0.00000009054
        }

        val x = pesoCorporalKg

        // Ecuación polinómica de quinto grado para calcular la ventaja/desventaja del peso corporal
        val denominador = a + (b * x) + (c * x.pow(2)) + (d * x.pow(3)) + (e * x.pow(4)) + (f * x.pow(5))

        if (denominador == 0.0) return 0

        val coeficiente = 500.0 / denominador

        // Retornamos el puntaje redondeado como un número entero para que se vea limpio en pantalla
        return (totalLevantadoKg * coeficiente).toInt()
    }

    /**
     * Función rápida para obtener el Total de Powerlifting (SBD)
     */
    fun obtenerTotal(sentadilla: Double, pressBanca: Double, pesoMuerto: Double): Double {
        return sentadilla + pressBanca + pesoMuerto
    }
}