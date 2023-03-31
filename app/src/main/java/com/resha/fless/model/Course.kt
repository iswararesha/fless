package com.resha.fless.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Course(
    val courseId: String? = null,
    val courseDescription: String? = null,
    val courseName: String? = null,
    val totalMaterial: Int? = null,
    val isOpen: Boolean? = false
) : Parcelable