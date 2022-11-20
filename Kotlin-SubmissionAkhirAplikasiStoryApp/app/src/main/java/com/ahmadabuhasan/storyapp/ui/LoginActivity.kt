package com.ahmadabuhasan.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ahmadabuhasan.storyapp.R
import com.ahmadabuhasan.storyapp.data.Result
import com.ahmadabuhasan.storyapp.databinding.ActivityLoginBinding
import com.ahmadabuhasan.storyapp.utils.Constant.KEY_EMAIL
import com.ahmadabuhasan.storyapp.utils.Constant.KEY_IS_LOGIN
import com.ahmadabuhasan.storyapp.utils.Constant.KEY_NAME
import com.ahmadabuhasan.storyapp.utils.Constant.KEY_TOKEN
import com.ahmadabuhasan.storyapp.utils.Constant.KEY_USER_ID
import com.ahmadabuhasan.storyapp.utils.SessionManager
import com.ahmadabuhasan.storyapp.viewmodel.StoryViewModel
import com.ahmadabuhasan.storyapp.viewmodel.ViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPref: SessionManager
    private lateinit var viewModel: StoryViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        sharedPref = SessionManager(this)
        setupViewModel()

        binding.tvToRegister.setOnClickListener { toRegister() }

        binding.btnLogin.setOnClickListener { checkLogin() }
    }

    private fun setupViewModel() {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[StoryViewModel::class.java]
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
            sendToAPI(email, password)
        }
    }

    private fun sendToAPI(email: String, password: String) {
        viewModel.vmLogin(email, password).observe(this@LoginActivity) {

            when (it) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    val responseBody = it.data.loginResult
                    sharedPref.apply {
                        setBooleanPref(KEY_IS_LOGIN, true)
                        setStringPref(KEY_TOKEN, responseBody.token)
                        setStringPref(KEY_USER_ID, responseBody.userId)
                        setStringPref(KEY_NAME, responseBody.name)
                        setStringPref(KEY_EMAIL, email)
                    }
                    val i = Intent(this@LoginActivity, MainActivity::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(i)
                    finish()
                }

                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
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
            finish()
        }
    }
}