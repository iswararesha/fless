package com.resha.fless.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Modul(
    val modulId: String? = null,
    val modulDescription: String? = null,
    val modulName: String? = null,
    val videoLink: String? = null,
    val hourNeed: String? = null,
    val subModul: List<SubModul>? = null
) : Parcelable