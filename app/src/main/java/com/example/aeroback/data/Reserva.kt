package com.example.aeroback.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reservas")
data class Reserva(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val vooId: Int,
    val passageiro: String,
    val assento: String,
    val bagagemDespachada: Boolean,
    val assentoVip: Boolean,
    val precoFinal: Double,
    val cashbackGanho: Double,
    val checkInRealizado: Boolean = false
)
