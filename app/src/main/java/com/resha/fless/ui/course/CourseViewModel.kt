package com.resha.fless.ui.course

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.resha.fless.model.Course
import com.resha.fless.model.UserModel
import com.resha.fless.model.UserPreference


class CourseViewModel (private val userPref: UserPreference) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _courseData = MutableLiveData<List<Course>>()
    val courseData : LiveData<List<Course>> = _courseData

    fun getUser(): LiveData<UserModel> {
        return userPref.getUser().asLiveData()
    }

    fun getCourse(){
        _isLoading.value = true

        val db = FirebaseFirestore.getInstance()

        db.collection("course")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var savedList : MutableList<Course> = mutableListOf()
                    for (documents in task.result) {
                        var itemList = Course(
                            documents.id,
                            documents.getString("courseDescription"),
                            documents.getString("courseName"),
                            documents.getString("hourNeed")
                        )
                        savedList.add(itemList)
                    }
                    _courseData.value = savedList

                    _isLoading.value = false
                } else {
                    Log.w(TAG, "Error getting documents.", task.exception)
                }
            }
    }
}