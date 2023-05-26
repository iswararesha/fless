package com.resha.fless.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.resha.fless.R
import com.resha.fless.databinding.ActivityLoginBinding
import com.resha.fless.preference.UserPreference
import com.resha.fless.ui.ViewModelFactory
import com.resha.fless.ui.main.MainActivity
import com.resha.fless.ui.register.RegisterActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
class LoginActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupView()
        setupActions()
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]

        loginViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        loginViewModel.errorMessage.observe(this) { message ->
            Toast.makeText(this@LoginActivity, message.toString(), Toast.LENGTH_SHORT).show()
        }

        loginViewModel.isSuccess.observe(this) {
            if (it) {
                Toast.makeText(this, "Selamat anda berhasil login", Toast.LENGTH_SHORT)

                Handler().postDelayed({
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish() } , 2000)
            }
        }
    }

    private fun setupActions() {
        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnLogin.setOnClickListener {
            actionLogin()
        }

        binding.tvForgotPassword.setOnClickListener(){
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupView() {
        supportActionBar?.hide()
    }

    private fun actionLogin() {
        val email = binding.editEmail
        val password = binding.editPassword

        if(checkValidation(email, password)){
            loginViewModel.login(
                email.text.toString(),
                password.text.toString()
            )
        }
    }

    private fun checkValidation(email: EditText, password: EditText): Boolean {
        val isEmailError : Boolean = email.length() == 0
        val isEmailFormatError : Boolean = !Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()
        val isPasswordError : Boolean = password.length() == 0

        if(isEmailError || isEmailFormatError){
            if(isEmailError){
                email.error = "Email harus diisi"
                email.setBackgroundResource(R.drawable.error_border)
            }

            if(isEmailFormatError){
                email.error = "Isi Email dengan benar"
                email.setBackgroundResource(R.drawable.error_border)
            }

            return false
        }else{
            email.setBackgroundResource(R.drawable.standard_border)
        }

        if(isPasswordError){
            password.error = "Password harus diisi"
            password.setBackgroundResource(R.drawable.error_border)

            return false
        }else{
            password.setBackgroundResource(R.drawable.standard_border)
        }

        return true
    }

    private fun showLoading(isLoading: Boolean) {
        if(isLoading){
            binding.loading.visibility = View.VISIBLE
        }else{
            binding.loading.visibility = View.GONE
        }
    }
}