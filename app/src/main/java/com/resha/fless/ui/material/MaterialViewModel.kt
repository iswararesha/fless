package com.resha.fless.ui.material

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.resha.fless.model.*
import java.sql.Timestamp

class MaterialViewModel (private val userPref: UserPreference) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _contentData = MutableLiveData<List<Content>>()
    val contentData : LiveData<List<Content>> = _contentData

    private val _materialData = MutableLiveData<SubModul>()
    val materialData : LiveData<SubModul> = _materialData

    private val _attemptData = MutableLiveData<List<Attempt>>()
    val attemptData : LiveData<List<Attempt>> = _attemptData

    fun getContent(material: Material){
        _isLoading.value = true

        val db = FirebaseFirestore.getInstance()

        db.collection("course")
            .document(material.courseParent!!)
            .collection("learningMaterial")
            .document(material.modulParent!!)
            .collection("subLearningMaterial")
            .document(material.subModulId!!)
            .collection("contentLearningMaterial")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val savedList : MutableList<Content> = mutableListOf()
                    for (documents in task.result) {
                        val itemList = Content(
                            documents.id,
                            documents.getString("content"),
                            documents.getString("type")
                        )
                        savedList.add(itemList)
                    }
                    _contentData.value = savedList

                    _isLoading.value = false
                } else {
                    Log.w(ContentValues.TAG, "Error getting documents.", task.exception)
                }
            }
    }

     fun getSubModul(material: Material){
         val db = FirebaseFirestore.getInstance()

         db.collection("course")
             .document(material.courseParent!!)
             .collection("learningMaterial")
             .document(material.modulParent!!)
             .collection("subLearningMaterial")
             .document(material.subModulId!!)
             .get()
             .addOnSuccessListener  { task ->
                 if (task != null) {
                     Log.e(TAG, task.id)
                     var itemList = SubModul(
                         task.id,
                         task.getString("name"),
                         task.getString("type"),
                         material.courseParent,
                         material.modulParent,
                         task.getString("nextSubModulId"),
                         task.getString("nextModulParent")
                     )
                     _materialData.value = itemList
                 }
             }.addOnFailureListener { exception ->
                 Log.d(TAG, "get failed with ", exception)
             }
    }

    fun getUserAttempt(material: Material){
        val db = FirebaseFirestore.getInstance()
        val user = Firebase.auth.currentUser
        val uid = user?.uid

        db.collection("course")
            .document(material.courseParent!!)
            .collection("learningMaterial")
            .document(material.modulParent!!)
            .collection("subLearningMaterial")
            .document(material.subModulId!!)
            .collection("studentAttempt")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var savedList : MutableList<Attempt> = mutableListOf()
                    for (documents in task.result) {
                        if(documents.getString("user")==uid){
                            val score = documents.getString("score")?.toFloat()?.times(100).toString()
                            var itemList = Attempt(
                                documents.getString("user"),
                                score,
                                documents.getTimestamp("dateAttempt")
                            )
                            savedList.add(itemList)
                        }
                    }
                    _attemptData.value = savedList

                    _isLoading.value = false
                } else {
                    Log.w(TAG, "Error getting documents.", task.exception)
                }
            }
    }
}