package com.example.socialadda.models

data class Post(
    val text: String = "",
    val createdBy: User = User(),
    val createdAt: Long = 0L,
    //we only store the ids of the users so a array of string is sufficient for that
    val likedBy: ArrayList<String> = ArrayList()
)