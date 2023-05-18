package com.alesyastea.ecotrivia.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.alesyastea.ecotrivia.R
import com.alesyastea.ecotrivia.databinding.ItemQuizBinding
import com.alesyastea.ecotrivia.models.QuizModel
import com.bumptech.glide.Glide

class QuizAdapter: RecyclerView.Adapter<QuizAdapter.QuizViewHolder>() {
    private lateinit var binding: ItemQuizBinding
    private lateinit var context: Context

    inner class QuizViewHolder : RecyclerView.ViewHolder(binding.root)

    private val callback = object : DiffUtil.ItemCallback<QuizModel>() {
        override fun areItemsTheSame(
            oldItem: QuizModel,
            newItem: QuizModel
        ): Boolean {
            return oldItem.quizname == newItem.quizname
        }

        override fun areContentsTheSame(
            oldItem: QuizModel,
            newItem: QuizModel
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemQuizBinding.inflate(inflater, parent, false)
        context = parent.context
        return QuizViewHolder()
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        val quiz = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(quiz.image).placeholder(R.drawable.news_placeholder)
                .into(binding.quizImg)
            binding.quizImg.clipToOutline = true
            binding.quizName.text = quiz.quizname
            binding.quizDescription.text = quiz.description
            binding.quizLevel.text = quiz.level

            setOnClickListener {
                onItemClickListener?.let { it(quiz) }
            }

            binding.btnTakeQuiz.setOnClickListener {
                onTakeQuizClickListener?.let { it(quiz) }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((QuizModel) -> Unit)? = null

    fun setOnItemClickListener(listener: (QuizModel) -> Unit) {
        onItemClickListener = listener
    }

    private var onTakeQuizClickListener: ((QuizModel) -> Unit)? = null

    fun setOnTakeQuizClickListener(listener: (QuizModel) -> Unit) {
        onTakeQuizClickListener = listener
    }
}