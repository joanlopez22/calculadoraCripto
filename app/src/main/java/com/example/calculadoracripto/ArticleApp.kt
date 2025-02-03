package com.example.calculadoracripto


import android.app.Application
import androidx.room.Room
import com.example.calculadoracripto.data.AppDatabase

class ArticleApp : Application() {

    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()
        // Inicialitza la base de dades Room
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "article_database"  // Nom de la base de dades (pot ser el mateix)
        ).build()
    }
}
