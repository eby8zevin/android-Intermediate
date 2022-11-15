package com.ahmadabuhasan.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmadabuhasan.storyapp.R
import com.ahmadabuhasan.storyapp.adapter.StoryAdapter
import com.ahmadabuhasan.storyapp.api.ApiConfig
import com.ahmadabuhasan.storyapp.databinding.ActivityMainBinding
import com.ahmadabuhasan.storyapp.model.ResponseAllStory
import com.ahmadabuhasan.storyapp.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StoryAdapter

    private lateinit var sharedPref: SessionManager
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = SessionManager(this)
        token = sharedPref.getToken

        binding.rvStory.layoutManager = LinearLayoutManager(this)
        binding.rvStory.setHasFixedSize(true)
        adapter = StoryAdapter(this, arrayListOf())
        binding.rvStory.adapter = adapter
        getAllStory("Bearer $token")

        binding.fab.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    AddStoryActivity::class.java
                )
            )
        }
    }

    private fun getAllStory(token: String) {
        val apiService = ApiConfig.getApiService().allStory(token)
        apiService.enqueue(object : Callback<ResponseAllStory> {
            override fun onResponse(
                call: Call<ResponseAllStory>,
                response: Response<ResponseAllStory>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        setData(responseBody.listStory)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseAllStory>, t: Throwable) {
                Log.e(TAG, "onFailure: " + t.message)
            }
        })
    }

    fun setData(data: ArrayList<ResponseAllStory.ListStory>) {
        adapter.setData(data)
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
            else -> return super.onOptionsItemSelected(item)
        }
    }
}