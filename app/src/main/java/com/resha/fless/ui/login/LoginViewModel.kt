package com.resha.fless.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.resha.fless.model.UserModel
import com.resha.fless.model.UserPreference
import kotlinx.coroutines.launch

class LoginViewModel(private val userPref: UserPreference) : ViewModel()  {
    fun login(name: String, email: String, password: String) {
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
