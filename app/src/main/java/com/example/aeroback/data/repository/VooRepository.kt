package com.example.aeroback.data.repository

import com.example.aeroback.data.Voo
import com.example.aeroback.data.VooDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex

class VooRepository(private val vooDao: VooDao) {

    fun listarVoos(): Flow<List<Voo>> = vooDao.listarVoos()

    fun buscarTodos(): Flow<List<Voo>> = vooDao.buscarTodos()

    suspend fun buscarVooPorId(id: Int): Voo? = vooDao.buscarPorId(id)

    suspend fun buscarVooPorId(id: String): Voo? = id.toIntOrNull()?.let { vooDao.buscarPorId(it) }

    suspend fun contarVoos(): Int = vooDao.contarVoos()

    suspend fun inserirVoo(voo: Voo) {
        vooDao.inserir(voo)
    }

    suspend fun inserirListaVoos(voos: List<Voo>) {
        vooDao.inserirLista(voos)
    }

    suspend fun atualizarVoo(voo: Voo) {
        vooDao.atualizar(voo)
    }

    suspend fun deletarVoo(voo: Voo) {
        vooDao.deletar(voo)
    }

    companion object {
        val seedMutex = Mutex()

        fun voosPadrao(): List<Voo> = listOf(
            Voo(
                companhia = "LATAM Airlines",
                origem = "GRU",
                destino = "MIA",
                dataIda = "15 Mar 2026",
                horario = "22:45",
                precoBase = 2850.0,
                cashbackPorcento = 12
            ),
            Voo(
                companhia = "Gol Linhas A\u00E9reas",
                origem = "CWB",
                destino = "GIG",
                dataIda = "20 Mar 2026",
                horario = "09:15",
                precoBase = 620.0,
                cashbackPorcento = 15
            ),
            Voo(
                companhia = "Azul Linhas A\u00E9reas",
                origem = "CWB",
                destino = "GRU",
                dataIda = "25 Mar 2026",
                horario = "14:30",
                precoBase = 490.0,
                cashbackPorcento = 10
            )
        )
    }
}
