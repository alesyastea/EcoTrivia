package com.alesyastea.ecotrivia.ui.details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.webkit.URLUtil
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.alesyastea.ecotrivia.R
import com.alesyastea.ecotrivia.databinding.FragmentNewsDetailsBinding
import com.alesyastea.ecotrivia.utils.Constants.ARTICLE_URL
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NewsDetailsFragment : Fragment() {

    private var _binding: FragmentNewsDetailsBinding? = null
    private val mBinding get() = _binding!!
    private val bundleArgs: NewsDetailsFragmentArgs by navArgs()
    private val viewModel by viewModels<NewsDetailsViewModel>()
    private var isFavorite = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsDetailsBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val articleArg = bundleArgs.article

        mBinding.icFavorite.setOnClickListener {
            if (isFavorite) {
                viewModel.deleteArticle(articleArg)
            } else {
                viewModel.saveFavoriteArticle(articleArg)
            }
            isFavorite = !isFavorite
            updateFavoriteIcon()
        }

        viewModel.getSavedArticles().observe(viewLifecycleOwner) { savedArticles ->
            savedArticles?.let {
                val savedArticle = it.find { it.url == articleArg.url }
                isFavorite = savedArticle?.isFavorite ?: false
                updateFavoriteIcon()
            }
        }

        articleArg.let { article ->
            article.urlToImage.let {
                Glide.with(this).load(article.urlToImage)
                    .placeholder(R.drawable.news_placeholder)
                    .into(mBinding.headerImage)
            }
            mBinding.headerImage.clipToOutline = true
            mBinding.tvTitle.text = article.title
            mBinding.articleDescription.text = article.description

            mBinding.btnToSource.setOnClickListener {
                try {
                    Intent()
                        .setAction(Intent.ACTION_VIEW)
                        .addCategory(Intent.CATEGORY_BROWSABLE)
                        .setData(Uri.parse(takeIf { URLUtil.isValidUrl(article.url) }
                            ?.let {
                                article.url
                            } ?: ARTICLE_URL))
                        .let {
                            ContextCompat.startActivity(requireContext(), it, null)
                        }
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        R.string.no_browser,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        mBinding.icBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    private fun updateFavoriteIcon() {
        val iconRes = if (isFavorite) R.drawable.ic_favorite_added else R.drawable.ic_favorite_empty
        mBinding.icFavorite.setImageResource(iconRes)
    }
}