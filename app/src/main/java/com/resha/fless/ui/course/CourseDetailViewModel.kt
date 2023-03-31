package com.resha.fless.ui.course

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.resha.fless.model.Modul
import com.resha.fless.model.SubModul
import com.resha.fless.model.UserModel
import com.resha.fless.model.UserPreference

class CourseDetailViewModel(private val pref: UserPreference) : ViewModel()  {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _modulData = MutableLiveData<List<Modul>>()
    val modulData : LiveData<List<Modul>> = _modulData

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun getModul(courseParent: String){
        _isLoading.value = true

        val db = FirebaseFirestore.getInstance()

        db.collection("course").document(courseParent).collection("learningMaterial")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var savedList : MutableList<Modul> = mutableListOf()
                    for (documents in task.result) {
                        var itemList = Modul(
                            documents.id,
                            documents.getString("modulName"),
                            documents.getLong("totalMaterial")?.toInt(),
                            getSubModul(courseParent, documents.id)
                        )
                        if(itemList != null){
                            savedList.add(itemList)
                        }
                    }
                    _modulData.value = savedList

                    _isLoading.value = false
                } else {
                    Log.w(TAG, "Error getting documents.", task.exception)
                }
            }
    }

    private fun getSubModul(courseParent: String, modulParent: String): List<SubModul>{
        val subModul : MutableList<SubModul> = mutableListOf()

        val db = FirebaseFirestore.getInstance()

        db.collection("course")
            .document(courseParent)
            .collection("learningMaterial")
            .document(modulParent)
            .collection("subLearningMaterial")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        var itemList = SubModul(
                            document.id,
                            document.getString("name"),
                            document.getString("type"),
                            courseParent,
                            modulParent,
                            document.getString("prevSubModulId"),
                            document.getString("prevModulParent"),
                            document.getString("nextSubModulId"),
                            document.getString("nextModulParent")
                        )
                        subModul.add(itemList)
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.exception)
                }
            }
        return subModul
    }
}