package com.ahmadabuhasan.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ahmadabuhasan.storyapp.R
import com.ahmadabuhasan.storyapp.api.ApiConfig
import com.ahmadabuhasan.storyapp.databinding.ActivityLoginBinding
import com.ahmadabuhasan.storyapp.model.ResponseLogin
import com.ahmadabuhasan.storyapp.utils.Constant.KEY_EMAIL
import com.ahmadabuhasan.storyapp.utils.Constant.KEY_IS_LOGIN
import com.ahmadabuhasan.storyapp.utils.Constant.KEY_NAME
import com.ahmadabuhasan.storyapp.utils.Constant.KEY_TOKEN
import com.ahmadabuhasan.storyapp.utils.Constant.KEY_USER_ID
import com.ahmadabuhasan.storyapp.utils.SessionManager
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "LoginActivity"
    }

    private lateinit var sharedPref: SessionManager
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()
        sharedPref = SessionManager(this)

        binding.tvToRegister.setOnClickListener { toRegister() }

        binding.btnLogin.setOnClickListener { checkLogin() }
    }

    private fun toRegister() {
        val i = Intent(this, RegisterActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
    }

    private fun checkLogin() {
        val email = binding.edLoginEmail.text.toString()
        val password = binding.edLoginPassword.text.toString()

        if (email.isEmpty()) {
            binding.edLoginEmail.requestFocus()
        } else if (password.isEmpty()) {
            binding.edLoginPassword.requestFocus()
        } else {
            sendAPI(email, password)
        }
    }

    private fun sendAPI(email: String, password: String) {
        showLoading(true)
        val apiService = ApiConfig.getApiService().login(email, password)
        apiService.enqueue(object : Callback<ResponseLogin> {
            override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()!!.loginResult
                    sharedPref.apply {
                        setBooleanPref(KEY_IS_LOGIN, true)
                        setStringPref(KEY_TOKEN, responseBody.token)
                        setStringPref(KEY_USER_ID, responseBody.userId)
                        setStringPref(KEY_NAME, responseBody.name)
                        setStringPref(KEY_EMAIL, email)
                    }

                    val i = Intent(this@LoginActivity, MainActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(i)
                    Toast.makeText(this@LoginActivity, response.body()?.message, Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val loginFailed = Gson().fromJson(
                        response.errorBody()?.charStream(),
                        ResponseLogin::class.java
                    )
                    Toast.makeText(this@LoginActivity, loginFailed.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
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

    override fun onStart() {
        super.onStart()
        val isLogin = sharedPref.isLogin
        if (isLogin) {
            val i = Intent(this@LoginActivity, MainActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
        }
    }
}