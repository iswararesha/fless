package com.resha.fless.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    val userId: String,
    val email : String,
    val name: String,
    val token: String,
    val isLogin: Boolean
) : Parcelable