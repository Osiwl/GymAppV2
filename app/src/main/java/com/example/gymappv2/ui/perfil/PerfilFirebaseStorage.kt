package com.example.gymappv2.ui.perfil

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


object PerfilFirebaseStorage {

    private const val TAG =
        "PERFIL_FIREBASE"

    private const val COLECCION_USUARIOS =
        "usuarios"


    // =====================================================
    // GUARDAR PERFIL EN FIRESTORE
    // =====================================================

    fun guardar(
        perfil: PerfilUsuario,
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


        val datos =
            hashMapOf<String, Any>(

                "uid" to
                        usuario.uid,

                "email" to
                        (usuario.email ?: ""),

                "nombre" to
                        perfil.nombre,

                "experiencia" to
                        perfil.experiencia,

                "objetivo" to
                        perfil.objetivo,

                "diasEntrenamiento" to
                        perfil.diasEntrenamiento,

                "genero" to
                        perfil.genero,

                "pesoKg" to
                        perfil.pesoKg,

                "alturaCm" to
                        perfil.alturaCm,

                "edad" to
                        perfil.edad,

                "rutinaAutomatica" to
                        perfil.rutinaAutomatica,

                "pressBancaKg" to
                        perfil.pressBancaKg,

                "pesoMuertoKg" to
                        perfil.pesoMuertoKg,

                "sentadillaKg" to
                        perfil.sentadillaKg,

                "onboardingCompletado" to
                        perfil.onboardingCompletado,

                "actualizadoEn" to
                        FieldValue.serverTimestamp()
            )


        FirebaseFirestore
            .getInstance()
            .collection(
                COLECCION_USUARIOS
            )
            .document(
                usuario.uid
            )
            .set(
                datos,
                SetOptions.merge()
            )
            .addOnSuccessListener {

                Log.d(
                    TAG,
                    "Perfil guardado correctamente"
                )

                onResultado(true)
            }
            .addOnFailureListener {
                    error ->

                Log.e(
                    TAG,
                    "Error guardando perfil",
                    error
                )

                onResultado(false)
            }
    }


    // =====================================================
    // CARGAR PERFIL DESDE FIRESTORE
    // =====================================================

    fun cargar(
        onResultado: (PerfilUsuario?) -> Unit
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
            .get()
            .addOnSuccessListener {
                    documento ->


                if (
                    !documento.exists()
                ) {

                    Log.d(
                        TAG,
                        "El usuario todavía no tiene perfil"
                    )

                    onResultado(null)

                    return@addOnSuccessListener
                }


                val perfil =
                    PerfilUsuario(

                        nombre =
                            documento
                                .getString(
                                    "nombre"
                                )
                                ?: "",


                        experiencia =
                            documento
                                .getString(
                                    "experiencia"
                                )
                                ?: "",


                        objetivo =
                            documento
                                .getString(
                                    "objetivo"
                                )
                                ?: "",


                        diasEntrenamiento =
                            (
                                    documento.get(
                                        "diasEntrenamiento"
                                    )
                                            as? Number
                                    )
                                ?.toInt()
                                ?: 0,


                        genero =
                            documento
                                .getString(
                                    "genero"
                                )
                                ?: "",


                        pesoKg =
                            (
                                    documento.get(
                                        "pesoKg"
                                    )
                                            as? Number
                                    )
                                ?.toFloat()
                                ?: 0f,


                        alturaCm =
                            (
                                    documento.get(
                                        "alturaCm"
                                    )
                                            as? Number
                                    )
                                ?.toFloat()
                                ?: 0f,


                        edad =
                            (
                                    documento.get(
                                        "edad"
                                    )
                                            as? Number
                                    )
                                ?.toInt()
                                ?: 0,


                        rutinaAutomatica =
                            documento
                                .getBoolean(
                                    "rutinaAutomatica"
                                )
                                ?: true,


                        pressBancaKg =
                            (
                                    documento.get(
                                        "pressBancaKg"
                                    )
                                            as? Number
                                    )
                                ?.toDouble()
                                ?: 0.0,


                        pesoMuertoKg =
                            (
                                    documento.get(
                                        "pesoMuertoKg"
                                    )
                                            as? Number
                                    )
                                ?.toDouble()
                                ?: 0.0,


                        sentadillaKg =
                            (
                                    documento.get(
                                        "sentadillaKg"
                                    )
                                            as? Number
                                    )
                                ?.toDouble()
                                ?: 0.0,


                        onboardingCompletado =
                            documento
                                .getBoolean(
                                    "onboardingCompletado"
                                )
                                ?: false
                    )


                Log.d(
                    TAG,
                    "Perfil cargado: $perfil"
                )


                onResultado(
                    perfil
                )
            }
            .addOnFailureListener {
                    error ->

                Log.e(
                    TAG,
                    "Error cargando perfil",
                    error
                )

                onResultado(null)
            }
    }
}