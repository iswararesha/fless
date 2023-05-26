package com.resha.fless.ui.course

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.resha.fless.model.Course
import com.resha.fless.preference.UserPreference
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*


class CourseViewModel (private val userPref: UserPreference) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _courseData = MutableLiveData<List<Course>>()
    val courseData : LiveData<List<Course>> = _courseData

    private val user = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun searchCourse(key: String){
        _courseData

        _isLoading.value = true
        val userId = user.uid

        val searchKey = key.toUpperCase()

        db.collection("course").orderBy("searchKey").startAt(searchKey).endAt(searchKey + "\uf8ff")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var savedList : MutableList<Course> = mutableListOf()
                    _courseData.value=savedList
                    for (documents in task.result) {
                        db.collection("user").document(userId!!)
                            .collection("course").document(documents.id)
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
                                    var itemList = Course(
                                        documents.id,
                                        documents.getString("courseDescription"),
                                        documents.getString("courseObjective"),
                                        documents.getString("courseName"),
                                        documents.getLong("totalMaterial")?.toInt(),
                                        true,
                                        documents.getString("courseImg"),
                                        documents.getString("videoLink"),
                                        documents.getString("thumbnail")
                                    )

                                    Log.d("Course", itemList.courseId!!)

                                    var dataExist = false
                                    var index = 0

                                    for(i in 1..savedList.size){
                                        if(savedList[i-1].courseId == itemList.courseId){
                                            dataExist = true
                                            index = i-1
                                        }
                                    }

                                    if(dataExist){
                                        savedList[index] = itemList
                                    }else{
                                        savedList.add(itemList)
                                    }

                                    _courseData.value = savedList

                                    Log.d(TAG, "$source data: ${snapshot.data}")
                                } else {
                                    var itemList = Course(
                                        documents.id,
                                        documents.getString("courseDescription"),
                                        documents.getString("courseObjective"),
                                        documents.getString("courseName"),
                                        documents.getLong("totalMaterial")?.toInt(),
                                        false,
                                        documents.getString("courseImg"),
                                        documents.getString("videoLink"),
                                        documents.getString("thumbnail")
                                    )
                                    Log.d("Course", itemList.courseId!!)
                                    savedList.add(itemList)

                                    _courseData.value = savedList

                                    Log.d(TAG, "$source data: null")
                                }
                            }
                    }

                    _isLoading.value = false
                } else {
                    Log.w(TAG, "Error getting documents.", task.exception)
                }
            }
    }

    fun getCourse(){
        _isLoading.value = true
        val userId = user.uid

        db.collection("course")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var savedList : MutableList<Course> = mutableListOf()
                    _courseData.value=savedList
                    for (documents in task.result) {
                        db.collection("user").document(userId!!)
                            .collection("course").document(documents.id)
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
                                    var itemList = Course(
                                        documents.id,
                                        documents.getString("courseDescription"),
                                        documents.getString("courseObjective"),
                                        documents.getString("courseName"),
                                        documents.getLong("totalMaterial")?.toInt(),
                                        true,
                                        documents.getString("courseImg"),
                                        documents.getString("videoLink"),
                                        documents.getString("thumbnail")
                                    )

                                    Log.d("Course", itemList.courseId!!)

                                    var dataExist = false
                                    var index = 0

                                    for(i in 1..savedList.size){
                                        if(savedList[i-1].courseId == itemList.courseId){
                                            dataExist = true
                                            index = i-1
                                        }
                                    }

                                    if(dataExist){
                                        savedList[index] = itemList
                                    }else{
                                        savedList.add(itemList)
                                    }

                                    _courseData.value = savedList

                                    Log.d(TAG, "$source data: ${snapshot.data}")
                                } else {
                                    var itemList = Course(
                                        documents.id,
                                        documents.getString("courseDescription"),
                                        documents.getString("courseObjective"),
                                        documents.getString("courseName"),
                                        documents.getLong("totalMaterial")?.toInt(),
                                        false,
                                        documents.getString("courseImg"),
                                        documents.getString("videoLink"),
                                        documents.getString("thumbnail")
                                    )
                                    Log.d("Course", itemList.courseId!!)
                                    savedList.add(itemList)

                                    _courseData.value = savedList

                                    Log.d(TAG, "$source data: null")
                                }
                            }
                    }

                    _isLoading.value = false
                } else {
                    Log.w(TAG, "Error getting documents.", task.exception)
                }
            }
    }

    fun setStatus(course: Course){
        setCourseStatus(course)

        db.collection("course").document(course.courseId!!).collection("learningMaterial")
            .get()
            .addOnSuccessListener { result ->
                for (learningMaterial in result) {
                    db.collection("course").document(course.courseId!!)
                        .collection("learningMaterial").document(learningMaterial.id).collection("subLearningMaterial")
                        .get()
                        .addOnSuccessListener { subResult ->
                            for (subLearningMaterial in subResult) {
                                val courseParent = course.courseId
                                val modulParent = learningMaterial.id
                                val subModulId = subLearningMaterial.id
                                val type = subLearningMaterial.getString("type")

                                val dateOpenNumber = subLearningMaterial.getLong("dateOpen") ?: 0
                                val deadLineNumber = subLearningMaterial.getLong("deadLine") ?: 0

                                val z = ZoneId.systemDefault()
                                val today: LocalDate = LocalDate.now(z)
                                val startOfToday: ZonedDateTime = today.atStartOfDay(z)

                                val localDateOpen = startOfToday.plusDays(dateOpenNumber)
                                val localDeadLine = startOfToday.plusDays(deadLineNumber)

                                val serverDateOpen = Timestamp(Date.from(localDateOpen.toInstant()))
                                val serverDeadLine = Timestamp(Date.from(localDeadLine.toInstant()))

                                val subModulData: MutableMap<String, Any> = mutableMapOf()
                                subModulData["isFinish"] = false
                                subModulData["dateOpen"] = serverDateOpen
                                subModulData["deadLine"] = serverDeadLine
                                subModulData["type"] = type.toString()

                                val task: MutableMap<String, Any> = mutableMapOf()
                                task["courseName"] = course.courseName!!
                                task["modulName"] = learningMaterial.getString("modulName").toString()
                                task["taskName"] = subLearningMaterial.getString("name").toString()
                                task["subModulId"] = subModulId
                                task["modulParent"] = modulParent
                                task["courseParent"] = courseParent
                                task["isFinish"] = false
                                task["dateOpen"] = serverDateOpen
                                task["deadLine"] = serverDeadLine

                                setSubModulStatus(courseParent, modulParent, subModulId, subModulData)
                                if(type == "task"){
                                    setTask(courseParent, modulParent, subModulId, task)
                                }
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d(TAG, "Error getting documents: ", exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    private fun setCourseStatus(course: Course){
        _isLoading.value = true
        val courseData: MutableMap<String, Any> = mutableMapOf()
        courseData["isOpen"] = true

        db.collection("user")
            .document(user.uid!!)
            .collection("course")
            .document(course.courseId!!)
            .set(courseData)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot written with ID: $documentReference")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
        _isLoading.value = false
    }

    private fun setSubModulStatus(course: String, modul: String, subModul: String, data: MutableMap<String, Any>){
        db.collection("user")
            .document(user.uid!!)
            .collection("course")
            .document(course)
            .collection("modul")
            .document(modul)
            .collection("subModul")
            .document(subModul)
            .set(data)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot written with ID: $documentReference")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    private fun setTask(course: String, modul: String, subModul: String, data: MutableMap<String, Any>){
        db.collection("user")
            .document(user.uid!!)
            .collection("task")
            .document("$course-$modul-$subModul")
            .set(data)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot written with ID: $documentReference")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }
}