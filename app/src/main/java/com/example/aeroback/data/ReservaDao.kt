package com.example.aeroback.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservaDao {
    @Query("SELECT * FROM reservas ORDER BY id DESC")
    fun buscarTodos(): Flow<List<Reserva>>

    @Query("SELECT * FROM reservas WHERE id = :id")
    suspend fun buscarPorId(id: Int): Reserva?

    @Query("SELECT COUNT(*) FROM reservas")
    suspend fun contarReservas(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(reserva: Reserva)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirLista(reservas: List<Reserva>)

    @Update
    suspend fun atualizar(reserva: Reserva)

    @Delete
    suspend fun deletar(reserva: Reserva)
}
