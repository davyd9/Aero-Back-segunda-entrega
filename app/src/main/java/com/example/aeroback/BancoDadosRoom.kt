package com.example.aeroback

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VooDao {
    @Query("SELECT * FROM voos ORDER BY origem ASC")
    fun listarVoos(): Flow<List<Voo>>

    @Query("SELECT * FROM voos WHERE id = :id LIMIT 1")
    suspend fun buscarVooPorId(id: String): Voo?

    @Query("SELECT COUNT(*) FROM voos")
    suspend fun contarVoos(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirVoo(voo: Voo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirListaVoos(voos: List<Voo>)

    @Update
    suspend fun atualizarVoo(voo: Voo)

    @Delete
    suspend fun deletarVoo(voo: Voo)
}

@Dao
interface ReservaDao {
    @Query("SELECT * FROM reservas ORDER BY id DESC")
    fun listarReservas(): Flow<List<Reserva>>

    @Query("SELECT * FROM reservas WHERE id = :id LIMIT 1")
    suspend fun buscarReservaPorId(id: String): Reserva?

    @Query("SELECT COUNT(*) FROM reservas")
    suspend fun contarReservas(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirReserva(reserva: Reserva)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirListaReservas(reservas: List<Reserva>)

    @Update
    suspend fun atualizarReserva(reserva: Reserva)

    @Delete
    suspend fun deletarReserva(reserva: Reserva)
}

@Database(
    entities = [Voo::class, Reserva::class],
    version = 1,
    exportSchema = false
)
abstract class AeroBackDatabase : RoomDatabase() {
    abstract fun vooDao(): VooDao
    abstract fun reservaDao(): ReservaDao

    companion object {
        @Volatile
        private var INSTANCE: AeroBackDatabase? = null

        fun getDatabase(context: Context): AeroBackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AeroBackDatabase::class.java,
                    "aeroback_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}