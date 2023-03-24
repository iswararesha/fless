package com.resha.fless.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.sql.Timestamp
import java.util.*

@Parcelize
data class Attempt(
    val user: String? = null,
    val score: String? = null,
    val dateAttempt: com.google.firebase.Timestamp? = null
) : Parcelable