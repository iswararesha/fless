package com.resha.fless.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Essay(
    val essayId: String? = null,
    val question: String? = null,
    val programImg: String? = null,
    val questionImg: String? = null,
    val fieldNumber: Int? = null
) : Parcelable