package com.resha.fless.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Course(
    val courseId: String? = "ID mata kuliah",
    val courseDescription: String? = "Deskripsi Mata Kuliah",
    val courseObjective: String? = "Tujuan Mata Kuliah",
    val courseName: String? = "Nama Mata Kuliah",
    val totalMaterial: Int? = 0,
    val isOpen: Boolean? = false,
    val courseImg: String? = "loading",
    val videoLink: String? = null,
    val thumbnail: String? = null
) : Parcelable