package com.resha.fless.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SubModul(
    val subModulId: String? = null,
    val name: String? = null,
    val type: String? = null,
    val courseParent: String? = null,
    val modulParent: String? = null
) : Parcelable