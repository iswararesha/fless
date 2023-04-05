package com.resha.fless.ui.profile

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.resha.fless.model.User
import com.resha.fless.model.UserPreference
import kotlinx.coroutines.launch

class ProfileViewModel (private val userPref: UserPreference) : ViewModel()  {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _isChanged = MutableLiveData<Boolean>()
    val isChanged : LiveData<Boolean> = _isChanged

    private val _userData = MutableLiveData<User>()
    val userDetail : LiveData<User> = _userData

    fun getUser() {
        _isLoading.value = true

        val user = Firebase.auth.currentUser

        _userData.value = User(
            user?.uid!!,
            user?.email!!,
            user?.displayName!!,
            true
        )

        _isLoading.value = false
    }

    fun logout(){
        Firebase.auth.signOut()


    }

    fun changePassword(oldPassword: String, newPassword: String, context: Context){
        val user = Firebase.auth.currentUser
        _isChanged.value = false

        val email = user!!.email
        val credential = EmailAuthProvider
            .getCredential(email!!, oldPassword)

        user.reauthenticate(credential)
            .addOnCompleteListener {
                Log.d(TAG, "User re-authenticated.")

                if(it.isSuccessful) {
                    user!!.updatePassword(newPassword)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context,
                                    "Ganti password berhasil.",
                                    Toast.LENGTH_SHORT)
                                    .show()
                                _isChanged.value = true
                            } else {
                                Toast.makeText(context,
                                    "Ganti password gagal, \nperiksa jaringan anda.",
                                    Toast.LENGTH_SHORT).show()
                                _isChanged.value = false
                            }
                        }
                }else{
                    Toast.makeText(context,
                        "Password lama yang anda masukkan salah!",
                        Toast.LENGTH_SHORT).show()
                }
            }


    }

    fun getThemeSettings(): LiveData<Boolean> {
        return userPref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            userPref.saveThemeSetting(isDarkModeActive)
        }
    }
}