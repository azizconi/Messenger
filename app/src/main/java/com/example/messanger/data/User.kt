package com.example.messanger.data

import android.os.Parcelable

import kotlinx.android.parcel.Parcelize

@Parcelize
class User(

    val uid: String,

    val username: String,
    val profileImgUrl: String,
    val password: String
): Parcelable {
    constructor(): this("", "", "", "")
}
