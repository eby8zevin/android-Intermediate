package com.ahmadabuhasan.storyapp.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.ahmadabuhasan.storyapp.databinding.ItemListBinding
import com.ahmadabuhasan.storyapp.model.ResponseAllStory
import com.ahmadabuhasan.storyapp.ui.DetailStoryActivity
import com.ahmadabuhasan.storyapp.utils.Constant
import com.bumptech.glide.Glide

class StoryAdapter(
    private val context: Context,
    private val storyList: ArrayList<ResponseAllStory.ListStory>
) :
    RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StoryAdapter.StoryViewHolder {
        val binding = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryAdapter.StoryViewHolder, position: Int) {
        storyList[position].let { story ->
            holder.bind(story)
        }
    }

    override fun getItemCount(): Int = storyList.size

    inner class StoryViewHolder(private val binding: ItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ResponseAllStory.ListStory) {
            with(binding) {
                val name = story.name
                tvTitle.text = buildString {
                    append("Nama: ")
                    append(name)
                }

                val desc = story.description
                tvDesc.text = buildString {
                    append("Desc: ")
                    append(desc)
                }

                tvDate.text = story.createdAt.timeStamp()

                Glide.with(context)
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
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: ArrayList<ResponseAllStory.ListStory>) {
        storyList.clear()
        storyList.addAll(data)
        notifyDataSetChanged()
    }

    fun String.timeStamp(): String = substring(0, 10)
}