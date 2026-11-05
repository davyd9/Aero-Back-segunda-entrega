package com.example.aeroback

import com.example.aeroback.data.Reserva
import com.example.aeroback.data.ReservaDao
import com.example.aeroback.data.Voo
import com.example.aeroback.data.VooDao
import com.example.aeroback.data.repository.ReservaRepository
import com.example.aeroback.data.repository.VooRepository
import com.example.aeroback.viewmodel.ReservaUiState
import com.example.aeroback.viewmodel.VooUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun calculoTempoRealVooUiState_estaCorreto() {
        val voo = Voo(
            id = 1,
            companhia = "LATAM Airlines",
            origem = "GRU",
            destino = "MIA",
            dataIda = "15 Mar 2026",
            horario = "22:45",
            precoBase = 2850.0,
            cashbackPorcento = 12
        )
        val state = VooUiState(
            vooSelecionado = voo,
            addBagagemDetalhes = true,
            addAssentoVipDetalhes = true
        )

        assertEquals(230.0, state.valorAdicionaisCalculado, 0.001)
        assertEquals(3080.0, state.valorTotalCalculado, 0.001)
        assertEquals(369.60, state.cashbackTotalCalculado, 0.001)
    }

    @Test
    fun saldosReservaUiState_separamConfirmadoEPendente() {
        val r1 = Reserva(1, 1, "Davyd Molina", "03A", true, true, 3080.0, 369.60, checkInRealizado = true)
        val r2 = Reserva(2, 2, "Guilherme Brunetti", "14C", false, false, 620.0, 93.00, checkInRealizado = false)

        val state = ReservaUiState(reservas = listOf(r1, r2))

        assertEquals(462.60, state.saldoTotalAcumulado, 0.001)
        assertEquals(369.60, state.saldoConfirmado, 0.001)
        assertEquals(93.00, state.saldoPendente, 0.001)
    }

    @Test
    fun vooRepositoryEFactory_funcionamComFlowReativo() = runBlocking {
        val fakeDao = object : VooDao {
            private val state = MutableStateFlow<List<Voo>>(emptyList())
            override fun buscarTodos(): Flow<List<Voo>> = state
            override suspend fun buscarPorId(id: Int): Voo? = state.value.find { it.id == id }
            override suspend fun contarVoos(): Int = state.value.size
            override suspend fun inserir(voo: Voo) {
                state.value = state.value + voo.copy(id = state.value.size + 1)
            }
            override suspend fun inserirLista(voos: List<Voo>) {
                state.value = voos.mapIndexed { idx, v -> v.copy(id = idx + 1) }
            }
            override suspend fun atualizar(voo: Voo) {
                state.value = state.value.map { if (it.id == voo.id) voo else it }
            }
            override suspend fun deletar(voo: Voo) {
                state.value = state.value.filterNot { it.id == voo.id }
            }
        }

        val repository = VooRepository(fakeDao)
        repository.garantirSeedInicial()
        assertEquals(3, repository.buscarTodos().first().size)

        val fakeReservaDao = object : ReservaDao {
            private val state = MutableStateFlow<List<Reserva>>(emptyList())
            override fun buscarTodos(): Flow<List<Reserva>> = state
            override suspend fun buscarPorId(id: Int): Reserva? = state.value.find { it.id == id }
            override suspend fun contarReservas(): Int = state.value.size
            override suspend fun inserir(reserva: Reserva) {
                state.value = state.value + reserva.copy(id = state.value.size + 1)
            }
            override suspend fun inserirLista(reservas: List<Reserva>) {
                state.value = reservas.mapIndexed { idx, r -> r.copy(id = idx + 1) }
            }
            override suspend fun atualizar(reserva: Reserva) {
                state.value = state.value.map { if (it.id == reserva.id) reserva else it }
            }
            override suspend fun deletar(reserva: Reserva) {
                state.value = state.value.filterNot { it.id == reserva.id }
            }
        }

        val reservaRepo = ReservaRepository(fakeReservaDao)
        reservaRepo.garantirSeedInicial(repository.buscarTodos().first())
        val reservas = reservaRepo.buscarTodos().first()
        assertEquals(2, reservas.size)
        assertTrue(reservas.any { it.checkInRealizado })
    }
}