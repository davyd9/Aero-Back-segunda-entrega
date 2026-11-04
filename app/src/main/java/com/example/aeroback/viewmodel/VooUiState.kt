package com.example.aeroback.viewmodel

import com.example.aeroback.data.Voo

data class VooUiState(
    val voos: List<Voo> = emptyList(),
    val vooSelecionado: Voo? = null,
    val idVooCarregado: Int? = null,
    val isLoading: Boolean = false,
    val carregandoDetalhes: Boolean = false,
    val erro: String? = null,

    // Controle de diálogo e formulário de cadastro/edição de voo (TelaListaVoos)
    val exibirDialogNovoVoo: Boolean = false,
    val vooEmEdicao: Voo? = null,
    val companhiaInput: String = "",
    val origemInput: String = "",
    val destinoInput: String = "",
    val dataInput: String = "18 Nov 2026",
    val horarioInput: String = "14:20",
    val precoInput: String = "",
    val cashbackInput: String = "10",

    // Personalização em tempo real na tela de detalhes (TelaDetalhesVoo)
    val nomePassageiroDetalhes: String = "Davyd Molina",
    val addBagagemDetalhes: Boolean = false,
    val addAssentoVipDetalhes: Boolean = false,

    // Estado do formulário da tela inicial de busca (TelaBuscaVoos)
    val tripType: String = "Ida e Volta",
    val origemBusca: String = "Curitiba (CWB)",
    val destinoBusca: String = "Miami (MIA)",
    val dataIdaBusca: String = "15 Mar 2026",
    val dataVoltaBusca: String = "22 Mar 2026",
    val passageirosConfig: String = "1 Adulto, Econ\u00F4mica",
    val dialogDataAbertaPara: String? = null,
    val dialogPassageirosAberto: Boolean = false,

    // Estado dos filtros do Radar de Preços (TelaRadarPrecos)
    val radarPrecoMaximo: Float = 2500f,
    val radarCashbackMinimo: Float = 10f
) {
    val valorBagagemCalculado: Double
        get() = if (addBagagemDetalhes) 150.0 else 0.0

    val valorAssentoCalculado: Double
        get() = if (addAssentoVipDetalhes) 80.0 else 0.0

    val valorAdicionaisCalculado: Double
        get() = valorBagagemCalculado + valorAssentoCalculado

    val valorTotalCalculado: Double
        get() = (vooSelecionado?.precoBase ?: 0.0) + valorAdicionaisCalculado

    val cashbackTotalCalculado: Double
        get() = valorTotalCalculado * ((vooSelecionado?.cashbackPorcento ?: 0) / 100.0)
}
