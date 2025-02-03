package com.example.calculadoracripto.ui.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.calculadoracripto.R
import com.example.calculadoracripto.data.entities.Article
import kotlinx.calculadoracripto.synthetic.main.item_article.view.*

class ArticleAdapter : ListAdapter<Article, ArticleAdapter.ArticleViewHolder>(ArticleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = getItem(position)
        holder.bind(article)
    }

    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(article: Article) {
            itemView.tvReferencia.text = article.referencia
            itemView.tvDescripcion.text = article.descripcion
            itemView.tvTipo.text = article.tipo
            itemView.tvPrecio.text = "${article.precioSinIva}€"
        }
    }

    class ArticleDiffCallback : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.referencia == newItem.referencia
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }
}
