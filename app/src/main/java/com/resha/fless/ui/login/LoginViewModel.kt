package com.resha.fless.ui.login

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.resha.fless.model.UserModel
import com.resha.fless.model.UserPreference
import kotlinx.coroutines.launch

class LoginViewModel(private val userPref: UserPreference) : ViewModel()  {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage : LiveData<String> = _errorMessage

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess : LiveData<Boolean> = _isSuccess

    fun getUser(): LiveData<UserModel> {
        return userPref.getUser().asLiveData()
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _isSuccess.value = true

                    val user = Firebase.auth.currentUser

                    val userId = user?.uid
                    val email2 = user?.email
                    val name = user?.displayName

                    val userData = UserModel(
                        userId!!,
                        email2!!,
                        name!!,
                        true
                    )

                    viewModelScope.launch {
                        userPref.login(userData)
                    }

                } else {
                    val errorResponse = task.exception?.message

                    _errorMessage.value = errorResponse
                }
            }
        _isLoading.value = false
    }
}
