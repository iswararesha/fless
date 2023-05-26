package com.resha.fless.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class SubModul(
    val subModulId: String? = null,
    val name: String? = null,
    val type: String? = null,
    val courseParent: String? = null,
    val modulParent: String? = null,
    val prevSubModulId: String? = null,
    val prevModulParent: String? = null,
    val nextSubModulId: String? = null,
    val nextModulParent: String? = null,
    val dateOpen: Timestamp? = null,
    val deadLine: Timestamp? = null,
    val isFinish: Boolean? = false,
    val finishDate: Timestamp? = null
) : Parcelable