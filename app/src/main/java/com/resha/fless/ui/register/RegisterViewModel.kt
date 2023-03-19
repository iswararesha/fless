package com.resha.fless.ui.register

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.resha.fless.model.UserModel
import com.resha.fless.model.UserPreference
import kotlinx.coroutines.launch


class RegisterViewModel(private val userPref: UserPreference) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage : LiveData<String> = _errorMessage

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess : LiveData<Boolean> = _isSuccess

    fun getUser(): LiveData<UserModel> {
        return userPref.getUser().asLiveData()
    }

    fun register(name: String, email: String, password: String) {
        _isLoading.value = true
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _isSuccess.value = true

                    val user = Firebase.auth.currentUser

                    val profileUpdates = userProfileChangeRequest {
                        displayName = name
                    }

                    user!!.updateProfile(profileUpdates)

                    val userId = user?.uid
                    val email2 = user?.email

                    val userData = UserModel(
                        userId!!,
                        email2!!,
                        name,
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