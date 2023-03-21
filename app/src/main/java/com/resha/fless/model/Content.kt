package com.resha.fless.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Content(
    val contentId: String? = null,
    val content: String? = null,
    val type: String? = null
) : Parcelable