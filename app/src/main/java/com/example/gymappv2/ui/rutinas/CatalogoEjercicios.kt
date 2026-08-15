package com.example.gymappv2.ui.rutinas

object CatalogoEjercicios {
    val todos = listOf(
        // PECHO
        EjercicioApp("barbell bench press", "Press de banca plano", "Pecho"),
        EjercicioApp("lever incline chest press", "Press de pecho alto en máquina", "Pecho"),
        EjercicioApp("lever seated fly", "Pec-Deck (mariposas)", "Pecho"),
        EjercicioApp("cable cross-over variation", "Cruces en polea", "Pecho"),

        // ESPALDA
        EjercicioApp("pull-up", "Dominadas", "Espalda"),
        EjercicioApp("cable pulldown", "Jalón al pecho", "Espalda"),
        EjercicioApp("barbell bent over row", "Remo con barra", "Espalda"),
        EjercicioApp("lever seated row", "Remo en máquina", "Espalda"),

        // PIERNA
        EjercicioApp("sled 45° leg press", "Prensa de piernas (45°)", "Pierna"), // 🔥 CORREGIDO
        EjercicioApp("barbell full squat", "Sentadilla con barra", "Pierna"),
        EjercicioApp("barbell romanian deadlift", "Peso muerto rumano", "Pierna"),
        EjercicioApp("lever leg extension", "Extensión de cuádriceps", "Pierna"),
        EjercicioApp("lever seated leg curl", "Flexión de isquios", "Pierna"),
        EjercicioApp("lever standing calf raise", "Pantorrillas de pie", "Pierna"),

        // BRAZOS
        EjercicioApp("ez barbell curl", "Curl con barra Z", "Brazos"),
        EjercicioApp("dumbbell hammer curl", "Curl martillo", "Brazos"),
        EjercicioApp("cable triceps pushdown (v-bar)", "Extensión tríceps polea", "Brazos"),
        EjercicioApp("barbell lying triceps extension skull crusher", "Press francés", "Brazos"),

        // HOMBRO
        EjercicioApp("dumbbell shoulder press", "Press de hombros con mancuernas", "Hombro"),
        EjercicioApp("barbell standing military press", "Press militar", "Hombro"),
        EjercicioApp("dumbbell lateral raise", "Elevaciones laterales", "Hombro")
    )
}