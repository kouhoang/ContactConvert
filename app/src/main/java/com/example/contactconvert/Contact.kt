package com.example.contactconvert

data class Contact(
    val id: String,
    val name: String,
    val phoneNumber: String,
    var isSelected: Boolean = false,
)
