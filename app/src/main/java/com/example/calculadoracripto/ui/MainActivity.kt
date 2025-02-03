package com.example.calculadoracripto

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calculadoracripto.R
import com.example.calculadoracripto.data.entities.Article
import com.example.calculadoracripto.ui.adapters.ArticleAdapter
import com.example.calculadoracripto.ui.viewmodels.ArticleViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val articleViewModel: ArticleViewModel by viewModels()
    private lateinit var articleAdapter: ArticleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configura el RecyclerView
        articleAdapter = ArticleAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = articleAdapter

        // Observa los cambios en la lista de artículos
        articleViewModel.allArticles.observe(this, Observer { articles ->
            articles?.let {
                articleAdapter.submitList(it)
            }
        })

        // Ejemplo de cómo agregar un artículo
        // Para prueba, agregamos un artículo
        //articleViewModel.insert(Article("Ref1", "Artículo de prueba", "Tipo A", 12.5f))
    }
}
