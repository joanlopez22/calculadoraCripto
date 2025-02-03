package com.example.calculadoracripto.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calculadoracripto.data.Article
import com.example.calculadoracripto.data.ArticleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppViewModel(private val repository: ArticleRepository) : ViewModel() {

    val articles = repository.getAllArticles()

    fun addArticle(article: Article) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertArticle(article)
        }
    }

    fun deleteArticle(article: Article) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteArticle(article)
        }
    }

    fun updateArticle(article: Article) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateArticle(article)
        }
    }
}
