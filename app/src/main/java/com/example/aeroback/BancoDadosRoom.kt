package com.example.aeroback

import android.content.Context
import android.util.Log
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Dao
interface VooDao {
    @Insert
    suspend fun inserir(voo: Voo)

    @Query("SELECT * FROM voos ORDER BY id ASC")
    suspend fun buscarTodos(): List<Voo>

    @Query("SELECT * FROM voos WHERE id = :id")
    suspend fun buscarPorId(id: Int): Voo?

    @Update
    suspend fun atualizar(voo: Voo)

    @Delete
    suspend fun deletar(voo: Voo)
}

@Dao
interface ReservaDao {
    @Insert
    suspend fun inserir(reserva: Reserva)

    @Query("SELECT * FROM reservas ORDER BY id DESC")
    suspend fun buscarTodos(): List<Reserva>

    @Query("SELECT * FROM reservas WHERE id = :id")
    suspend fun buscarPorId(id: Int): Reserva?

    @Update
    suspend fun atualizar(reserva: Reserva)

    @Delete
    suspend fun deletar(reserva: Reserva)
}

@Database(
    entities = [Voo::class, Reserva::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vooDao(): VooDao
    abstract fun reservaDao(): ReservaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val seedMutex = Mutex()

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "aeroback_room_v1.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun garantirDadosIniciais(context: Context, db: AppDatabase) {
            seedMutex.withLock {
                val prefs = context.applicationContext.getSharedPreferences(
                    "aeroback_room_prefs",
                    Context.MODE_PRIVATE
                )
                if (prefs.getBoolean("seed_inicial_v1", false)) {
                    return
                }
                try {
                    val vooDao = db.vooDao()
                    val reservaDao = db.reservaDao()

                    if (vooDao.buscarTodos().isEmpty()) {
                        vooDao.inserir(
                            Voo(
                                companhia = "LATAM Airlines",
                                origem = "GRU",
                                destino = "MIA",
                                dataIda = "15 Mar 2026",
                                horario = "22:45",
                                precoBase = 2850.0,
                                cashbackPorcento = 12
                            )
                        )
                        vooDao.inserir(
                            Voo(
                                companhia = "Gol Linhas A\u00E9reas",
                                origem = "CWB",
                                destino = "GIG",
                                dataIda = "20 Mar 2026",
                                horario = "09:15",
                                precoBase = 620.0,
                                cashbackPorcento = 15
                            )
                        )
                        vooDao.inserir(
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

                    val voosCadastrados = vooDao.buscarTodos()
                    if (reservaDao.buscarTodos().isEmpty() && voosCadastrados.isNotEmpty()) {
                        val primeiroVooId = voosCadastrados.first().id
                        val segundoVooId = voosCadastrados.getOrNull(1)?.id ?: primeiroVooId

                        reservaDao.inserir(
                            Reserva(
                                vooId = primeiroVooId,
                                passageiro = "Davyd Molina",
                                assento = "03A (VIP)",
                                bagagemDespachada = true,
                                assentoVip = true,
                                precoFinal = 3080.0,
                                cashbackGanho = 369.60,
                                checkInRealizado = true
                            )
                        )
                        reservaDao.inserir(
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

                    prefs.edit().putBoolean("seed_inicial_v1", true).apply()
                } catch (e: Exception) {
                    Log.e("AppDatabase", "Erro ao popular dados iniciais no Room: ${e.message}", e)
                }
            }
        }
    }
}
