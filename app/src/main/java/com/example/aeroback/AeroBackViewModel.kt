package com.example.aeroback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AeroBackRepository(
    private val vooDao: VooDao,
    private val reservaDao: ReservaDao
) {
    val todosVoos = vooDao.listarVoos()
    val todasReservas = reservaDao.listarReservas()

    suspend fun contarVoos() = vooDao.contarVoos()
    suspend fun contarReservas() = reservaDao.contarReservas()

    suspend fun inserirVoo(voo: Voo) = vooDao.inserirVoo(voo)
    suspend fun inserirListaVoos(voos: List<Voo>) = vooDao.inserirListaVoos(voos)
    suspend fun atualizarVoo(voo: Voo) = vooDao.atualizarVoo(voo)
    suspend fun deletarVoo(voo: Voo) = vooDao.deletarVoo(voo)

    suspend fun inserirReserva(reserva: Reserva) = reservaDao.inserirReserva(reserva)
    suspend fun inserirListaReservas(reservas: List<Reserva>) = reservaDao.inserirListaReservas(reservas)
    suspend fun atualizarReserva(reserva: Reserva) = reservaDao.atualizarReserva(reserva)
    suspend fun deletarReserva(reserva: Reserva) = reservaDao.deletarReserva(reserva)
}

class AeroBackViewModel(
    private val repository: AeroBackRepository
) : ViewModel() {

    val voos: StateFlow<List<Voo>> = repository.todosVoos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val reservas: StateFlow<List<Reserva>> = repository.todasReservas.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        popularDadosIniciaisSeVazio()
    }

    private fun popularDadosIniciaisSeVazio() {
        viewModelScope.launch {
            if (repository.contarVoos() == 0) {
                val voosIniciais = listOf(
                    Voo(
                        id = "voo-1",
                        companhia = "LATAM Airlines",
                        origem = "GRU",
                        destino = "MIA",
                        dataIda = "15 Mar 2026",
                        horario = "22:45",
                        precoBase = 2850.0,
                        cashbackPorcento = 12
                    ),
                    Voo(
                        id = "voo-2",
                        companhia = "Gol Linhas A\u00E9reas",
                        origem = "CWB",
                        destino = "GIG",
                        dataIda = "20 Mar 2026",
                        horario = "09:15",
                        precoBase = 620.0,
                        cashbackPorcento = 15
                    ),
                    Voo(
                        id = "voo-3",
                        companhia = "Azul Linhas A\u00E9reas",
                        origem = "CWB",
                        destino = "GRU",
                        dataIda = "25 Mar 2026",
                        horario = "14:30",
                        precoBase = 490.0,
                        cashbackPorcento = 10
                    )
                )
                repository.inserirListaVoos(voosIniciais)
            }

            if (repository.contarReservas() == 0) {
                val reservasIniciais = listOf(
                    Reserva(
                        id = "RES-101",
                        vooId = "voo-1",
                        passageiro = "Davyd Molina",
                        assento = "03A (VIP)",
                        bagagemDespachada = true,
                        assentoVip = true,
                        precoFinal = 3080.0,
                        cashbackGanho = 369.60,
                        checkInRealizado = true
                    ),
                    Reserva(
                        id = "RES-102",
                        vooId = "voo-2",
                        passageiro = "Davyd Molina",
                        assento = "14C",
                        bagagemDespachada = false,
                        assentoVip = false,
                        precoFinal = 620.0,
                        cashbackGanho = 93.00,
                        checkInRealizado = false
                    )
                )
                repository.inserirListaReservas(reservasIniciais)
            }
        }
    }

    fun adicionarVoo(voo: Voo) {
        viewModelScope.launch {
            repository.inserirVoo(voo)
        }
    }

    fun atualizarVoo(voo: Voo) {
        viewModelScope.launch {
            repository.atualizarVoo(voo)
        }
    }

    fun removerVoo(voo: Voo) {
        viewModelScope.launch {
            repository.deletarVoo(voo)
        }
    }

    fun adicionarReserva(reserva: Reserva) {
        viewModelScope.launch {
            repository.inserirReserva(reserva)
        }
    }

    fun atualizarReserva(reserva: Reserva) {
        viewModelScope.launch {
            repository.atualizarReserva(reserva)
        }
    }

    fun alternarCheckIn(reserva: Reserva, checkIn: Boolean) {
        viewModelScope.launch {
            repository.atualizarReserva(reserva.copy(checkInRealizado = checkIn))
        }
    }

    fun removerReserva(reserva: Reserva) {
        viewModelScope.launch {
            repository.deletarReserva(reserva)
        }
    }
}

class AeroBackViewModelFactory(
    private val repository: AeroBackRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AeroBackViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AeroBackViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconhecido")
    }
}