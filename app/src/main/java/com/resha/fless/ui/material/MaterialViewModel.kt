package com.resha.fless.ui.material

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.resha.fless.model.Content
import com.resha.fless.model.UserPreference

class MaterialViewModel (private val userPref: UserPreference) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _materialData = MutableLiveData<List<Content>>()
    val materialData : LiveData<List<Content>> = _materialData

    fun getContent(courseParent: String, modulParent: String, subModulParent: String){
        _isLoading.value = true

        val db = FirebaseFirestore.getInstance()

        db.collection("course")
            .document(courseParent)
            .collection("learningMaterial")
            .document(modulParent)
            .collection("subLearningMaterial")
            .document(subModulParent)
            .collection("contentLearningMaterial")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var savedList : MutableList<Content> = mutableListOf()
                    for (documents in task.result) {
                        var itemList = Content(
                            documents.id,
                            documents.getString("content"),
                            documents.getString("type")
                        )
                        if(itemList != null){
                            savedList.add(itemList)
                        }
                    }
                    _materialData.value = savedList

                    _isLoading.value = false
                } else {
                    Log.w(ContentValues.TAG, "Error getting documents.", task.exception)
                }
            }
    }
}