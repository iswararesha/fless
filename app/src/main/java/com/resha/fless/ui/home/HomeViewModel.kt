package com.resha.fless.ui.home

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.jjoe64.graphview.series.DataPoint
import com.resha.fless.model.Recent
import com.resha.fless.model.User
import com.resha.fless.preference.UserPreference
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class HomeViewModel (private val userPref: UserPreference) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _recent = MutableLiveData<List<Recent>>()
    val recent: LiveData<List<Recent>> = _recent

    private val _recentData = MutableLiveData<List<Recent>>()
    val recentData : LiveData<List<Recent>> = _recentData

    private val _suggestionData = MutableLiveData<String>()
    val suggestionData: LiveData<String> = _suggestionData

    private val _pointData = MutableLiveData<List<DataPoint>>()
    val pointData: LiveData<List<DataPoint>> = _pointData

    private val _totalMaterialData = MutableLiveData<Int>()
    val totalMaterialData: LiveData<Int> = _totalMaterialData

    private val _totalEvaluationData = MutableLiveData<Int>()
    val totalEvaluationData: LiveData<Int> = _totalEvaluationData

    private val _totalTaskData = MutableLiveData<Int>()
    val totalTaskData: LiveData<Int> = _totalTaskData

    private val user = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _userData = MutableLiveData<User>()
    val userDetail : LiveData<User> = _userData

    fun getUser() {
        _isLoading.value = true

        val user = Firebase.auth.currentUser

        _userData.value = User(
            user?.uid!!,
            user?.email!!,
            user?.displayName!!,
            true
        )

        _isLoading.value = false
    }

    fun getGraph(){
        _isLoading.value = true
        var savedList : MutableList<Recent> = mutableListOf()
        _recent.value = savedList

        val z = ZoneId.systemDefault()
        val today: LocalDate = LocalDate.now(z)
        val startOfToday: ZonedDateTime = today.atStartOfDay(z)

        val userId = user.uid

        val date = startOfToday.minusDays(2)

        val queryLog = db.collection("user").document(userId!!)
            .collection("log")

        queryLog.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot != null) {
                    var emptyList: MutableList<Recent> = mutableListOf()
                    _recent.value = emptyList
                    for (documents in snapshot.documents) {
                        val dataDate = documents.getTimestamp("date")?.toDate()
                        val d = ZonedDateTime.ofInstant(dataDate?.toInstant(), z)
                        if(d!! >= date) {
                            db.collection("course")
                                .document(documents.getString("courseParent")!!)
                                .collection("learningMaterial")
                                .document(documents.getString("modulParent")!!)
                                .collection("subLearningMaterial")
                                .document(documents.getString("subModulId")!!)
                                .get()
                                .addOnSuccessListener { subModul ->
                                    val itemList = Recent(
                                        documents.id,
                                        subModul.getString("name"),
                                        documents.getString("subModulId"),
                                        documents.getString("modulParent"),
                                        documents.getString("courseParent"),
                                        documents.getTimestamp("date"),
                                        documents.getString("type")
                                    )
                                    savedList.add(itemList)
                                    Log.d("DATE", itemList.recentId.toString())

                                    _recent.value = savedList
                                }.addOnFailureListener { exception ->
                                    Log.w(TAG, "Error getting documents.", exception)
                                }
                        }
                    }
                }
            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }


        _isLoading.value = false
    }

    fun getSuggestion(data: List<Recent>){
        var response = "Kamu belum memiliki progress dalam 3 hari terakhir, ayo kita mulai untuk belajar!"


        val z = ZoneId.systemDefault()
        val today: LocalDate = LocalDate.now(z)
        val startOfToday: ZonedDateTime = today.atStartOfDay(z)

        val date = ArrayList<ZonedDateTime>()
        date.add(startOfToday)
        date.add(startOfToday.minusDays(1))
        date.add(startOfToday.minusDays(2))

        var longestProgressInDay = 0
        var longestDay = 0

        val tempProgress = intArrayOf(0, 0, 0)

        for(logData in data) {
            val d = ZonedDateTime.ofInstant(logData.date?.toDate()?.toInstant(), z)

            if (d >= date[2] && d <= date[1]) {
                tempProgress[2] += 1
            } else if (d >= date[1] && d <= date[0]) {
                tempProgress[1] += 1
            } else if (d >= date[0]) {
                tempProgress[0] += 1
            }
        }

        for (day in 1..tempProgress.size) {
            if (tempProgress[day-1] >= longestProgressInDay) {
                longestProgressInDay = tempProgress[day-1]
                longestDay = day
            }
        }

        Log.d("MOST", longestProgressInDay.toString())
        Log.d("MOSTDAY", longestDay.toString())

        if(longestProgressInDay >= 11){
            when(longestDay){
                1 -> response = "Kamu melakukan pembelajaran yang luar biasa hari ini!"
                2 -> response = "Kamu melakukan pembelajaran yang luar biasa kemarin, semoga hari ini bisa seperti kemarin!"
                3 -> response = "Kamu melakukan pembelajaran yang luar dalam 3 hari kebelakang, semoga hari ini bisa menjadi lebih baik!"
            }
        }else if (longestProgressInDay in 10 downTo 6){
            when(longestDay){
                1 -> response = "Kamu melakukan pembelajaran dengan baik hari ini. Selagi masih ada waktu, ayo tingkatkan lagi kegiatan pembelajaran hari ini!"
                2 -> response = "Kamu melakukan pembelajaran dengan baik kemarin, semoga hari ini bisa lebih baik dari kemarin!"
                3 -> response = "Kamu melakukan pembelajaran dengan baik dalam 3 hari kebelakang, hari ini harus bisa menjadi lebih baik!"
            }
        }else if (longestProgressInDay in 5 downTo 1){
            when(longestDay){
                1 -> response = "Kamu belum mencapai target pembelajaran yang baik hari ini. Ayo tingkatkan lagi!"
                2 -> response = "Kamu tidak mencapai target pembelajaran yang baik kemarin. Hari ini harus bisa menjadi lebih baik!"
                3 -> response = "Kamu tidak mencapai target pembelajaran yang baik dalam 3 hari kebelakang. Ayo semangat, hari ini pasti bisa lebih baik!"
            }
        }

        _suggestionData.value = response
    }


    fun getPoint(data: List<Recent>){
        var savedList : MutableList<DataPoint> = mutableListOf()
        _pointData.value = savedList

        val z = ZoneId.systemDefault()
        val today: LocalDate = LocalDate.now(z)
        val startOfToday: ZonedDateTime = today.atStartOfDay(z)

        val date = ArrayList<ZonedDateTime>()
        date.add(startOfToday)
        date.add(startOfToday.minusDays(1))
        date.add(startOfToday.minusDays(2))

        var oldestData = startOfToday
        for(logData in data){
            val d = ZonedDateTime.ofInstant(logData.date?.toDate()?.toInstant(), z)

            if(d <= oldestData)
                oldestData = d
        }

        val tempProgress = intArrayOf(0, 0, 0)

        for (logData in data){
            val d = ZonedDateTime.ofInstant(logData.date?.toDate()?.toInstant(), z)

            if(d>=date[2] && d<=date[1]) {
                tempProgress[2] += 1
            }else if(d>=date[1] && d<=date[0]) {
                tempProgress[1] += 1
            }else if(d>=date[0]) {
                tempProgress[0] += 1
            }
        }

        for (i in tempProgress.size - 1 downTo 0) {
            val d = Date.from(date[i].toInstant())

            val point = DataPoint(d, tempProgress[i].toDouble())
            Log.d("POINT", "${date[i]} ${tempProgress[i].toDouble()}")

            savedList.add(point)
        }

        _pointData.value = savedList
    }

    fun getTotal(data: List<Recent>) {
        var material = 0
        var evaluation = 0
        var task = 0

        val z = ZoneId.systemDefault()
        val today: LocalDate = LocalDate.now(z)
        val startOfToday: ZonedDateTime = today.atStartOfDay(z)

        for (logData in data) {

            val d = ZonedDateTime.ofInstant(logData.date?.toDate()?.toInstant(), z)

            if(d>=startOfToday){
                when (logData.type.toString()) {
                    "material" -> {
                        material++
                    }
                    "task" -> {
                        task++
                    }
                    else -> {
                        evaluation++
                    }
                }
            }
        }

        _totalMaterialData.value = material
        _totalEvaluationData.value = evaluation
        _totalTaskData.value = task
    }

    fun getRecent() {
        _isLoading.value = true
        var savedList: MutableList<Recent> = mutableListOf()
        _recentData.value = savedList

        val userId = user.uid

        val queryLog = db.collection("user").document(userId!!)
            .collection("recent").orderBy("date", Query.Direction.DESCENDING).limit(3)

        queryLog.get()
            .addOnSuccessListener { result ->
                var emptyList: MutableList<Recent> = mutableListOf()
                _recent.value = emptyList
                for (documents in result) {
                    db.collection("course")
                        .document(documents.getString("courseParent")!!)
                        .collection("learningMaterial")
                        .document(documents.getString("modulParent")!!)
                        .collection("subLearningMaterial")
                        .document(documents.getString("subModulId")!!)
                        .get()
                        .addOnSuccessListener { subModul ->
                            val itemList = Recent(
                                documents.id,
                                subModul.getString("name"),
                                documents.getString("subModulId"),
                                documents.getString("modulParent"),
                                documents.getString("courseParent"),
                                documents.getTimestamp("date"),
                                documents.getString("type")
                            )
                            savedList.add(itemList)
                            Log.d("DATE", itemList.recentId.toString())

                            _recentData.value = savedList
                        }.addOnFailureListener { exception ->
                            Log.w(TAG, "Error getting documents.", exception)
                        }
                }
            }.addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
        _isLoading.value = false
    }
}