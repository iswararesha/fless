package com.resha.fless.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.resha.fless.databinding.ActivityForgotPasswordBinding
import com.resha.fless.model.UserPreference
import com.resha.fless.ui.ViewModelFactory
import com.resha.fless.ui.main.MainActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
    }

    private fun setupAction(){
        binding.btnVerification.setOnClickListener(){
            actionLogin()
        }

        binding.tvBackLogin.setOnClickListener(){
            finish()
        }
    }

    private fun actionLogin() {
        val email = binding.editEmail

        if(checkValidation(email)){
            loginViewModel.forgotPassword(email.text.toString(), this)
        }
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]

        loginViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        loginViewModel.errorMessage.observe(this) { message ->
            Toast.makeText(this, message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkValidation(email: EditText): Boolean {
        val isEmailError : Boolean = email.length() == 0
        val isEmailFormatError : Boolean = !Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()

        if(isEmailError || isEmailFormatError){
            if(isEmailError){
                email.error = "Email harus diisi"
                email.setBackgroundResource(R.drawable.error_border)
            }

            if(isEmailFormatError) {
                email.error = "Isi Email dengan benar"
                email.setBackgroundResource(R.drawable.error_border)
            }

            return false
        }else{
            email.setBackgroundResource(R.drawable.standard_border)
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

    private fun setupView() {
        supportActionBar?.hide()
    }
}