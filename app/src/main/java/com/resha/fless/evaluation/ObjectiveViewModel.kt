package com.resha.fless.evaluation

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.resha.fless.model.Answer
import com.resha.fless.model.Material
import com.resha.fless.model.Objective
import com.resha.fless.model.UserPreference

class ObjectiveViewModel (private val pref: UserPreference) : ViewModel()  {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _objectiveData = MutableLiveData<List<Objective>>()
    val objectiveData : LiveData<List<Objective>> = _objectiveData

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

                    _isLoading.value = false
                }
            }

        _isLoading.value = false
    }

    fun sendAnswer(material: Material, number: Int, answer: ArrayList<Answer>){
        _isLoading.value = true

        val db = FirebaseFirestore.getInstance()

        db.collection("course")
            .document(material.courseParent!!)
            .collection("learningMaterial")
            .document(material.modulParent!!)
            .collection("answerEvaluation")
            .document(material.subModulId!!)
            .get()
            .addOnSuccessListener  { task ->
                if (task != null) {
                    Log.e(TAG, task.id)

                    val rightAnswer = ArrayList<Answer>()
                    for(i in answer){
                        rightAnswer.add(Answer(
                            i.number,
                            task.getString("${i.number}"))
                        )
                    }

                    var total = 0
                    for (i in 1..number){
                        if(rightAnswer[i-1] == answer[i-1]) {
                            total += 1
                        }
                    }
                    val user = Firebase.auth.currentUser
                    val uid = user?.uid

                    val data: MutableMap<String, Any> = mutableMapOf()

                    val score = total.toFloat()/number.toFloat()

                    data["user"] = uid!!
                    data["score"] = "$score"
                    data["dateAttempt"] = FieldValue.serverTimestamp()

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

                            _isLoading.value = false
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)

                            _isLoading.value = false
                        }

                }
            }.addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)

                _isLoading.value = false
            }
    }
}