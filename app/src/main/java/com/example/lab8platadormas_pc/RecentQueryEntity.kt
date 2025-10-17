// ----------------------------------------------------------------------------
// Pedro Caso
// 241286
// Lab 8 plataformas
// ----------------------------------------------------------------------------

package com.example.lab8platadormas_pc

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_queries")
data class RecentQueryEntity(
    @PrimaryKey val query: String,
    val lastUsedAt: Long = System.currentTimeMillis()
)
