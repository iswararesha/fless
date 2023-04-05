package com.resha.fless.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recent(
    val recentId: String? = null,
    val subModulName: String? = "Nama materi",
    val subModulId: String? = null,
    val modulParent: String? = null,
    val courseParent: String? = null,
    val date: Timestamp? = null,
    val type: String? = null
) : Parcelable

