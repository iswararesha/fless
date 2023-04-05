package com.resha.fless.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Course(
    val courseId: String? = "ID mata kuliah",
    val courseDescription: String? = "Deskripsi Mata Kuliah",
    val courseName: String? = "Nama Mata Kuliah",
    val totalMaterial: Int? = 0,
    val isOpen: Boolean? = false,
    val courseImg: String? = "loading",
    val videoLink: String? = "https://firebasestorage.googleapis.com/v0/b/fless-570f7.appspot.com/o/Elementor%20video%20placeholder.mp4?alt=media&token=5fc199b0-6586-41b3-b64e-988131dc28fe"
) : Parcelable