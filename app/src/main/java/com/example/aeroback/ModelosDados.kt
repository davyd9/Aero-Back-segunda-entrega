package com.example.aeroback

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "voos")
data class Voo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val companhia: String,
    val origem: String,
    val destino: String,
    val dataIda: String,
    val horario: String,
    val precoBase: Double,
    val cashbackPorcento: Int
)

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
