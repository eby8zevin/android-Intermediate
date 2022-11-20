package com.ahmadabuhasan.storyapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Token(
    var token: String? = null
) : Parcelable