package com.raj.weathertodo.utils

interface ApiListner {
    fun onResponse(data : String)

    fun onFailure()
}