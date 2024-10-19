package com.services.provider.domain.model

import android.net.Uri
import com.google.firebase.firestore.Exclude


data class User(
    var id: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var qualification: String = "",
    var email: String = "",
    val phoneNo: String = "",
    var password: String = "",
    val skilledType: String = "",
    var profilePhoto: String = "",
    var countryName: String = "",
    var userDetails: String = "",
    var status: UserStatus = UserStatus.PENDING,

    @get:Exclude var imageUri: Uri? = null,
) {
    fun getName(): String {
        return "$firstName $lastName"
    }
}


enum class UserStatus{
    PENDING,
    DELETED,
    DISABLED,
    APPROVED,
    REJECTED
}