package com.resha.fless.evaluation

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.resha.fless.model.Answer
import com.resha.fless.model.Essay
import com.resha.fless.model.Material
import com.resha.fless.model.UserPreference

class EssayViewModel (private val pref: UserPreference) : ViewModel()  {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _essayData = MutableLiveData<Essay>()
    val essayData : LiveData<Essay> = _essayData

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
                        task.getString("fieldNumber")?.toInt()
                    )
                    _essayData.value = itemList

                    _isLoading.value = false
                }
            }
            .addOnFailureListener{ exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }
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
                    Log.e(ContentValues.TAG, task.id)

                    val rightAnswer = ArrayList<Answer>()
                    for(i in 1..number){
                        rightAnswer.add(Answer(
                            "number$i",
                            task.getString("number$i"))
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
                            Log.d(ContentValues.TAG, "DocumentSnapshot written with ID: ${documentReference.id}")

                            _isLoading.value = false
                        }
                        .addOnFailureListener { e ->
                            Log.w(ContentValues.TAG, "Error adding document", e)

                            _isLoading.value = false
                        }

                }
            }.addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)

                _isLoading.value = false
            }
    }
}