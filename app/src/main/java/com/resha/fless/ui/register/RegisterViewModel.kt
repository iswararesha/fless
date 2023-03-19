package com.resha.fless.ui.register

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.resha.fless.model.UserModel
import com.resha.fless.model.UserPreference
import kotlinx.coroutines.launch

class RegisterViewModel(private val userPref: UserPreference) : ViewModel() {
    fun register(name: String, email: String, password: String) {
        val userId = "1a"
        val email = email
        val token = "token"
        val name = name

        val userData = UserModel(
            userId!!,
            email!!,
            name!!,
            token!!,
            true
        )

        viewModelScope.launch {
            userPref.login(userData)
        }
    }
}