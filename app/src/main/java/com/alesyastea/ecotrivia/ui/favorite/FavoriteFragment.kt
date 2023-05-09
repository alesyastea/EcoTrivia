package com.alesyastea.ecotrivia.ui.favorite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alesyastea.ecotrivia.R
import com.alesyastea.ecotrivia.databinding.FragmentFavoriteBinding
import com.alesyastea.ecotrivia.ui.adapters.NewsAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val mBinding get() = _binding!!
    private val viewModel by viewModels<FavoriteViewModel>()
    lateinit var newsAdapter: NewsAdapter
    private val firebaseAnalytics by lazy { FirebaseAnalytics.getInstance(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        firebaseAnalytics.logEvent("favorite_fragment_opened", null)


        newsAdapter.setOnItemClickListener {
            val bundleFB = Bundle()
            bundleFB.putString(FirebaseAnalytics.Param.ITEM_NAME, it.title)
            firebaseAnalytics.logEvent("article_clicked", bundleFB)

            val bundle = bundleOf("article" to it)
            view.findNavController().navigate(
                R.id.action_favoriteFragment_to_newsDetailsFragment,
                bundle
            )
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback (
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                viewModel.deleteArticle(article)
                Snackbar.make(view, getString(R.string.delete_article), Snackbar.LENGTH_SHORT).apply {
                    setAction(getString(R.string.undo)) {
                        viewModel.saveFavoriteArticle(article)
                    }
                    show()
                }
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, article.title)
                firebaseAnalytics.logEvent("article_deleted", bundle)
            }

        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(mBinding.recyclerView)
        }


        viewModel.getSavedArticles().observe(viewLifecycleOwner, Observer{ articles ->
            newsAdapter.differ.submitList(articles)
        })

    }

    private fun initAdapter() {
        newsAdapter = NewsAdapter()
        mBinding.recyclerView.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

}