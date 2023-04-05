package com.resha.fless.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Progress(
    val taskId: String? = null,
    val subModulId: String? = null,
    val modulParent: String? = null,
    val courseParent: String? = null,
    val dateOpen: String? = null,
    val dateFinish: String? = "Tugas belum selesai",
    val isFinish: Boolean? = false,
    val type: String? = null
) : Parcelable
