package com.example.aeroback.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aeroback.data.Reserva
import com.example.aeroback.data.Voo
import com.example.aeroback.data.repository.ReservaRepository
import com.example.aeroback.data.repository.VooRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.withLock

class ReservaViewModel(
    private val reservaRepository: ReservaRepository,
    private val vooRepository: VooRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReservaUiState())
    val uiState: StateFlow<ReservaUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, erro = null) }
            try {
                VooRepository.seedMutex.withLock {
                    if (vooRepository.contarVoos() == 0) {
                        vooRepository.inserirListaVoos(VooRepository.voosPadrao())
                    }
                    if (reservaRepository.contarReservas() == 0) {
                        val voosCadastrados = vooRepository.listarVoos().first()
                        if (voosCadastrados.isNotEmpty()) {
                            val primeiroVooId = voosCadastrados.first().id
                            val segundoVooId = voosCadastrados.getOrNull(1)?.id ?: primeiroVooId
                            reservaRepository.inserirListaReservas(
                                ReservaRepository.reservasPadrao(primeiroVooId, segundoVooId)
                            )
                        }
                    }
                }

                combine(
                    reservaRepository.listarReservas(),
                    vooRepository.listarVoos()
                ) { listaReservas, listaVoos ->
                    listaReservas to listaVoos
                }.collect { (listaReservas, listaVoos) ->
                    _uiState.update { estadoAtual ->
                        estadoAtual.copy(
                            reservas = listaReservas,
                            voosDisponiveis = listaVoos,
                            isLoading = false,
                            erro = null
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("ReservaViewModel", "Erro ao inicializar e coletar reservas: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        erro = "Erro ao carregar reservas: ${e.message}"
                    )
                }
            }
        }
    }

    // --- Emissão de Reserva a partir da TelaDetalhesVoo ---

    fun emitirReservaDoVoo(
        voo: Voo,
        nomePassageiro: String,
        addBagagem: Boolean,
        addAssentoVip: Boolean,
        onSucesso: () -> Unit = {}
    ) {
        val valorBagagem = if (addBagagem) 150.0 else 0.0
        val valorAssento = if (addAssentoVip) 80.0 else 0.0
        val valorTotalCalculado = voo.precoBase + valorBagagem + valorAssento
        val cashbackTotalCalculado = valorTotalCalculado * (voo.cashbackPorcento / 100.0)

        val novaReserva = Reserva(
            vooId = voo.id,
            passageiro = nomePassageiro.ifBlank { "Davyd Molina" },
            assento = if (addAssentoVip) "03A (VIP)" else "14C",
            bagagemDespachada = addBagagem,
            assentoVip = addAssentoVip,
            precoFinal = valorTotalCalculado,
            cashbackGanho = cashbackTotalCalculado,
            checkInRealizado = false
        )

        viewModelScope.launch {
            try {
                reservaRepository.inserirReserva(novaReserva)
                onSucesso()
            } catch (e: Exception) {
                Log.e("ReservaViewModel", "Erro ao inserir reserva do voo: ${e.message}", e)
                _uiState.update { it.copy(erro = "Erro ao emitir reserva: ${e.message}") }
            }
        }
    }

    // --- Ações da TelaListaReservas (CRUD + Check-in) ---

    fun abrirDialogNovaReserva() {
        _uiState.update {
            it.copy(
                passageiroInput = "",
                assentoInput = "08B",
                bagagemInput = false,
                exibirDialogNovaReserva = true,
                reservaEmEdicao = null
            )
        }
    }

    fun fecharDialogNovaReserva() {
        _uiState.update { it.copy(exibirDialogNovaReserva = false) }
    }

    fun iniciarEdicaoReserva(reserva: Reserva) {
        _uiState.update {
            it.copy(
                passageiroInput = reserva.passageiro,
                assentoInput = reserva.assento,
                bagagemInput = reserva.bagagemDespachada,
                reservaEmEdicao = reserva,
                exibirDialogNovaReserva = false
            )
        }
    }

    fun cancelarEdicaoReserva() {
        _uiState.update { it.copy(reservaEmEdicao = null) }
    }

    fun atualizarPassageiroInput(valor: String) {
        _uiState.update { it.copy(passageiroInput = valor) }
    }

    fun atualizarAssentoInput(valor: String) {
        _uiState.update { it.copy(assentoInput = valor) }
    }

    fun atualizarBagagemInput(valor: Boolean) {
        _uiState.update { it.copy(bagagemInput = valor) }
    }

    fun adicionarReservaManual() {
        val estado = _uiState.value
        if (estado.passageiroInput.isBlank()) return

        val vooPadrao = estado.voosDisponiveis.firstOrNull()
        val precoBase = vooPadrao?.precoBase ?: 850.0
        val pct = vooPadrao?.cashbackPorcento ?: 10
        val precoFinal = precoBase + if (estado.bagagemInput) 150.0 else 0.0

        val novaReserva = Reserva(
            vooId = vooPadrao?.id ?: 1,
            passageiro = estado.passageiroInput,
            assento = estado.assentoInput.ifBlank { "10A" },
            bagagemDespachada = estado.bagagemInput,
            assentoVip = false,
            precoFinal = precoFinal,
            cashbackGanho = precoFinal * (pct / 100.0),
            checkInRealizado = false
        )

        _uiState.update { it.copy(exibirDialogNovaReserva = false) }

        viewModelScope.launch {
            try {
                reservaRepository.inserirReserva(novaReserva)
            } catch (e: Exception) {
                Log.e("ReservaViewModel", "Erro ao inserir reserva manual: ${e.message}", e)
                _uiState.update { it.copy(erro = "Erro ao adicionar reserva: ${e.message}") }
            }
        }
    }

    fun salvarEdicaoReserva() {
        val estado = _uiState.value
        val atual = estado.reservaEmEdicao ?: return

        val reservaEditada = atual.copy(
            passageiro = estado.passageiroInput.ifBlank { atual.passageiro },
            assento = estado.assentoInput.ifBlank { atual.assento },
            bagagemDespachada = estado.bagagemInput
        )

        _uiState.update { it.copy(reservaEmEdicao = null) }

        viewModelScope.launch {
            try {
                reservaRepository.atualizarReserva(reservaEditada)
                if (_uiState.value.reservaSelecionada?.id == reservaEditada.id) {
                    _uiState.update { it.copy(reservaSelecionada = reservaEditada) }
                }
            } catch (e: Exception) {
                Log.e("ReservaViewModel", "Erro ao atualizar reserva: ${e.message}", e)
                _uiState.update { it.copy(erro = "Erro ao editar reserva: ${e.message}") }
            }
        }
    }

    fun alternarCheckIn(reserva: Reserva, novoStatus: Boolean = !reserva.checkInRealizado) {
        viewModelScope.launch {
            try {
                val reservaAtualizada = reserva.copy(checkInRealizado = novoStatus)
                reservaRepository.atualizarReserva(reservaAtualizada)
                if (_uiState.value.reservaSelecionada?.id == reserva.id) {
                    val recarregada = reservaRepository.buscarReservaPorId(reserva.id)
                    _uiState.update { it.copy(reservaSelecionada = recarregada) }
                }
            } catch (e: Exception) {
                Log.e("ReservaViewModel", "Erro ao alternar check-in: ${e.message}", e)
                _uiState.update { it.copy(erro = "Erro ao atualizar check-in: ${e.message}") }
            }
        }
    }

    fun removerReserva(reserva: Reserva, onConcluido: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                reservaRepository.deletarReserva(reserva)
                if (_uiState.value.reservaSelecionada?.id == reserva.id) {
                    _uiState.update {
                        it.copy(
                            reservaSelecionada = null,
                            vooDaReservaSelecionada = null
                        )
                    }
                }
                onConcluido()
            } catch (e: Exception) {
                Log.e("ReservaViewModel", "Erro ao remover reserva: ${e.message}", e)
                _uiState.update { it.copy(erro = "Erro ao cancelar reserva: ${e.message}") }
            }
        }
    }

    // --- Ações da TelaCartaoEmbarque (Busca por ID) ---

    fun carregarReservaPorId(bookingId: Int) {
        val mudouReserva = _uiState.value.idReservaCarregada != bookingId
        _uiState.update {
            it.copy(
                carregandoDetalhes = true,
                idReservaCarregada = bookingId,
                reservaSelecionada = if (mudouReserva) null else it.reservaSelecionada,
                vooDaReservaSelecionada = if (mudouReserva) null else it.vooDaReservaSelecionada,
                erro = null
            )
        }

        viewModelScope.launch {
            try {
                val reservaEncontrada = reservaRepository.buscarReservaPorId(bookingId)
                val vooRelacionado = reservaEncontrada?.let { vooRepository.buscarVooPorId(it.vooId) }
                _uiState.update {
                    it.copy(
                        reservaSelecionada = reservaEncontrada,
                        vooDaReservaSelecionada = vooRelacionado,
                        carregandoDetalhes = false
                    )
                }
            } catch (e: Exception) {
                Log.e("ReservaViewModel", "Erro ao buscar reserva por id ($bookingId): ${e.message}", e)
                _uiState.update {
                    it.copy(
                        reservaSelecionada = null,
                        vooDaReservaSelecionada = null,
                        carregandoDetalhes = false,
                        erro = "Erro ao carregar cartão de embarque: ${e.message}"
                    )
                }
            }
        }
    }
}
