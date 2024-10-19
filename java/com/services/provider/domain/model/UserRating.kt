package com.services.provider.domain.model

data class UserRating(
    val userId: String = "",
    var userProfilePic: String = "",
    var userName: String = "",
    var userEmail: String = "",
    val rating: Float = 0.0f,
    val ratingMsg: String = "",
    var country: String = ""
){
    fun updateUserDetails(user: User):UserRating{
        userProfilePic=user.profilePhoto
        userName=user.firstName
        userEmail=user.email
        country=user.countryName
        return this
    }
}