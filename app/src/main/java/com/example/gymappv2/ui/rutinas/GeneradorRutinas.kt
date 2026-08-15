package com.example.gymappv2.ui.rutinas

import com.example.gymappv2.ui.perfil.PerfilUsuario

enum class Nivel {
    PRINCIPIANTE,
    INTERMEDIO,
    AVANZADO
}

enum class Objetivo {
    BAJAR_PESO,
    MANTENER,
    GANAR_MASA
}

enum class Dias {
    TRES,
    CUATRO,
    CINCO,
    SEIS
}

data class EjercicioApp(
    val idFirebase: String = "",
    val nombreEs: String = "",
    val categoria: String = "",
    val urlImagen: String = "",
    val urlGif: String = "",
    val musculo: String = ""
)

data class RutinaDia(
    val titulo: String,
    val ejercicios: List<EjercicioApp>
)

data class PlanEntrenamiento(
    val nombreCamino: String,
    val descripcionDias: String,
    val diasRutina: List<RutinaDia>,
    val series: Int,
    val reps: String,
    val descanso: String,
    val cardio: String
)

// ==========================================================
// CONVERSORES DEL ONBOARDING
// ==========================================================

fun convertirNivel(texto: String): Nivel {

    return when (texto.trim().lowercase()) {

        "intermedio" ->
            Nivel.INTERMEDIO

        "avanzado" ->
            Nivel.AVANZADO

        else ->
            Nivel.PRINCIPIANTE
    }
}

fun convertirObjetivo(texto: String): Objetivo {

    return when (texto.trim().lowercase()) {

        "bajar de peso" ->
            Objetivo.BAJAR_PESO

        "mantenerme" ->
            Objetivo.MANTENER

        "ganar masa muscular" ->
            Objetivo.GANAR_MASA

        else ->
            Objetivo.MANTENER
    }
}

fun convertirDias(numeroDias: Int): Dias {

    return when (numeroDias) {

        3 -> Dias.TRES
        4 -> Dias.CUATRO
        5 -> Dias.CINCO
        6 -> Dias.SEIS

        else -> Dias.CUATRO
    }
}

// ==========================================================
// GENERAR DIRECTAMENTE DESDE PERFIL
// ==========================================================

fun calcularRutinaIdeal(
    perfil: PerfilUsuario
): PlanEntrenamiento {

    return calcularRutinaIdeal(
        nivel = convertirNivel(
            perfil.experiencia
        ),
        dias = convertirDias(
            perfil.diasEntrenamiento
        ),
        objetivo = convertirObjetivo(
            perfil.objetivo
        )
    )
}

// ==========================================================
// GENERADOR PRINCIPAL
// ==========================================================

