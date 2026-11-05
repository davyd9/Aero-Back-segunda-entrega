package com.example.aeroback.data.repository

import android.content.SharedPreferences
import com.example.aeroback.data.Reserva
import com.example.aeroback.data.ReservaDao
import com.example.aeroback.data.Voo
import kotlinx.coroutines.flow.Flow

class ReservaRepository(
    private val reservaDao: ReservaDao,
    private val prefs: SharedPreferences? = null
) {

    fun buscarTodos(): Flow<List<Reserva>> = reservaDao.buscarTodos()

    fun listarReservas(): Flow<List<Reserva>> = buscarTodos()

    suspend fun buscarReservaPorId(id: Int): Reserva? = reservaDao.buscarPorId(id)

    suspend fun buscarReservaPorId(id: String): Reserva? = id.toIntOrNull()?.let { reservaDao.buscarPorId(it) }

    suspend fun contarReservas(): Int = reservaDao.contarReservas()

    suspend fun garantirSeedInicial(voosCadastrados: List<Voo>) {
        val jaExecutouSeed = prefs?.getBoolean(KEY_SEED_RESERVAS, false) ?: false
        if (jaExecutouSeed) return

        if (reservaDao.contarReservas() == 0 && voosCadastrados.isNotEmpty()) {
            val primeiroVooId = voosCadastrados.first().id
            val segundoVooId = voosCadastrados.getOrNull(1)?.id ?: primeiroVooId
            reservaDao.inserirLista(reservasPadrao(primeiroVooId, segundoVooId))
        }
        prefs?.edit()?.putBoolean(KEY_SEED_RESERVAS, true)?.apply()
    }

    suspend fun inserirReserva(reserva: Reserva) {
        reservaDao.inserir(reserva)
    }

    suspend fun inserirListaReservas(reservas: List<Reserva>) {
        reservaDao.inserirLista(reservas)
    }

    suspend fun atualizarReserva(reserva: Reserva) {
        reservaDao.atualizar(reserva)
    }

    suspend fun deletarReserva(reserva: Reserva) {
        reservaDao.deletar(reserva)
    }

    companion object {
        private const val KEY_SEED_RESERVAS = "seed_reservas_inicial_v1"

        fun reservasPadrao(primeiroVooId: Int, segundoVooId: Int): List<Reserva> = listOf(
            Reserva(
                vooId = primeiroVooId,
                passageiro = "Davyd Molina",
                assento = "03A (VIP)",
                bagagemDespachada = true,
                assentoVip = true,
                precoFinal = 3080.0,
                cashbackGanho = 369.60,
                checkInRealizado = true
            ),
            Reserva(
                vooId = segundoVooId,
                passageiro = "Guilherme Brunetti",
                assento = "14C",
                bagagemDespachada = false,
                assentoVip = false,
                precoFinal = 620.0,
                cashbackGanho = 93.00,
                checkInRealizado = false
            )
        )
    }
}
