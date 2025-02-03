package com.example.calculadoracripto.data

import androidx.room.*

@Dao
interface ArticleDao {

    @Query("SELECT * FROM articles")
    fun getAllArticles(): List<Article>

    @Query("SELECT * FROM articles WHERE descripcio LIKE '%' || :filter || '%'")
    fun filterArticlesByDescription(filter: String): List<Article>

    @Query("SELECT * FROM articles WHERE tipus = :type")
    fun filterArticlesByType(type: String): List<Article>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArticle(article: Article)

    @Update
    fun updateArticle(article: Article)

    @Delete
    fun deleteArticle(article: Article)
}
