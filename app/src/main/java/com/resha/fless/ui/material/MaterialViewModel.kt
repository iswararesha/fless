package com.resha.fless.ui.material

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
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

    private val _taskStatus = MutableLiveData<Boolean>()
    val taskStatus : LiveData<Boolean> = _taskStatus

    private val storage = Firebase.storage
    private val storageRef = storage.reference
    private val db = FirebaseFirestore.getInstance()
    private val user = Firebase.auth.currentUser

    fun getContent(material: Material){
        _isLoading.value = true

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
                    Log.w(TAG, "Error getting documents.", task.exception)
                }
            }
    }

     fun getSubModul(material: Material){
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
                         task.getString("prevSubModulId"),
                         task.getString("prevModulParent"),
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
        val uid = user?.uid

        db.collection("course")
            .document(material.courseParent!!)
            .collection("learningMaterial")
            .document(material.modulParent!!)
            .collection("subLearningMaterial")
            .document(material.subModulId!!)
            .collection("studentAttempt")
            .orderBy("dateAttempt", Query.Direction.DESCENDING)
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

    fun uploadTask(material: Material, file: Uri, fileName: String){
        val path = "course/${material.courseParent}/${material.modulParent}/${material.subModulId}" +
                "/studentAttempt/${user?.uid}/$fileName"
        val uploadTask = storageRef.child(path).putFile(file)

        uploadTask.addOnSuccessListener {
            storageRef.child(path).downloadUrl.addOnSuccessListener {
                setTask(material, it.toString())

                Log.e("Firebase", "download passed link: $it")
            }.addOnFailureListener {
                Log.e("Firebase", "Failed in downloading")
            }
        }.addOnFailureListener {
            Log.e("Firebase", "Image Upload fail")
        }
    }

    private fun setTask(material: Material, fileUrl: String){
        _isLoading.value = true

        val uid = user?.uid

        val data: MutableMap<String, Any> = mutableMapOf()

        data["user"] = uid!!
        data["fileUrl"] = fileUrl
        data["dateAttempt"] = FieldValue.serverTimestamp()

        db.collection("course")
            .document(material.courseParent!!)
            .collection("learningMaterial")
            .document(material.modulParent!!)
            .collection("subLearningMaterial")
            .document(material.subModulId!!)
            .collection("studentAttempt")
            .document(uid)
            .set(data)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot written with ID: $documentReference")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

        _isLoading.value = false
    }

     fun getUserTask(material: Material) {
         _taskStatus.value = false
        val uid = user?.uid

        db.collection("course")
            .document(material.courseParent!!)
            .collection("learningMaterial")
            .document(material.modulParent!!)
            .collection("subLearningMaterial")
            .document(material.subModulId!!)
            .collection("studentAttempt")
            .document(uid!!)
            .addSnapshotListener {
                snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                val source = if (snapshot != null && snapshot.metadata.hasPendingWrites())
                    "Local"
                else
                    "Server"

                if (snapshot != null && snapshot.exists()) {
                    _taskStatus.value = true
                    Log.d(TAG, "$source data: ${snapshot.data}")
                } else {
                    Log.d(TAG, "$source data: null")
                }
        }
    }
}