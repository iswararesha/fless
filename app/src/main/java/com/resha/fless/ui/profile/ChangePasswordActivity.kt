package com.resha.fless.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.resha.fless.R
import com.resha.fless.databinding.ActivityChangePasswordBinding
import com.resha.fless.preference.UserPreference
import com.resha.fless.ui.ViewModelFactory


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
    }

    private fun setupView() {
        supportActionBar?.hide()
    }


    private fun setupViewModel() {
        profileViewModel = ViewModelProvider(this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[ProfileViewModel::class.java]

        profileViewModel.isLoading.observe(this){
            showLoading(it)
        }

        binding.btnConfirmation.setOnClickListener(){
            setupAction()
        }

        binding.btnBack.setOnClickListener(){
            finish()
        }
    }

    private fun setupAction(){
        val oldPassword = binding.editPassword
        val newPassword = binding.editNewPassword
        val confirmation = binding.editConfirmPassword

        if(checkValidation(oldPassword, newPassword, confirmation)){
            profileViewModel.changePassword(oldPassword.text.toString(), newPassword.text.toString(),this)
            profileViewModel.isChanged.observe(this){
                if(it) {
                    finish()
                }
            }
        }
    }

    private fun checkValidation(oldPassword: EditText, newPassword: EditText, confirmation: EditText): Boolean {
        val isOldPassword : Boolean = oldPassword.length() == 0
        val isNewPassword : Boolean = newPassword.length() == 0
        val isConfirmationPassword : Boolean = confirmation.length() == 0

        if(isOldPassword || isNewPassword || isConfirmationPassword){
            if(isOldPassword){
                oldPassword.error = "Harus diisi"
                oldPassword.setBackgroundResource(R.drawable.error_border)
            }else {
                oldPassword.setBackgroundResource(R.drawable.standard_border)
            }

            if(isOldPassword){
                newPassword.error = "Harus diisi"
                newPassword.setBackgroundResource(R.drawable.error_border)
            }else {
                newPassword.setBackgroundResource(R.drawable.standard_border)
            }

            if(isOldPassword){
                confirmation.error = "Harus diisi"
                confirmation.setBackgroundResource(R.drawable.error_border)
            }else {
                confirmation.setBackgroundResource(R.drawable.standard_border)
            }

            return false
        }

        if(newPassword.text.toString() != confirmation.text.toString()){
            confirmation.error = "Konfirmasi password tiak sesuai"
            confirmation.setBackgroundResource(R.drawable.error_border)

            return false
        }else{
            confirmation.setBackgroundResource(R.drawable.standard_border)
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