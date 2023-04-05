package com.resha.fless.ui.login

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.resha.fless.model.UserPreference
import kotlinx.coroutines.launch

class LoginViewModel(private val userPref: UserPreference) : ViewModel()  {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage : LiveData<String> = _errorMessage

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess : LiveData<Boolean> = _isSuccess

    fun login(email: String, password: String) {
        _isLoading.value = true
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _isSuccess.value = true

                } else {
                    val errorResponse = task.exception?.message

                    _errorMessage.value = errorResponse
                }
            }
        _isLoading.value = false
    }

    fun forgotPassword(email: String, context: Context){
        if (email.isEmpty()) {
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return
        }

        Firebase.auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context,"Email verifikasi berhasil dikirim.",Toast.LENGTH_SHORT).show()
                }else {
                    val errorResponse = task.exception?.message

                    Toast.makeText(context,errorResponse,Toast.LENGTH_SHORT).show()
                }
            }
    }
}
