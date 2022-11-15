package com.ahmadabuhasan.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ahmadabuhasan.storyapp.R
import com.ahmadabuhasan.storyapp.api.ApiConfig
import com.ahmadabuhasan.storyapp.databinding.ActivityRegisterBinding
import com.ahmadabuhasan.storyapp.model.ResponseRegister
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "RegisterActivity"
    }

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()

        binding.tvToLogin.setOnClickListener { toLogin() }

        binding.btnRegister.setOnClickListener { checkRegister() }
    }

    private fun toLogin() {
        val i = Intent(this, LoginActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    private fun checkRegister() {
        val name = binding.edRegisterName.text.toString()
        val email = binding.edRegisterEmail.text.toString()
        val password = binding.edRegisterPassword.text.toString()

        if (name.isEmpty()) {
            binding.edRegisterName.requestFocus()
        } else if (email.isEmpty()) {
            binding.edRegisterEmail.requestFocus()
        } else if (password.isEmpty()) {
            binding.edRegisterPassword.requestFocus()
        } else {
            sendAPI(name, email, password)
        }
    }

    private fun sendAPI(name: String, email: String, password: String) {
        showLoading(true)
        val apiService = ApiConfig.getApiService().register(name, email, password)
        apiService.enqueue(object : Callback<ResponseRegister> {
            override fun onResponse(
                call: Call<ResponseRegister>,
                response: Response<ResponseRegister>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val i = Intent(this@RegisterActivity, MainActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(i)
                    Toast.makeText(
                        this@RegisterActivity,
                        response.body()?.message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    val loginFailed = Gson().fromJson(
                        response.errorBody()?.charStream(),
                        ResponseRegister::class.java
                    )
                    Toast.makeText(this@RegisterActivity, loginFailed.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<ResponseRegister>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure: " + t.message)
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}