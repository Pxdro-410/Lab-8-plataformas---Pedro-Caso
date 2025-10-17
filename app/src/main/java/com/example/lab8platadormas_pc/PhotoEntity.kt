// ----------------------------------------------------------------------------
// Pedro Caso
// 241286
// Lab 8 plataformas
// ----------------------------------------------------------------------------

package com.example.lab8platadormas_pc

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class PhotoEntity(
    @PrimaryKey val id: Long,
    val photographer: String,
    val width: Int,
    val height: Int,
    val url: String,
    val thumbnailUrl: String?,
    val queryKey: String,
    val pageIndex: Int,
    val isFavorite: Boolean
)
