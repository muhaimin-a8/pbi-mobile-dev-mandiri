package me.muhaimin.mandirinews.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import me.muhaimin.mandirinews.R
import me.muhaimin.mandirinews.data.remote.response.Article
import me.muhaimin.mandirinews.databinding.ItemHeadlineNewsBinding

class ListHeadlineNewsAdapter :
    ListAdapter<Article, ListHeadlineNewsAdapter.ViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemHeadlineNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(getItem(position))
        }
    }

    class ViewHolder(private val binding: ItemHeadlineNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(news: Article) {
            if (news.urlToImage == null) {
                binding.iwHeadline.setImageDrawable(
                    ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.baseline_image_not_supported_24
                    )
                )
                binding.iwHeadline.scaleType = ImageView.ScaleType.CENTER
            } else {
                Glide.with(binding.root)
                    .load(news.urlToImage)
                    .into(binding.iwHeadline)
            }

            binding.tvTitle.text = news.title
        }
    }


    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean =
                oldItem == newItem
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(article: Article)
    }
}