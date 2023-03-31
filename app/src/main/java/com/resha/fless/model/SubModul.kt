package com.resha.fless.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SubModul(
    val subModulId: String? = null,
    val name: String? = null,
    val type: String? = null,
    val courseParent: String? = null,
    val modulParent: String? = null,
    val prevSubModulId: String? = null,
    val prevModulParent: String? = null,
    val nextSubModulId: String? = null,
    val nextModulParent: String? = null,
    val isOpen: Boolean? = false
) : Parcelable