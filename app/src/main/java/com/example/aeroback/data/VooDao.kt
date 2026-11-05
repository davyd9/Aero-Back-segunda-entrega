package com.example.aeroback.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VooDao {
    @Query("SELECT * FROM voos ORDER BY id ASC")
    fun buscarTodos(): Flow<List<Voo>>

    @Query("SELECT * FROM voos WHERE id = :id")
    suspend fun buscarPorId(id: Int): Voo?

    @Query("SELECT COUNT(*) FROM voos")
    suspend fun contarVoos(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(voo: Voo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirLista(voos: List<Voo>)

    @Update
    suspend fun atualizar(voo: Voo)

    @Delete
    suspend fun deletar(voo: Voo)
}
