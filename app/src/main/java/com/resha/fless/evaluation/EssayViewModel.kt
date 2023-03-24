package com.resha.fless.evaluation

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.resha.fless.model.*

class EssayViewModel (private val pref: UserPreference) : ViewModel()  {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _essayData = MutableLiveData<Essay>()
    val essayData : LiveData<Essay> = _essayData

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun getEssay(material: Material){
        _isLoading.value = true

        val db = FirebaseFirestore.getInstance()

        db.collection("course")
            .document(material.courseParent!!)
            .collection("learningMaterial")
            .document(material.modulParent!!)
            .collection("subLearningMaterial")
            .document(material.subModulId!!)
            .collection("contentLearningMaterial")
            .document("001")
            .get()
            .addOnSuccessListener { task ->
                if (task != null) {
                    val itemList = Essay(
                        task.id,
                        task.getString("question"),
                        task.getString("programImg"),
                        task.getString("questionImg"),
                        task.getLong("fieldNumber")?.toInt()
                    )
                    _essayData.value = itemList

                    _isLoading.value = false
                }
            }
            .addOnFailureListener{ exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
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
                Log.d(ContentValues.TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }

        _isLoading.value = false
    }
}