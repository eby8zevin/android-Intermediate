package com.ahmadabuhasan.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ahmadabuhasan.storyapp.R
import com.ahmadabuhasan.storyapp.data.Result
import com.ahmadabuhasan.storyapp.databinding.ActivityRegisterBinding
import com.ahmadabuhasan.storyapp.viewmodel.StoryViewModel
import com.ahmadabuhasan.storyapp.viewmodel.ViewModelFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var viewModel: StoryViewModel
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        setupViewModel()

        binding.tvToLogin.setOnClickListener { toLogin() }

        binding.btnRegister.setOnClickListener { checkRegister() }
    }

    private fun setupViewModel() {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[StoryViewModel::class.java]
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
            sendToAPI(name, email, password)
        }
    }

    private fun sendToAPI(name: String, email: String, password: String) {

        viewModel.vmRegister(name, email, password).observe(this) {

            when (it) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    Toast.makeText(this, it.data.message, Toast.LENGTH_SHORT).show()
                    toLogin()
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
}