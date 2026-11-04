package com.example.aeroback.viewmodel

import com.example.aeroback.data.Reserva
import com.example.aeroback.data.Voo

data class ReservaUiState(
    val reservas: List<Reserva> = emptyList(),
    val voosDisponiveis: List<Voo> = emptyList(),
    val reservaSelecionada: Reserva? = null,
    val vooDaReservaSelecionada: Voo? = null,
    val idReservaCarregada: Int? = null,
    val isLoading: Boolean = false,
    val carregandoDetalhes: Boolean = false,
    val erro: String? = null,

    // Controle de diálogo e formulário de reservas (TelaListaReservas)
    val exibirDialogNovaReserva: Boolean = false,
    val reservaEmEdicao: Reserva? = null,
    val passageiroInput: String = "",
    val assentoInput: String = "08B",
    val bagagemInput: Boolean = false
) {
    val saldoTotalAcumulado: Double
        get() = reservas.sumOf { it.cashbackGanho }

    val saldoConfirmado: Double
        get() = reservas.filter { it.checkInRealizado }.sumOf { it.cashbackGanho }

    val saldoPendente: Double
        get() = reservas.filter { !it.checkInRealizado }.sumOf { it.cashbackGanho }
}
