package com.resha.fless.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    val taskId: String? = null,
    val courseName: String? = null,
    val modulName: String? = null,
    val taskName: String? = null,
    val subModulId: String? = null,
    val modulParent: String? = null,
    val courseParent: String? = null,
    val dateOpen: Timestamp? = null,
    val deadLine: Timestamp? = null,
    val finishDate: Timestamp? = null,
    val isFinish: Boolean? = false
) : Parcelable
