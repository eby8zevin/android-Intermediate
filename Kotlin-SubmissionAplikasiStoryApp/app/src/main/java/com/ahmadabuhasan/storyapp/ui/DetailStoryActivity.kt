package com.ahmadabuhasan.storyapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ahmadabuhasan.storyapp.databinding.ActivityDetailStoryBinding
import com.ahmadabuhasan.storyapp.model.ResponseAllStory
import com.ahmadabuhasan.storyapp.utils.Constant
import com.bumptech.glide.Glide

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding
    private lateinit var story: ResponseAllStory.ListStory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Detail Story"
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        story = intent.getParcelableExtra(Constant.BUNDLE_STORY)!!
        setData()
    }

    private fun setData() {
        binding.tvTitle.text = story.name
        binding.tvDesc.text = story.description

        Glide.with(this)
            .load(story.photoUrl)
            .centerCrop()
            .skipMemoryCache(true)
            .into(binding.ivPhoto)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onNavigateUp()
    }
}