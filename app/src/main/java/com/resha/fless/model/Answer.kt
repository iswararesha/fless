package com.resha.fless.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Answer(
    val number: String? = null,
    var answer: String? = null
) : Parcelable