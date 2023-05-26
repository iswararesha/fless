package com.resha.fless.ui.task

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.resha.fless.model.Task
import com.resha.fless.preference.UserPreference

class TaskViewModel (private val userPref: UserPreference) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _taskData = MutableLiveData<List<Task>>()
    val taskData: LiveData<List<Task>> = _taskData

    private val user = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun getOnGoingTask(){
        val userId = user.uid

        val queryLog = db.collection("user").document(userId!!)
            .collection("task").whereLessThan("dateOpen", Timestamp.now())

        queryLog.addSnapshotListener {
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
                var savedList : MutableList<Task> = mutableListOf()
                for (documents in snapshot.documents) {
                    if(documents.getBoolean("isFinish") == false){
                        val itemList = Task(
                            documents.id,
                            documents.getString("courseName"),
                            documents.getString("modulName"),
                            documents.getString("taskName"),
                            documents.getString("subModulId"),
                            documents.getString("modulParent"),
                            documents.getString("courseParent"),
                            documents.getTimestamp("dateOpen"),
                            documents.getTimestamp("deadLine"),
                            documents.getTimestamp("finishDate"),
                            documents.getBoolean("isFinish")
                        )

                        savedList.add(itemList)
                    }
                }
                _taskData.value = savedList
            } else {
                Log.d(TAG, "$source data: null")
            }
        }
    }

    fun getFinishTask(){
        val userId = user.uid

        val queryLog = db.collection("user").document(userId!!)
            .collection("task").whereLessThan("dateOpen", Timestamp.now())

        queryLog.addSnapshotListener {
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
                var savedList : MutableList<Task> = mutableListOf()
                for (documents in snapshot.documents) {
                    if(documents.getBoolean("isFinish") == true){
                        val itemList = Task(
                            documents.id,
                            documents.getString("courseName"),
                            documents.getString("modulName"),
                            documents.getString("taskName"),
                            documents.getString("subModulId"),
                            documents.getString("modulParent"),
                            documents.getString("courseParent"),
                            documents.getTimestamp("dateOpen"),
                            documents.getTimestamp("deadLine"),
                            documents.getTimestamp("finishDate"),
                            documents.getBoolean("isFinish")
                        )

                        savedList.add(itemList)

                    }
                }
                _taskData.value = savedList
            } else {
                Log.d(TAG, "$source data: null")
            }
        }
    }
}