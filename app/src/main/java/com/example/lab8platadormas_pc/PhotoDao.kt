// ----------------------------------------------------------------------------
// Pedro Caso
// 241286
// Lab 8 plataformas
// ----------------------------------------------------------------------------

package com.example.lab8platadormas_pc

import androidx.room.*

@Dao
interface PhotoDao {

    // Guardar fotos
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(photos: List<PhotoEntity>)

    // Obtener fotos por query
    @Query("SELECT * FROM photos WHERE queryKey = :query ORDER BY pageIndex ASC")
    suspend fun getPhotosByQuery(query: String): List<PhotoEntity>

    // Obtener una foto por id
    @Query("SELECT * FROM photos WHERE id = :photoId LIMIT 1")
    suspend fun getPhotoById(photoId: Long): PhotoEntity?

    // Actualizar estado de favorito
    @Query("UPDATE photos SET isFavorite = :favorite WHERE id = :photoId")
    suspend fun updateFavorite(photoId: Long, favorite: Boolean)

    // Obtener los favosritos
    @Query("SELECT * FROM photos WHERE isFavorite = 1")
    suspend fun getAllFavorites(): List<PhotoEntity>

    // Borrar cache
    @Query("DELETE FROM photos WHERE queryKey = :query")
    suspend fun clearQuery(query: String)

    // Obtener gavoritos
    @Query("SELECT * FROM photos WHERE isFavorite = 1 ORDER BY id DESC")
    suspend fun getFavorites(): List<PhotoEntity>

}
