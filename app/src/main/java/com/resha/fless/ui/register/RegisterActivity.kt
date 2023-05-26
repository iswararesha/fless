package com.resha.fless.ui.register

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
import com.resha.fless.databinding.ActivityRegisterBinding
import com.resha.fless.preference.UserPreference
import com.resha.fless.ui.ViewModelFactory
import com.resha.fless.ui.login.LoginActivity
import com.resha.fless.ui.main.MainActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
class RegisterActivity : AppCompatActivity() {
    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupView()
        setupActions()
    }

    private fun setupViewModel() {
        registerViewModel = ViewModelProvider(this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[RegisterViewModel::class.java]

        registerViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        registerViewModel.errorMessage.observe(this) { message ->
            Toast.makeText(this@RegisterActivity, message.toString(), Toast.LENGTH_SHORT).show()
        }

        registerViewModel.isSuccess.observe(this) {
            if (it) {
                Toast.makeText(this, "Selamat datang teman!", Toast.LENGTH_SHORT).show()

                Handler().postDelayed({
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish() } , 2000)
            }
        }
    }

    private fun setupActions() {
        binding.tvLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnRegister.setOnClickListener {
            actionRegister()
        }
    }

    private fun setupView() {
        supportActionBar?.hide()
    }

    private fun actionRegister() {
        val name = binding.editName
        val email = binding.editEmail
        val password = binding.editPassword

        if(checkValidation(name, email, password)){
            registerViewModel.register(
                name.text.toString(),
                email.text.toString(),
                password.text.toString()
            )
        }
    }

    private fun checkValidation(name: EditText, email: EditText, password: EditText): Boolean {
        val isNameError : Boolean = name.length() == 0
        val isEmailError : Boolean = email.length() == 0
        val isEmailFormatError : Boolean = !Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()
        val isPasswordError : Boolean = password.length() == 0
        val isPasswordLengthError : Boolean = password.length() < 8 || password.length() > 16

        if(isNameError){
            name.error = "Nama harus diisi"
            name.setBackgroundResource(R.drawable.error_border)

            return false
        }else{
            name.setBackgroundResource(R.drawable.standard_border)
        }

        if(isEmailError || isEmailFormatError) {
            if (isEmailError) {
                email.error = "Email harus diisi"
                email.setBackgroundResource(R.drawable.error_border)
            }

            if (isEmailFormatError) {
                email.error = "Isi Email dengan benar"
                email.setBackgroundResource(R.drawable.error_border)
            }

            return false
        }else{
            email.setBackgroundResource(R.drawable.standard_border)
        }

        if(isPasswordError || isPasswordLengthError) {
            if(isPasswordError) {
                password.error = "Password harus diisi"
                password.setBackgroundResource(R.drawable.error_border)
            }

            if(isPasswordLengthError){
                password.error = "Password minimal 8 dan maksimal 16 karakter"
                password.setBackgroundResource(R.drawable.error_border)
            }

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