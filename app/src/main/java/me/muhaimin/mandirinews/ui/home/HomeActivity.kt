package me.muhaimin.mandirinews.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import me.muhaimin.mandirinews.R
import me.muhaimin.mandirinews.data.remote.response.Article
import me.muhaimin.mandirinews.databinding.ActivityHomeBinding
import me.muhaimin.mandirinews.ui.detail.DetailActivity

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // top headline news
        viewModel.topHeadlineNews.observe(this) { article ->
            if (article.urlToImage == null) {
                binding.ivTopHeadlineNews.setImageDrawable(
                    ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.baseline_image_not_supported_24
                    )
                )
                binding.ivTopHeadlineNews.scaleType = ImageView.ScaleType.CENTER
            } else {
                Glide.with(binding.root)
                    .load(article.urlToImage)
                    .into(binding.ivTopHeadlineNews)
            }

            binding.tvTitleTopHeadlineNews.text = article.title
            binding.tvSourceNameTopHeadlineNews.text = article.source?.name
            binding.tvAuthorTopHeadlineNews.text = "- ${article.author}"
            binding.tvPublishedAtTopHeadlineNews.text = article.publishedAt

            binding.layoutTopHeadlineNews.setOnClickListener {
                toDetailActivity(article.title.toString(), article.url.toString())
            }

        }

        // headline news
        setupHeadlineNews()
        viewModel.listHeadlineNews.observe(this) { listArticle ->
            val adapter = ListHeadlineNewsAdapter()
            binding.rvHeadlineNews.adapter = adapter
            adapter.submitList(listArticle)

            adapter.setOnItemClickCallback(object : ListHeadlineNewsAdapter.OnItemClickCallback {
                override fun onItemClicked(article: Article) {
                    toDetailActivity(article.title.toString(), article.url.toString())
                }
            })
        }

        // everything news
        setupEverythingNews()
        viewModel.listEverythingNews.observe(this) { listArticle ->
            val adapter = ListEverythingNewsAdapter()
            adapter.submitList(listArticle)
            binding.rvEverythingNews.adapter = adapter

            adapter.setOnItemClickCallback(object : ListEverythingNewsAdapter.OnItemClickCallback {
                override fun onItemClicked(article: Article) {
                    toDetailActivity(article.title.toString(), article.url.toString())
                }

            })
        }

        // progressbar
        viewModel.isLoading.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        // when error to fetch data
        viewModel.errorMessage.observe(this) {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT)
                .setTextColor(getColor(android.R.color.holo_red_light))
                .show()
        }

        // get news
        viewModel.getNews()
    }

    private fun setupEverythingNews() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvEverythingNews.layoutManager = layoutManager

        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvEverythingNews.addItemDecoration(itemDecoration)

        // endless scroll
        binding.rvEverythingNews.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (recyclerView.canScrollVertically(1)) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (layoutManager.itemCount <= layoutManager.findLastVisibleItemPosition() + 2) {
                        viewModel.increasePageEverythingNews()
                    }
                }
            }
        })
    }

    private fun setupHeadlineNews() {
        val layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }
        binding.rvHeadlineNews.layoutManager = layoutManager

        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvHeadlineNews.addItemDecoration(itemDecoration)

        // endless scroll
        binding.rvEverythingNews.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (recyclerView.canScrollVertically(1)) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (layoutManager.itemCount <= layoutManager.findLastVisibleItemPosition() + 2) {
                        viewModel.increasePageHeadlineNews()
                    }
                }
            }
        })

    }

    private fun toDetailActivity(title: String, url: String) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_TITLE, title)
        intent.putExtra(DetailActivity.EXTRA_URL, url)

        startActivity(intent)
    }
}