package com.resha.fless.ui.material

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.resha.fless.model.*
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

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

    private val _modulData = MutableLiveData<List<Modul>>()
    val modulData : LiveData<List<Modul>> = _modulData

    private val storage = Firebase.storage
    private val storageRef = storage.reference
    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance()

    fun prevModul(context: Context){
        val prevMaterial = Material(
            _materialData.value?.prevSubModulId,
            _materialData.value?.courseParent,
            _materialData.value?.prevModulParent
        )

        if(prevMaterial.subModulId == "none"){
            getActivity(context)?.finish()
        }

        getSubModul(prevMaterial)
    }

    fun nextModul(context: Context){
        val currentMaterial = Material(
            _materialData.value?.subModulId,
            _materialData.value?.courseParent,
            _materialData.value?.modulParent
        )

        updateLog(currentMaterial)

        val nextMaterial = Material(
            _materialData.value?.nextSubModulId,
            _materialData.value?.courseParent,
            _materialData.value?.nextModulParent
        )

        if(nextMaterial.subModulId == "none"){
            getActivity(context)?.finish()
        }else{
            setStatus(nextMaterial)
        }

        getSubModul(nextMaterial)
    }

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
                    Log.e(TAG, snapshot.id)
                    var itemList = SubModul(
                        snapshot.id,
                        snapshot.getString("name"),
                        snapshot.getString("type"),
                        material.courseParent,
                        material.modulParent,
                        snapshot.getString("prevSubModulId"),
                        snapshot.getString("prevModulParent"),
                        snapshot.getString("nextSubModulId"),
                        snapshot.getString("nextModulParent"),
                        true
                    )

                    val newMaterial = Material(
                        snapshot.id,
                        material.courseParent,
                        material.modulParent
                    )
                    val type = snapshot.getString("type")

                    newLog(newMaterial, type!!)

                    _materialData.value = itemList
                    Log.d(TAG, "$source data: ${snapshot.data}")
                } else {
                    Log.d(TAG, "$source data: null")
                }
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

                if (snapshot != null) {
                    var savedList : MutableList<Attempt> = mutableListOf()
                    for (documents in snapshot.documents) {
                        if(documents.getString("user")==uid){
                            val score = documents.getString("score")?.toFloat()?.times(100)?.toInt().toString()
                            val date = documents.getTimestamp("dateAttempt")?.toDate()?.let {
                                SimpleDateFormat("dd MMMM yyyy - hh:mm a", Locale("id", "ID")).format(
                                    it)
                            }
                            var itemList = Attempt(
                                documents.getString("user"),
                                score,
                                date
                            )
                            savedList.add(itemList)
                        }
                    }
                    _attemptData.value = savedList
                } else {
                    Log.d(TAG, "$source data: null")
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
                updateLog(material)

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

    private fun setStatus(material: Material){
        _isLoading.value = true
        val data: MutableMap<String, Any> = mutableMapOf()

        data["isOpen"] = true

        db.collection("user")
            .document(user?.uid!!)
            .collection("course")
            .document(material.courseParent!!)
            .collection("modul")
            .document(material.modulParent!!)
            .collection("subModul")
            .document(material.subModulId!!)
            .set(data)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot written with ID: $documentReference")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

        _isLoading.value = false
    }

    fun getModul(courseParent: String){
        _isLoading.value = true

        db.collection("course").document(courseParent).collection("learningMaterial")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                val source = if (snapshot != null && snapshot.metadata.hasPendingWrites())
                    "Local"
                else
                    "Server"

                if (snapshot != null) {
                    var savedList : MutableList<Modul> = mutableListOf()
                    for (documents in snapshot.documents) {
                        var itemList = Modul(
                            documents.id,
                            documents.getString("modulName"),
                            documents.getLong("totalMaterial")?.toInt(),
                            getSubModul(courseParent, documents.id)
                        )

                        savedList.add(itemList)
                    }

                    _modulData.value = savedList
                    _isLoading.value = false
                } else {
                    Log.d(TAG, "$source data: null")
                    _isLoading.value = false
                }
            }
    }

    private fun getSubModul(courseParent: String, modulParent: String): List<SubModul>{
        val subModul : MutableList<SubModul> = mutableListOf()

        val db = FirebaseFirestore.getInstance()

        val user = FirebaseAuth.getInstance()
        val userId = user.uid

        db.collection("course")
            .document(courseParent)
            .collection("learningMaterial")
            .document(modulParent)
            .collection("subLearningMaterial")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                val source = if (snapshot != null && snapshot.metadata.hasPendingWrites())
                    "Local"
                else
                    "Server"

                if (snapshot != null) {
                    for (document in snapshot.documents) {
                        db.collection("user").document(userId!!)
                            .collection("course").document(courseParent)
                            .collection("modul").document(modulParent)
                            .collection("subModul").document(document.id)
                            .addSnapshotListener { snapshot2, e ->
                                if (e != null) {
                                    Log.w(TAG, "Listen failed.", e)
                                    return@addSnapshotListener
                                }

                                val source = if (snapshot2 != null && snapshot.metadata.hasPendingWrites())
                                    "Local"
                                else
                                    "Server"

                                if (snapshot2 != null && snapshot2.exists()) {
                                    var itemList = SubModul(
                                        document.id,
                                        document.getString("name"),
                                        document.getString("type"),
                                        courseParent,
                                        modulParent,
                                        document.getString("prevSubModulId"),
                                        document.getString("prevModulParent"),
                                        document.getString("nextSubModulId"),
                                        document.getString("nextModulParent"),
                                        true
                                    )

                                    var dataExist = false
                                    var index = 0

                                    for(i in 1..subModul.size){
                                        if(subModul[i-1].subModulId == itemList.subModulId){
                                            dataExist = true
                                            index = i-1
                                        }
                                    }

                                    if(dataExist){
                                        subModul[index] = itemList
                                    }else{
                                        subModul.add(itemList)
                                    }

                                    Log.d(TAG, "$source data: ${snapshot2.data}")
                                } else {
                                    var itemList = SubModul(
                                        document.id,
                                        document.getString("name"),
                                        document.getString("type"),
                                        courseParent,
                                        modulParent,
                                        document.getString("prevSubModulId"),
                                        document.getString("prevModulParent"),
                                        document.getString("nextSubModulId"),
                                        document.getString("nextModulParent"),
                                        false
                                    )

                                    subModul.add(itemList)

                                    Log.d(TAG, "$source data: null")
                                }
                            }
                    }
                } else {
                    Log.d(TAG, "$source data: null")
                }
            }
        return subModul
    }

    private fun updateLog(material: Material){
        val userId = user.uid

        val updateData: MutableMap<String, Any> = mutableMapOf()
        updateData["isFinish"] = true
        updateData["dateFinish"] = FieldValue.serverTimestamp()

        val queryLog = db.collection("user")
            .document(userId!!)
            .collection("log")
            .document(material.courseParent!!+"-"+material.modulParent+"-"+material.subModulId)

        queryLog.get()
            .addOnSuccessListener { logDocument ->
                if (logDocument != null) {
                    val isFinish = logDocument.getBoolean("isFinish")

                    if (!isFinish!!) {
                        queryLog.set(updateData, SetOptions.merge())
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    private fun newLog(material: Material, type: String){
        val userId = user.uid

        val newData: MutableMap<String, Any> = mutableMapOf()
        newData["isFinish"] = false
        newData["dateOpen"] = FieldValue.serverTimestamp()
        newData["subModulId"] = material.subModulId!!
        newData["modulParent"] = material.modulParent!!
        newData["courseParent"] = material.courseParent!!
        newData["type"] = type

        val queryLog = db.collection("user")
            .document(userId!!)
            .collection("log")
            .document(material.courseParent!!+"-"+material.modulParent+"-"+material.subModulId)

        queryLog.get()
            .addOnSuccessListener { logDocument ->
                if (!logDocument.exists()) {
                    queryLog.set(newData)
                    Log.d("LOG","Create Log Success")
                }else{
                    Log.d("LOG","No Need New Log")
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }
}