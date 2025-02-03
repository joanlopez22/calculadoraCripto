package com.example.calculadoracripto.data


class ArticleRepository(private val articleDao: ArticleDao) {

    fun getAllArticles() = articleDao.getAllArticles()

    fun filterArticlesByDescription(filter: String) = articleDao.filterArticlesByDescription(filter)

    fun filterArticlesByType(type: String) = articleDao.filterArticlesByType(type)

    fun insertArticle(article: Article) = articleDao.insertArticle(article)

    fun updateArticle(article: Article) = articleDao.updateArticle(article)

    fun deleteArticle(article: Article) = articleDao.deleteArticle(article)
}
