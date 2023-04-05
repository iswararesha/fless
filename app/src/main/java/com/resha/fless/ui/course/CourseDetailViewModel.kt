package com.resha.fless.ui.course

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.resha.fless.model.Modul
import com.resha.fless.model.SubModul
import com.resha.fless.model.UserPreference

class CourseDetailViewModel(private val pref: UserPreference) : ViewModel()  {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _modulData = MutableLiveData<List<Modul>>()
    val modulData : LiveData<List<Modul>> = _modulData

    fun getModul(courseParent: String){
        _isLoading.value = true

        val db = FirebaseFirestore.getInstance()

        val user = FirebaseAuth.getInstance()
        val userId = user.uid

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
}