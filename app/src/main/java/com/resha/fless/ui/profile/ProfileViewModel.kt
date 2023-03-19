package com.resha.fless.ui.profile

import androidx.lifecycle.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.resha.fless.model.User
import com.resha.fless.model.UserPreference
import kotlinx.coroutines.launch

class ProfileViewModel (private val userPref: UserPreference) : ViewModel()  {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _userData = MutableLiveData<User>()
    val userDetail : LiveData<User> = _userData

    fun getUser() {
        _isLoading.value = true

        val user = Firebase.auth.currentUser

        _userData.value = User(
            user?.uid,
            user?.email,
            user?.displayName
        )

        _isLoading.value = false
    }

    fun logout(){
        Firebase.auth.signOut()

        viewModelScope.launch {
            userPref.logout()
        }
    }
}