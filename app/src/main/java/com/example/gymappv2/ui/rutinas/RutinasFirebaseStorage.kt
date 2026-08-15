package com.example.gymappv2.ui.rutinas

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch


object RutinasFirebaseStorage {

    private const val TAG =
        "RUTINAS_FIREBASE"

    private const val COLECCION_USUARIOS =
        "usuarios"

    private const val COLECCION_RUTINAS =
        "rutinas"


    // =========================================================
    // GUARDAR TODAS LAS RUTINAS
    // =========================================================

    fun guardarRutinas(
        rutinas: List<RutinaDia>,
        onResultado: (Boolean) -> Unit = {}
    ) {

        val usuario =
            FirebaseAuth
                .getInstance()
                .currentUser


        if (usuario == null) {

            Log.e(
                TAG,
                "No hay usuario autenticado"
            )

            onResultado(false)

            return
        }


        val db =
            FirebaseFirestore
                .getInstance()


        val referenciaRutinas =
            db
                .collection(
                    COLECCION_USUARIOS
                )
                .document(
                    usuario.uid
                )
                .collection(
                    COLECCION_RUTINAS
                )


        /*
         * Primero obtenemos las rutinas existentes.
         *
         * Después usamos un batch para:
         *
         * - borrar las anteriores
         * - escribir las actuales
         *
         * Así Firestore siempre queda igual
         * que la lista que tiene HomeScreen.
         */

        referenciaRutinas
            .get()
            .addOnSuccessListener {
                    snapshot ->


                val batch =
                    db.batch()


                // =============================================
                // BORRAR RUTINAS ANTERIORES
                // =============================================

                snapshot.documents
                    .forEach {
                            documento ->

                        batch.delete(
                            documento.reference
                        )
                    }


                // =============================================
                // GUARDAR RUTINAS ACTUALES
                // =============================================

                rutinas.forEachIndexed {
                        index,
                        rutina ->


                    val documento =
                        referenciaRutinas
                            .document(
                                "rutina_$index"
                            )


                    val ejercicios =
                        rutina
                            .ejercicios
                            .map {
                                    ejercicio ->


                                hashMapOf<String, Any>(

                                    "idFirebase" to
                                            ejercicio.idFirebase,

                                    "nombreEs" to
                                            ejercicio.nombreEs,

                                    "categoria" to
                                            ejercicio.categoria,

                                    "urlImagen" to
                                            ejercicio.urlImagen,

                                    "urlGif" to
                                            ejercicio.urlGif,

                                    "musculo" to
                                            ejercicio.musculo
                                )
                            }


                    val datos =
                        hashMapOf<String, Any>(

                            "orden" to
                                    index,

                            "titulo" to
                                    rutina.titulo,

                            "ejercicios" to
                                    ejercicios
                        )


                    batch.set(
                        documento,
                        datos
                    )
                }


                // =============================================
                // EJECUTAR TODO
                // =============================================

                batch
                    .commit()
                    .addOnSuccessListener {

                        Log.d(
                            TAG,
                            "Rutinas guardadas correctamente: ${rutinas.size}"
                        )


                        onResultado(true)
                    }
                    .addOnFailureListener {
                            error ->


                        Log.e(
                            TAG,
                            "Error guardando rutinas",
                            error
                        )


                        onResultado(false)
                    }
            }
            .addOnFailureListener {
                    error ->


                Log.e(
                    TAG,
                    "Error leyendo rutinas anteriores",
                    error
                )


                onResultado(false)
            }
    }


    // =========================================================
    // CARGAR TODAS LAS RUTINAS
    // =========================================================

