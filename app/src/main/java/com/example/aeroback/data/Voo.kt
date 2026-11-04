package com.example.aeroback.data

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
