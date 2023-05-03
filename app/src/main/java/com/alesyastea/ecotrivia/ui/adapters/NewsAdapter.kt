package com.alesyastea.ecotrivia.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.alesyastea.ecotrivia.databinding.ItemArticleBinding
import com.alesyastea.ecotrivia.models.NewsResponse
import com.bumptech.glide.Glide

class NewsAdapter: RecyclerView.Adapter<NewsAdapter.NewsViewHolder>()  {

    private lateinit var binding: ItemArticleBinding
    private lateinit var context: Context

    inner class NewsViewHolder: RecyclerView.ViewHolder(binding.root)

    private val callback = object : DiffUtil.ItemCallback<NewsResponse.Article>() {
        override fun areItemsTheSame(
            oldItem: NewsResponse.Article,
            newItem: NewsResponse.Article
        ): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(
            oldItem: NewsResponse.Article,
            newItem: NewsResponse.Article
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemArticleBinding.inflate(inflater, parent, false)
        context = parent.context
        return NewsViewHolder()
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(binding.articleImg)
            binding.articleImg.clipToOutline = true
            binding.articleTitle.text = article.title
            binding.articleDate.text = article.publishedAt

            setOnClickListener {
                onItemClickListener?.let { it(article) }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((NewsResponse.Article) ->  Unit)? = null

    fun setOnItemClickListener(listener: (NewsResponse.Article) -> Unit) {
        onItemClickListener = listener
    }
}