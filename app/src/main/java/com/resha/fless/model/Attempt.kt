package com.resha.fless.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Attempt(
    val user: String? = null,
    val score: String? = null,
    val dateAttempt: String? = null
) : Parcelable