// ----------------------------------------------------------------------------
// Pedro Caso
// 241286
// Lab 8 plataformas
// ----------------------------------------------------------------------------

package com.example.lab8platadormas_pc

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RecentQueryDao {

    // Insertar o actualizar timestamp
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(query: RecentQueryEntity)

    // Obtener las busquedas mas recientes
    @Query("SELECT * FROM recent_queries ORDER BY lastUsedAt DESC LIMIT :limit")
    suspend fun getRecentQueries(limit: Int = 10): List<RecentQueryEntity>

    // Borrar una busqyeda del usuario
    @Query("DELETE FROM recent_queries WHERE `query` = :query")
    suspend fun deleteQuery(query: String)
}
