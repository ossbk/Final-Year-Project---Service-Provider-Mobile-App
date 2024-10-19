package com.services.provider.domain.model

data class UserProfile(
    val user: User = User(),
    var services: List<SkilledService> = emptyList(),
    var ratings: List<UserRating> = emptyList()
)