fun calcularRutinaIdeal(
    nivel: Nivel,
    dias: Dias,
    objetivo: Objetivo
): PlanEntrenamiento {

    // ======================================================
    // EJERCICIOS PUSH
    // Pecho + Hombro + Tríceps
    // ======================================================

    val ejerciciosPush = listOf(

        EjercicioApp(
            idFirebase = "barbell bench press",
            nombreEs = "Press de banca plano",
            categoria = "Pecho"
        ),

        EjercicioApp(
            idFirebase = "lever incline chest press",
            nombreEs = "Press de pecho alto en máquina",
            categoria = "Pecho"
        ),

        EjercicioApp(
            idFirebase = "dumbbell shoulder press",
            nombreEs = "Press de hombros con mancuernas",
            categoria = "Hombro"
        ),

        EjercicioApp(
            idFirebase = "dumbbell lateral raise",
            nombreEs = "Elevaciones laterales",
            categoria = "Hombro"
        ),

        EjercicioApp(
            idFirebase = "cable triceps pushdown (v-bar)",
            nombreEs = "Extensión de tríceps en polea",
            categoria = "Brazos"
        )
    )

    // ======================================================
    // EJERCICIOS PULL
    // Espalda + Bíceps
    // ======================================================

    val ejerciciosPull = listOf(

        EjercicioApp(
            idFirebase = "pull-up",
            nombreEs = "Dominadas",
            categoria = "Espalda"
        ),

        EjercicioApp(
            idFirebase = "cable pulldown",
            nombreEs = "Jalón al pecho",
            categoria = "Espalda"
        ),

        EjercicioApp(
            idFirebase = "barbell bent over row",
            nombreEs = "Remo con barra",
            categoria = "Espalda"
        ),

        EjercicioApp(
            idFirebase = "lever seated row",
            nombreEs = "Remo en máquina",
            categoria = "Espalda"
        ),

        EjercicioApp(
            idFirebase = "ez barbell curl",
            nombreEs = "Curl con barra Z",
            categoria = "Brazos"
        ),

        EjercicioApp(
            idFirebase = "dumbbell hammer curl",
            nombreEs = "Curl martillo",
            categoria = "Brazos"
        )
    )

    // ======================================================
    // PIERNA
    // ======================================================

    val ejerciciosPierna = listOf(

        EjercicioApp(
            idFirebase = "barbell full squat",
            nombreEs = "Sentadilla con barra",
            categoria = "Pierna"
        ),

        EjercicioApp(
            idFirebase = "sled 45° leg press",
            nombreEs = "Prensa de piernas a 45°",
            categoria = "Pierna"
        ),

        EjercicioApp(
            idFirebase = "barbell romanian deadlift",
            nombreEs = "Peso muerto rumano",
            categoria = "Pierna"
        ),

        EjercicioApp(
            idFirebase = "lever leg extension",
            nombreEs = "Extensión de cuádriceps",
            categoria = "Pierna"
        ),

        EjercicioApp(
            idFirebase = "lever seated leg curl",
            nombreEs = "Flexión de isquios",
            categoria = "Pierna"
        ),

        EjercicioApp(
            idFirebase = "lever standing calf raise",
            nombreEs = "Pantorrillas de pie",
            categoria = "Pierna"
        )
    )

    // ======================================================
    // TORSO
    // ======================================================

    val ejerciciosTorso = listOf(

        EjercicioApp(
            idFirebase = "barbell bench press",
            nombreEs = "Press de banca plano",
            categoria = "Pecho"
        ),

        EjercicioApp(
            idFirebase = "lever incline chest press",
            nombreEs = "Press inclinado en máquina",
            categoria = "Pecho"
        ),

        EjercicioApp(
            idFirebase = "cable pulldown",
            nombreEs = "Jalón al pecho",
            categoria = "Espalda"
        ),

        EjercicioApp(
            idFirebase = "lever seated row",
            nombreEs = "Remo en máquina",
            categoria = "Espalda"
        ),

        EjercicioApp(
            idFirebase = "dumbbell shoulder press",
            nombreEs = "Press de hombros",
            categoria = "Hombro"
        ),

        EjercicioApp(
            idFirebase = "dumbbell lateral raise",
            nombreEs = "Elevaciones laterales",
            categoria = "Hombro"
        )
    )

    // ======================================================
    // CUERPO COMPLETO A
    // ======================================================

    val cuerpoCompletoA = listOf(

        EjercicioApp(
            "barbell full squat",
            "Sentadilla con barra",
            "Pierna"
        ),

        EjercicioApp(
            "barbell bench press",
            "Press de banca plano",
            "Pecho"
        ),

        EjercicioApp(
            "lever seated row",
            "Remo en máquina",
            "Espalda"
        ),

        EjercicioApp(
            "dumbbell shoulder press",
            "Press de hombros",
            "Hombro"
        ),

        EjercicioApp(
            "ez barbell curl",
            "Curl con barra Z",
            "Brazos"
        ),

        EjercicioApp(
            "cable triceps pushdown (v-bar)",
            "Extensión de tríceps",
            "Brazos"
        )
    )

    // ======================================================
    // CUERPO COMPLETO B
    // ======================================================

    val cuerpoCompletoB = listOf(

        EjercicioApp(
            "sled 45° leg press",
            "Prensa de piernas",
            "Pierna"
        ),

        EjercicioApp(
            "lever incline chest press",
            "Press inclinado",
            "Pecho"
        ),

        EjercicioApp(
            "cable pulldown",
            "Jalón al pecho",
            "Espalda"
        ),

        EjercicioApp(
            "dumbbell lateral raise",
            "Elevaciones laterales",
            "Hombro"
        ),

        EjercicioApp(
            "dumbbell hammer curl",
            "Curl martillo",
            "Brazos"
        ),

        EjercicioApp(
            "cable triceps pushdown (v-bar)",
            "Extensión tríceps polea",
            "Brazos"
        )
    )

    // ======================================================
    // CUERPO COMPLETO C
    // ======================================================

    val cuerpoCompletoC = listOf(

        EjercicioApp(
            "barbell romanian deadlift",
            "Peso muerto rumano",
            "Pierna"
        ),

        EjercicioApp(
            "barbell bench press",
            "Press de banca plano",
            "Pecho"
        ),

        EjercicioApp(
            "barbell bent over row",
            "Remo con barra",
            "Espalda"
        ),

        EjercicioApp(
            "dumbbell shoulder press",
            "Press de hombros",
            "Hombro"
        ),

        EjercicioApp(
            "ez barbell curl",
            "Curl con barra Z",
            "Brazos"
        ),

        EjercicioApp(
            "cable triceps pushdown (v-bar)",
            "Extensión de tríceps",
            "Brazos"
        )
    )

    // ======================================================
    // EL NIVEL YA MODIFICA EL VOLUMEN
    // ======================================================

    val series = when (nivel) {

        Nivel.PRINCIPIANTE -> 3

        Nivel.INTERMEDIO -> 4

        Nivel.AVANZADO -> 4
    }

    /*
     * También limitamos cuántos ejercicios aparecen
     * según experiencia.
     *
     * Principiante: máximo 4
     * Intermedio: máximo 5
     * Avanzado: lista completa
     */
    fun adaptarEjercicios(
        lista: List<EjercicioApp>
    ): List<EjercicioApp> {

        return when (nivel) {

            Nivel.PRINCIPIANTE ->
                lista.take(4)

            Nivel.INTERMEDIO ->
                lista.take(5)

            Nivel.AVANZADO ->
                lista
        }
    }

    // ======================================================
    // OBJETIVO
    // ======================================================

    val reps: String
    val descanso: String
    val cardio: String

    when (objetivo) {

        Objetivo.BAJAR_PESO -> {

            reps = "15"
            descanso = "60 seg"
            cardio =
                "20-30 min de cardio"
        }

        Objetivo.MANTENER -> {

            reps = "12"
            descanso = "90 seg"
            cardio =
                "10-15 min de cardio"
        }

        Objetivo.GANAR_MASA -> {

            reps = "8-12"
            descanso = "90-120 seg"
            cardio =
                "Cardio opcional"
        }
    }

    val nombreNivel = when (nivel) {

        Nivel.PRINCIPIANTE ->
            "Principiante"

        Nivel.INTERMEDIO ->
            "Intermedio"

        Nivel.AVANZADO ->
            "Avanzado"
    }

    // ======================================================
    // PLAN SEGÚN NÚMERO DE DÍAS
    // ======================================================

    return when (dias) {

        // ==================================================
        // 3 DÍAS
        // ==================================================

        Dias.TRES -> {

            val rutinas = listOf(

                RutinaDia(
                    titulo =
                        "Día 1: Cuerpo Completo A",
                    ejercicios =
                        adaptarEjercicios(
                            cuerpoCompletoA
                        )
                ),

                RutinaDia(
                    titulo =
                        "Día 2: Cuerpo Completo B",
                    ejercicios =
                        adaptarEjercicios(
                            cuerpoCompletoB
                        )
                ),

                RutinaDia(
                    titulo =
                        "Día 3: Cuerpo Completo C",
                    ejercicios =
                        adaptarEjercicios(
                            cuerpoCompletoC
                        )
                )
            )

            PlanEntrenamiento(
                nombreCamino =
                    "$nombreNivel - 3 Días",
                descripcionDias =
                    "Cuerpo Completo",
                diasRutina =
                    rutinas,
                series =
                    series,
                reps =
                    reps,
                descanso =
                    descanso,
                cardio =
                    cardio
            )
        }

        // ==================================================
        // 4 DÍAS
        // ==================================================

        Dias.CUATRO -> {

            val rutinas = listOf(

                RutinaDia(
                    "Día 1: Torso",
                    adaptarEjercicios(
                        ejerciciosTorso
                    )
                ),

                RutinaDia(
                    "Día 2: Pierna",
                    adaptarEjercicios(
                        ejerciciosPierna
                    )
                ),

                RutinaDia(
                    "Día 3: Torso",
                    adaptarEjercicios(
                        ejerciciosTorso
                    )
                ),

                RutinaDia(
                    "Día 4: Pierna",
                    adaptarEjercicios(
                        ejerciciosPierna
                    )
                )
            )

            PlanEntrenamiento(
                nombreCamino =
                    "$nombreNivel - 4 Días",
                descripcionDias =
                    "Torso / Pierna",
                diasRutina =
                    rutinas,
                series =
                    series,
                reps =
                    reps,
                descanso =
                    descanso,
                cardio =
                    cardio
            )
        }

        // ==================================================
        // 5 DÍAS
        // ==================================================

        Dias.CINCO -> {

            val rutinas = listOf(

                RutinaDia(
                    "Día 1: Push",
                    adaptarEjercicios(
                        ejerciciosPush
                    )
                ),

                RutinaDia(
                    "Día 2: Pull",
                    adaptarEjercicios(
                        ejerciciosPull
                    )
                ),

                RutinaDia(
                    "Día 3: Pierna",
                    adaptarEjercicios(
                        ejerciciosPierna
                    )
                ),

                RutinaDia(
                    "Día 4: Torso",
                    adaptarEjercicios(
                        ejerciciosTorso
                    )
                ),

                RutinaDia(
                    "Día 5: Pierna",
                    adaptarEjercicios(
                        ejerciciosPierna
                    )
                )
            )

            PlanEntrenamiento(
                nombreCamino =
                    "$nombreNivel - 5 Días",
                descripcionDias =
                    "Push / Pull / Pierna / Torso / Pierna",
                diasRutina =
                    rutinas,
                series =
                    series,
                reps =
                    reps,
                descanso =
                    descanso,
                cardio =
                    cardio
            )
        }

        // ==================================================
        // 6 DÍAS
        // ==================================================

        Dias.SEIS -> {

            val rutinas = listOf(

                RutinaDia(
                    "Día 1: Push",
                    adaptarEjercicios(
                        ejerciciosPush
                    )
                ),

                RutinaDia(
                    "Día 2: Pull",
                    adaptarEjercicios(
                        ejerciciosPull
                    )
                ),

                RutinaDia(
                    "Día 3: Pierna",
                    adaptarEjercicios(
                        ejerciciosPierna
                    )
                ),

                RutinaDia(
                    "Día 4: Push",
                    adaptarEjercicios(
                        ejerciciosPush
                    )
                ),

                RutinaDia(
                    "Día 5: Pull",
                    adaptarEjercicios(
                        ejerciciosPull
                    )
                ),

                RutinaDia(
                    "Día 6: Pierna",
                    adaptarEjercicios(
                        ejerciciosPierna
                    )
                )
            )

            PlanEntrenamiento(
                nombreCamino =
                    "$nombreNivel - 6 Días",
                descripcionDias =
                    "Push / Pull / Pierna x2",
                diasRutina =
                    rutinas,
                series =
                    series,
                reps =
                    reps,
                descanso =
                    descanso,
                cardio =
                    cardio
            )
        }
    }
}