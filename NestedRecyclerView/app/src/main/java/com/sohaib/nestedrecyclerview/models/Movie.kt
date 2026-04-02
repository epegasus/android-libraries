package com.sohaib.nestedrecyclerview.models

data class Movie(
    val id: Int,
    val imageId: Int,
    val title: String,
    val categoryId: Int,
    val createdDate: Long = System.currentTimeMillis()
)