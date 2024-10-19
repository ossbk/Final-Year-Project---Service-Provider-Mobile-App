package com.services.provider.domain.model

import android.net.Uri
import com.google.firebase.firestore.Exclude

data class SkilledService(
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var price: String = "",
    var imageUrl: String = "",
    @get:Exclude var imageUri: Uri? = null,
    var serviceRating: Float = 0f,
    var serviceNoOfPeopleRated: Int = 0,
    var userId: String = "",
    var serviceCategory: String = "",
    var user: User = User(),
    val ratings: HashMap<String, List<UserRating>> = hashMapOf()
)

val serviceCategories = listOf(
    "Home Maintenance and Repair",
    "Cleaning Services",
    "Landscaping and Outdoor Services",
    "Construction and Remodeling",
    "Automotive Services",
    "Professional Services",
    "Health and Wellness",
    "Event Services",
    "Beauty and Personal Care",
    "Technology and Electronics",
    "Pet Services",
    "Education and Tutoring",
    "Transportation Services",
    "Fitness and Sports",
    "Specialized Services",
)

