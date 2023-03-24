package com.resha.fless.evaluation

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.resha.fless.model.*

class ObjectiveViewModel (private val pref: UserPreference) : ViewModel()  {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _objectiveData = MutableLiveData<List<Objective>>()
    val objectiveData : LiveData<List<Objective>> = _objectiveData

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun getObjective(material: Material){
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
                    val savedList : MutableList<Objective> = mutableListOf()
                    for (documents in task.result) {
                        val itemList = Objective(
                            documents.id,
                            documents.getString("question"),
                            documents.getString("optionA"),
                            documents.getString("optionB"),
                            documents.getString("optionC"),
                            documents.getString("optionD"),
                            documents.getString("optionE")
                        )
                        savedList.add(itemList)
                    }
                    _objectiveData.value = savedList

                    _isLoading.value = false
                } else {
                    Log.w(ContentValues.TAG, "Error getting documents.", task.exception)
                }
            }
    }

    fun sendAnswer(material: Material, answer: List<Answer>){
        _isLoading.value = true

        val db = FirebaseFirestore.getInstance()
        val user = Firebase.auth.currentUser
        val uid = user?.uid

        val data: MutableMap<String, String> = mutableMapOf()
        data["user"] = uid!!
        data.putAll(answer.associate { it.number!! to it.answer!! })

        db.collection("course")
            .document(material.courseParent!!)
            .collection("learningMaterial")
            .document(material.modulParent!!)
            .collection("subLearningMaterial")
            .document(material.subModulId!!)
            .collection("studentAttempt")
            .add(data)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

        _isLoading.value = false
    }
}