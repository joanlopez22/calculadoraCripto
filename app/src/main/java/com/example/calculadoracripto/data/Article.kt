package com.example.calculadoracripto.data



import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class Article(
    @PrimaryKey
    val referencia: String,
    val descripcio: String,
    val tipus: String,
    val preuSenseIva: Float
)
