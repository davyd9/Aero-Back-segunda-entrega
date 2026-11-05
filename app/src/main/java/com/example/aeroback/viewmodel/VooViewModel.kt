package com.example.aeroback.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aeroback.data.Voo
import com.example.aeroback.data.repository.VooRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.withLock

class VooViewModel(
    private val repository: VooRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VooUiState())
    val uiState: StateFlow<VooUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, erro = null) }
            try {
                VooRepository.seedMutex.withLock {
                    repository.garantirSeedInicial()
                }
                repository.buscarTodos().collect { listaVoos ->
                    _uiState.update { estadoAtual ->
                        estadoAtual.copy(
                            voos = listaVoos,
                            isLoading = false,
                            erro = null
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("VooViewModel", "Erro ao inicializar e coletar voos: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        erro = "Erro ao carregar voos: ${e.message}"
                    )
                }
            }
        }
    }

    // --- Ações da TelaListaVoos (CRUD de Voo) ---

    fun abrirDialogNovoVoo() {
        _uiState.update {
            it.copy(
                companhiaInput = "",
                origemInput = "",
                destinoInput = "",
                dataInput = "18 Nov 2026",
                horarioInput = "14:20",
                precoInput = "",
                cashbackInput = "10",
                exibirDialogNovoVoo = true,
                vooEmEdicao = null
            )
        }
    }

    fun fecharDialogNovoVoo() {
        _uiState.update { it.copy(exibirDialogNovoVoo = false) }
    }

    fun iniciarEdicaoVoo(voo: Voo) {
        _uiState.update {
            it.copy(
                companhiaInput = voo.companhia,
                origemInput = voo.origem,
                destinoInput = voo.destino,
                dataInput = voo.dataIda,
                horarioInput = voo.horario,
                precoInput = voo.precoBase.toString(),
                cashbackInput = voo.cashbackPorcento.toString(),
                vooEmEdicao = voo,
                exibirDialogNovoVoo = false
            )
        }
    }

    fun cancelarEdicaoVoo() {
        _uiState.update { it.copy(vooEmEdicao = null) }
    }

    fun atualizarCompanhiaInput(valor: String) {
        _uiState.update { it.copy(companhiaInput = valor) }
    }

    fun atualizarOrigemInput(valor: String) {
        _uiState.update { it.copy(origemInput = valor) }
    }

    fun atualizarDestinoInput(valor: String) {
        _uiState.update { it.copy(destinoInput = valor) }
    }

    fun atualizarDataInput(valor: String) {
        _uiState.update { it.copy(dataInput = valor) }
    }

    fun atualizarHorarioInput(valor: String) {
        _uiState.update { it.copy(horarioInput = valor) }
    }

    fun atualizarPrecoInput(valor: String) {
        _uiState.update { it.copy(precoInput = valor) }
    }

    fun atualizarCashbackInput(valor: String) {
        _uiState.update { it.copy(cashbackInput = valor) }
    }

    fun adicionarVoo() {
        val estado = _uiState.value
        if (estado.origemInput.isBlank() || estado.destinoInput.isBlank()) return

        val preco = estado.precoInput.replace(",", ".").toDoubleOrNull() ?: 1200.0
        val cashback = estado.cashbackInput.toIntOrNull() ?: 10
        val novoVoo = Voo(
            companhia = estado.companhiaInput.ifBlank { "AeroBack Linhas" },
            origem = estado.origemInput.uppercase(),
            destino = estado.destinoInput.uppercase(),
            dataIda = estado.dataInput.ifBlank { "18 Nov 2026" },
            horario = estado.horarioInput.ifBlank { "14:20" },
            precoBase = preco,
            cashbackPorcento = cashback
        )

        _uiState.update { it.copy(exibirDialogNovoVoo = false) }

        viewModelScope.launch {
            try {
                repository.inserirVoo(novoVoo)
            } catch (e: Exception) {
                Log.e("VooViewModel", "Erro ao inserir voo: ${e.message}", e)
                _uiState.update { it.copy(erro = "Erro ao inserir voo: ${e.message}") }
            }
        }
    }

    fun salvarEdicaoVoo() {
        val estado = _uiState.value
        val vooAtual = estado.vooEmEdicao ?: return

        val preco = estado.precoInput.replace(",", ".").toDoubleOrNull() ?: vooAtual.precoBase
        val cashback = estado.cashbackInput.toIntOrNull() ?: vooAtual.cashbackPorcento
        val vooEditado = vooAtual.copy(
            companhia = estado.companhiaInput.ifBlank { vooAtual.companhia },
            origem = estado.origemInput.uppercase().ifBlank { vooAtual.origem },
            destino = estado.destinoInput.uppercase().ifBlank { vooAtual.destino },
            dataIda = estado.dataInput.ifBlank { vooAtual.dataIda },
            horario = estado.horarioInput.ifBlank { vooAtual.horario },
            precoBase = preco,
            cashbackPorcento = cashback
        )

        _uiState.update { it.copy(vooEmEdicao = null) }

        viewModelScope.launch {
            try {
                repository.atualizarVoo(vooEditado)
            } catch (e: Exception) {
                Log.e("VooViewModel", "Erro ao atualizar voo: ${e.message}", e)
                _uiState.update { it.copy(erro = "Erro ao atualizar voo: ${e.message}") }
            }
        }
    }

    fun removerVoo(voo: Voo) {
        viewModelScope.launch {
            try {
                repository.deletarVoo(voo)
            } catch (e: Exception) {
                Log.e("VooViewModel", "Erro ao deletar voo: ${e.message}", e)
                _uiState.update { it.copy(erro = "Erro ao remover voo: ${e.message}") }
            }
        }
    }

    // --- Ações da TelaDetalhesVoo (Busca por ID e Opcionais em Tempo Real) ---

    fun carregarVooPorId(flightId: Int) {
        val mudouVoo = _uiState.value.idVooCarregado != flightId
        _uiState.update {
            it.copy(
                carregandoDetalhes = true,
                idVooCarregado = flightId,
                vooSelecionado = if (mudouVoo) null else it.vooSelecionado,
                nomePassageiroDetalhes = if (mudouVoo) "Davyd Molina" else it.nomePassageiroDetalhes,
                addBagagemDetalhes = if (mudouVoo) false else it.addBagagemDetalhes,
                addAssentoVipDetalhes = if (mudouVoo) false else it.addAssentoVipDetalhes,
                erro = null
            )
        }

        viewModelScope.launch {
            try {
                val vooEncontrado = repository.buscarVooPorId(flightId)
                _uiState.update {
                    it.copy(
                        vooSelecionado = vooEncontrado,
                        carregandoDetalhes = false
                    )
                }
            } catch (e: Exception) {
                Log.e("VooViewModel", "Erro ao buscar voo por id ($flightId): ${e.message}", e)
                _uiState.update {
                    it.copy(
                        vooSelecionado = null,
                        carregandoDetalhes = false,
                        erro = "Erro ao buscar voo: ${e.message}"
                    )
                }
            }
        }
    }

    fun atualizarNomePassageiroDetalhes(nome: String) {
        _uiState.update { it.copy(nomePassageiroDetalhes = nome) }
    }

    fun alternarBagagemDetalhes(selecionado: Boolean) {
        _uiState.update { it.copy(addBagagemDetalhes = selecionado) }
    }

    fun alternarAssentoVipDetalhes(selecionado: Boolean) {
        _uiState.update { it.copy(addAssentoVipDetalhes = selecionado) }
    }

    fun resetarPersonalizacaoDetalhes() {
        _uiState.update {
            it.copy(
                nomePassageiroDetalhes = "Davyd Molina",
                addBagagemDetalhes = false,
                addAssentoVipDetalhes = false
            )
        }
    }

    // --- Ações da TelaBuscaVoos ---

    fun selecionarTipoViagem(tipo: String) {
        _uiState.update { it.copy(tripType = tipo) }
    }

    fun inverterOrigemDestinoBusca() {
        _uiState.update {
            it.copy(
                origemBusca = it.destinoBusca,
                destinoBusca = it.origemBusca
            )
        }
    }

    fun abrirDialogDataBusca(tipoData: String?) {
        _uiState.update { it.copy(dialogDataAbertaPara = tipoData) }
    }

    fun selecionarDataBusca(tipoData: String, dataEscolhida: String) {
        _uiState.update {
            if (tipoData == "Ida") {
                it.copy(dataIdaBusca = dataEscolhida, dialogDataAbertaPara = null)
            } else {
                it.copy(dataVoltaBusca = dataEscolhida, dialogDataAbertaPara = null)
            }
        }
    }

    fun definirDialogPassageirosAberto(aberto: Boolean) {
        _uiState.update { it.copy(dialogPassageirosAberto = aberto) }
    }

    fun selecionarPassageirosConfig(config: String) {
        _uiState.update {
            it.copy(
                passageirosConfig = config,
                dialogPassageirosAberto = false
            )
        }
    }

    // --- Ações da TelaRadarPrecos ---

    fun atualizarRadarPrecoMaximo(valor: Float) {
        _uiState.update { it.copy(radarPrecoMaximo = valor) }
    }

    fun atualizarRadarCashbackMinimo(valor: Float) {
        _uiState.update { it.copy(radarCashbackMinimo = valor) }
    }
}
