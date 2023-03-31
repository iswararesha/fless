package com.resha.fless.ui.task

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.resha.fless.model.Attempt
import com.resha.fless.model.Course
import com.resha.fless.model.TaskModel
import com.resha.fless.model.UserPreference
import java.text.SimpleDateFormat
import java.util.*

class TaskViewModel (private val userPref: UserPreference) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _taskData = MutableLiveData<List<TaskModel>>()
    val taskData: LiveData<List<TaskModel>> = _taskData

    private val user = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun getOnGoingTask(){
        val userId = user.uid

        val queryLog = db.collection("user").document(userId!!)
            .collection("log").whereEqualTo("isFinish", false).whereEqualTo("type", "task")

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
                    var savedList : MutableList<TaskModel> = mutableListOf()
                    _taskData.value = savedList
                    for (documents in snapshot.documents) {
                        val dateOpen = documents.getTimestamp("dateOpen")?.toDate()?.let {
                            SimpleDateFormat("dd MMMM yyyy - hh:mm a", Locale("id", "ID")).format(
                                it)
                        }

                        val itemList = TaskModel(
                            documents.id,
                            documents.getString("subModulId"),
                            documents.getString("modulParent"),
                            documents.getString("courseParent"),
                            dateOpen,
                            "Tugas belum selesai",
                            documents.getBoolean("isFinish")

                        )

                        savedList.add(itemList)
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
            .collection("log").whereEqualTo("isFinish", true).whereEqualTo("type", "task")

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
                var savedList : MutableList<TaskModel> = mutableListOf()
                _taskData.value = savedList
                for (documents in snapshot.documents) {
                    val dateOpen = documents.getTimestamp("dateOpen")?.toDate()?.let {
                        SimpleDateFormat("dd MMMM yyyy - hh:mm a", Locale("id", "ID")).format(
                            it)
                    }

                    val dateFinish = documents.getTimestamp("dateFinish")?.toDate()?.let {
                        SimpleDateFormat("dd MMMM yyyy - hh:mm a", Locale("id", "ID")).format(
                            it)
                    }
                    val itemList = TaskModel(
                        documents.id,
                        documents.getString("subModulId"),
                        documents.getString("modulParent"),
                        documents.getString("courseParent"),
                        dateOpen,
                        dateFinish,
                        documents.getBoolean("isFinish")

                    )

                    savedList.add(itemList)
                }
                _taskData.value = savedList
            } else {
                Log.d(TAG, "$source data: null")
            }
        }
    }
}