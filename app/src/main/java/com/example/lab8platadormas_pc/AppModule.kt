// ----------------------------------------------------------------------------
// Pedro Caso
// 241286
// Lab 8 plataformas
// ----------------------------------------------------------------------------

package com.example.lab8platadormas_pc

import android.content.Context
import androidx.room.Room

object AppModule {
    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "gallery_db"
        ).build()
    }

    fun providePhotoDao(db: AppDatabase) = db.photoDao()
    fun provideRecentQueryDao(db: AppDatabase) = db.recentQueryDao()
}