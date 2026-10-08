package com.example.aeroback

data class Voo(
    val id: String,
    val companhia: String,
    val origem: String,
    val destino: String,
    val dataIda: String,
    val horario: String,
    val precoBase: Double,
    val cashbackPorcento: Int
)

data class Reserva(
    val id: String,
    val vooId: String,
    val passageiro: String,
    val assento: String,
    val bagagemDespachada: Boolean,
    val assentoVip: Boolean,
    val precoFinal: Double,
    val cashbackGanho: Double,
    var checkInRealizado: Boolean = false
)
