package com.resha.fless.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Modul(
    val modulId: String? = null,
    val modulName: String? = null,
    val totalMaterial: Int? = 0,
    val subModul: List<SubModul>? = null
) : Parcelable