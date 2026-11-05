package com.example.aeroback.data.repository

import android.content.SharedPreferences
import com.example.aeroback.data.Voo
import com.example.aeroback.data.VooDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex

class VooRepository(
    private val vooDao: VooDao,
    private val prefs: SharedPreferences? = null
) {

    fun buscarTodos(): Flow<List<Voo>> = vooDao.buscarTodos()

    fun listarVoos(): Flow<List<Voo>> = buscarTodos()

    suspend fun buscarVooPorId(id: Int): Voo? = vooDao.buscarPorId(id)

    suspend fun buscarVooPorId(id: String): Voo? = id.toIntOrNull()?.let { vooDao.buscarPorId(it) }

    suspend fun contarVoos(): Int = vooDao.contarVoos()

    suspend fun garantirSeedInicial() {
        val jaExecutouSeed = prefs?.getBoolean(KEY_SEED_VOOS, false) ?: false
        if (jaExecutouSeed) return

        if (vooDao.contarVoos() == 0) {
            vooDao.inserirLista(voosPadrao())
        }
        prefs?.edit()?.putBoolean(KEY_SEED_VOOS, true)?.apply()
    }

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
        const val PREFS_NAME = "aeroback_room_prefs"
        private const val KEY_SEED_VOOS = "seed_voos_inicial_v1"

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
