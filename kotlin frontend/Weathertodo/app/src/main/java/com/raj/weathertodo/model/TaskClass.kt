package com.raj.weathertodo.model

data class TaskClass(
    val audio: String,
    val created_at: String,
    val description: String,
    val id: Int,
    val image: String,
    val title: String,
    val updated_at: String,
    val user_id: Int,
    val video: String,
    var status: Int
)