package com.resha.fless.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val userId: String,
    val email : String,
    val name: String,
    val isLogin: Boolean
) : Parcelable