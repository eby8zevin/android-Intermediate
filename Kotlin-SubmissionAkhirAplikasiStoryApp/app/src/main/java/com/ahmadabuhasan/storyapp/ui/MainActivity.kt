package com.ahmadabuhasan.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmadabuhasan.storyapp.R
import com.ahmadabuhasan.storyapp.adapter.LoadingStateAdapter
import com.ahmadabuhasan.storyapp.adapter.StoryAdapter
import com.ahmadabuhasan.storyapp.databinding.ActivityMainBinding
import com.ahmadabuhasan.storyapp.utils.SessionManager
import com.ahmadabuhasan.storyapp.viewmodel.StoryViewModel
import com.ahmadabuhasan.storyapp.viewmodel.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPref: SessionManager
    private lateinit var viewModel: StoryViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = SessionManager(this)
        setupViewModel()
        setupView()
        getAllStory()

        binding.fab.setOnClickListener { toAddStory() }
    }

    private fun toAddStory() {
        startActivity(Intent(this@MainActivity, AddStoryActivity::class.java))
    }

    private fun setupViewModel() {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[StoryViewModel::class.java]
    }

    private fun setupView() {
        storyAdapter = StoryAdapter()
        binding.rvStory.layoutManager = LinearLayoutManager(this)
        with(binding.rvStory) {
            setHasFixedSize(true)
            adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    storyAdapter.retry()
                }
            )
        }
    }

    private fun getAllStory() {
        viewModel.vmListStory().observe(this) {
            storyAdapter.submitData(lifecycle, it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle("Apakah Anda yakin ingin keluar ?")
                    ?.setPositiveButton("Iya") { _, _ ->
                        sharedPref.clearData()
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    }
                    ?.setNegativeButton("Batal", null)
                val alert = alertDialog.create()
                alert.show()
                true
            }

            R.id.action_maps -> {
                val i = Intent(this, MapsActivity::class.java)
                startActivity(i)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}