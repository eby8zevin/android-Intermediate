package com.ahmadabuhasan.storyapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ahmadabuhasan.storyapp.databinding.ItemListBinding
import com.ahmadabuhasan.storyapp.model.ListStory
import com.ahmadabuhasan.storyapp.ui.DetailStoryActivity
import com.ahmadabuhasan.storyapp.utils.Constant
import com.bumptech.glide.Glide

class StoryAdapter :
    PagingDataAdapter<ListStory, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStory>() {
            override fun areItemsTheSame(oldItem: ListStory, newItem: ListStory): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStory, newItem: ListStory): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StoryViewHolder {
        val binding = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val storyData = getItem(position)
        if (storyData != null) {
            holder.bind(story = storyData)
        }
    }

    class StoryViewHolder(private val binding: ItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStory) {
            with(binding) {

                val name = story.name
                tvTitle.text = "Nama: $name"

                val desc = story.description
                tvDesc.text = "Desc: $desc"

                tvDate.text = story.createdAt.timeStamp()

                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .centerCrop()
                    .skipMemoryCache(true)
                    .into(ivPhoto)
            }

            itemView.setOnClickListener {
                val intent = Intent(it.context, DetailStoryActivity::class.java)
                intent.putExtra(Constant.BUNDLE_STORY, story)

                val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    itemView.context as Activity,
                    Pair(binding.ivPhoto, "photo"),
                    Pair(binding.tvTitle, "title"),
                    Pair(binding.tvDesc, "description"),
                )
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }

        private fun String.timeStamp(): String = substring(0, 10)
    }
}