package com.resha.fless.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserStatus(
    val id: String? = null,
    val status: String? = null,
    val isOpen: Boolean? = false,
    val dateAttempt: String? = null
) : Parcelable