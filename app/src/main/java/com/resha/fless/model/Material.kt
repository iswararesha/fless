package com.resha.fless.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Material(
    val subModulId: String? = null,
    val courseParent: String? = null,
    val modulParent: String? = null
) : Parcelable