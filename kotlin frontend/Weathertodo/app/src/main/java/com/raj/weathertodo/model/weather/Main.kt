package com.raj.weathertodo.model.weather

data class Main(
    val feels_like: Double,
    val humidity: Int,
    val pressure: Int,
    val temp: Float,
    val temp_max: Float,
    val temp_min: Float
)