    fun cargarRutinas(
        onResultado: (List<RutinaDia>?) -> Unit
    ) {

        val usuario =
            FirebaseAuth
                .getInstance()
                .currentUser


        if (usuario == null) {

            Log.e(
                TAG,
                "No hay usuario autenticado"
            )


            onResultado(null)

            return
        }


        FirebaseFirestore
            .getInstance()
            .collection(
                COLECCION_USUARIOS
            )
            .document(
                usuario.uid
            )
            .collection(
                COLECCION_RUTINAS
            )
            .orderBy(
                "orden"
            )
            .get()
            .addOnSuccessListener {
                    snapshot ->


                // =============================================
                // NO EXISTEN RUTINAS GUARDADAS
                // =============================================

                if (
                    snapshot.isEmpty
                ) {

                    Log.d(
                        TAG,
                        "El usuario todavía no tiene rutinas guardadas"
                    )


                    onResultado(
                        emptyList()
                    )

                    return@addOnSuccessListener
                }


                val listaRutinas =
                    snapshot.documents
                        .mapNotNull {
                                documento ->


                            try {

                                // =====================================
                                // TÍTULO
                                // =====================================

                                val titulo =
                                    documento
                                        .getString(
                                            "titulo"
                                        )
                                        ?: "Rutina"


                                // =====================================
                                // EJERCICIOS
                                // =====================================

                                val ejerciciosRaw =
                                    documento.get(
                                        "ejercicios"
                                    )
                                            as? List<*>
                                        ?: emptyList<Any>()


                                val ejercicios =
                                    ejerciciosRaw
                                        .mapNotNull {
                                                item ->


                                            val mapa =
                                                item
                                                        as? Map<*, *>
                                                    ?: return@mapNotNull null


                                            EjercicioApp(

                                                idFirebase =
                                                    mapa[
                                                        "idFirebase"
                                                    ]
                                                        ?.toString()
                                                        ?: "",


                                                nombreEs =
                                                    mapa[
                                                        "nombreEs"
                                                    ]
                                                        ?.toString()
                                                        ?: "",


                                                categoria =
                                                    mapa[
                                                        "categoria"
                                                    ]
                                                        ?.toString()
                                                        ?: "",


                                                urlImagen =
                                                    mapa[
                                                        "urlImagen"
                                                    ]
                                                        ?.toString()
                                                        ?: "",


                                                urlGif =
                                                    mapa[
                                                        "urlGif"
                                                    ]
                                                        ?.toString()
                                                        ?: "",


                                                musculo =
                                                    mapa[
                                                        "musculo"
                                                    ]
                                                        ?.toString()
                                                        ?: ""
                                            )
                                        }


                                // =====================================
                                // RUTINA COMPLETA
                                // =====================================

                                RutinaDia(

                                    titulo =
                                        titulo,

                                    ejercicios =
                                        ejercicios
                                )


                            } catch (
                                error: Exception
                            ) {

                                Log.e(
                                    TAG,
                                    "Error convirtiendo rutina ${documento.id}",
                                    error
                                )


                                null
                            }
                        }


                Log.d(
                    TAG,
                    "Rutinas cargadas: ${listaRutinas.size}"
                )


                onResultado(
                    listaRutinas
                )
            }
            .addOnFailureListener {
                    error ->


                Log.e(
                    TAG,
                    "Error cargando rutinas",
                    error
                )


                onResultado(null)
            }
    }


    // =========================================================
    // BORRAR TODAS LAS RUTINAS
    // =========================================================

    fun borrarTodas(
        onResultado: (Boolean) -> Unit = {}
    ) {

        val usuario =
            FirebaseAuth
                .getInstance()
                .currentUser


        if (usuario == null) {

            onResultado(false)

            return
        }


        val db =
            FirebaseFirestore
                .getInstance()


        val referencia =
            db
                .collection(
                    COLECCION_USUARIOS
                )
                .document(
                    usuario.uid
                )
                .collection(
                    COLECCION_RUTINAS
                )


        referencia
            .get()
            .addOnSuccessListener {
                    snapshot ->


                val batch:
                        WriteBatch =
                    db.batch()


                snapshot.documents
                    .forEach {
                            documento ->


                        batch.delete(
                            documento.reference
                        )
                    }


                batch
                    .commit()
                    .addOnSuccessListener {

                        Log.d(
                            TAG,
                            "Todas las rutinas fueron eliminadas"
                        )


                        onResultado(true)
                    }
                    .addOnFailureListener {
                            error ->


                        Log.e(
                            TAG,
                            "Error eliminando rutinas",
                            error
                        )


                        onResultado(false)
                    }
            }
            .addOnFailureListener {
                    error ->


                Log.e(
                    TAG,
                    "Error obteniendo rutinas para borrar",
                    error
                )


                onResultado(false)
            }
    }
}