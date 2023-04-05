package com.resha.fless.ui.course

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.resha.fless.model.Course
import com.resha.fless.model.UserPreference


class CourseViewModel (private val userPref: UserPreference) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _courseData = MutableLiveData<List<Course>>()
    val courseData : LiveData<List<Course>> = _courseData

    private val user = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun searchCourse(key: String){
        _courseData

        _isLoading.value = true
        val userId = user.uid

        val searchKey = key.toUpperCase()

        db.collection("course").orderBy("searchKey").startAt(searchKey).endAt(searchKey + "\uf8ff")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var savedList : MutableList<Course> = mutableListOf()
                    _courseData.value = savedList
                    for (documents in task.result) {
                        db.collection("user").document(userId!!)
                            .collection("course").document(documents.id)
                            .addSnapshotListener { snapshot, e ->
                                if (e != null) {
                                    Log.w(TAG, "Listen failed.", e)
                                    return@addSnapshotListener
                                }

                                val source = if (snapshot != null && snapshot.metadata.hasPendingWrites())
                                    "Local"
                                else
                                    "Server"

                                if (snapshot != null && snapshot.exists()) {
                                    var itemList = Course(
                                        documents.id,
                                        documents.getString("courseDescription"),
                                        documents.getString("courseName"),
                                        documents.getLong("totalMaterial")?.toInt(),
                                        true,
                                        documents.getString("courseImg"),
                                        documents.getString("videoLink")
                                    )

                                    Log.d("Course", itemList.courseId!!)

                                    var dataExist = false
                                    var index = 0

                                    for(i in 1..savedList.size){
                                        if(savedList[i-1].courseId == itemList.courseId){
                                            dataExist = true
                                            index = i-1
                                        }
                                    }

                                    if(dataExist){
                                        savedList[index] = itemList
                                    }else{
                                        savedList.add(itemList)
                                    }

                                    _courseData.value = savedList

                                    Log.d(TAG, "$source data: ${snapshot.data}")
                                } else {
                                    var itemList = Course(
                                        documents.id,
                                        documents.getString("courseDescription"),
                                        documents.getString("courseName"),
                                        documents.getLong("totalMaterial")?.toInt(),
                                        false,
                                        documents.getString("courseImg"),
                                        documents.getString("videoLink")
                                    )
                                    Log.d("Course", itemList.courseId!!)
                                    savedList.add(itemList)

                                    _courseData.value = savedList

                                    Log.d(TAG, "$source data: null")
                                }
                            }
                    }

                    _isLoading.value = false
                } else {
                    Log.w(TAG, "Error getting documents.", task.exception)
                }
            }
    }

    fun getCourse(){
        _isLoading.value = true
        val userId = user.uid

        db.collection("course")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var savedList : MutableList<Course> = mutableListOf()
                    _courseData.value = savedList
                    for (documents in task.result) {
                        db.collection("user").document(userId!!)
                            .collection("course").document(documents.id)
                            .addSnapshotListener { snapshot, e ->
                                if (e != null) {
                                    Log.w(TAG, "Listen failed.", e)
                                    return@addSnapshotListener
                                }

                                val source = if (snapshot != null && snapshot.metadata.hasPendingWrites())
                                    "Local"
                                else
                                    "Server"

                                if (snapshot != null && snapshot.exists()) {
                                    var itemList = Course(
                                        documents.id,
                                        documents.getString("courseDescription"),
                                        documents.getString("courseName"),
                                        documents.getLong("totalMaterial")?.toInt(),
                                        true,
                                        documents.getString("courseImg"),
                                        documents.getString("videoLink")
                                    )

                                    Log.d("Course", itemList.courseId!!)

                                    var dataExist = false
                                    var index = 0

                                    for(i in 1..savedList.size){
                                        if(savedList[i-1].courseId == itemList.courseId){
                                            dataExist = true
                                            index = i-1
                                        }
                                    }

                                    if(dataExist){
                                        savedList[index] = itemList
                                    }else{
                                        savedList.add(itemList)
                                    }

                                    _courseData.value = savedList

                                    Log.d(TAG, "$source data: ${snapshot.data}")
                                } else {
                                    var itemList = Course(
                                        documents.id,
                                        documents.getString("courseDescription"),
                                        documents.getString("courseName"),
                                        documents.getLong("totalMaterial")?.toInt(),
                                        false,
                                        documents.getString("courseImg"),
                                        documents.getString("videoLink")
                                    )
                                    Log.d("Course", itemList.courseId!!)
                                    savedList.add(itemList)

                                    _courseData.value = savedList

                                    Log.d(TAG, "$source data: null")
                                }
                            }
                    }

                    _isLoading.value = false
                } else {
                    Log.w(TAG, "Error getting documents.", task.exception)
                }
            }
    }

    fun setStatus(course: Course){
        _isLoading.value = true
        val data: MutableMap<String, Any> = mutableMapOf()

        data["isOpen"] = true

        db.collection("user")
            .document(user.uid!!)
            .collection("course")
            .document(course.courseId!!)
            .set(data)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot written with ID: $documentReference")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

        db.collection("user")
            .document(user.uid!!)
            .collection("course")
            .document(course.courseId!!)
            .collection("modul")
            .document("MODUL01")
            .collection("subModul")
            .document("MODUL01SUB01")
            .set(data)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot written with ID: $documentReference")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

        _isLoading.value = false
    }
}