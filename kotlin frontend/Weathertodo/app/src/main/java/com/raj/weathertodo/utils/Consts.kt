package com.raj.weathertodo.utils


//distance api url
//http://api.openweathermap.org/data/2.5/weather?lat=21.2193&lon=72.8419&units=metric&appid=1890c332f5dd8f6cce75b7faaa746329
//const val BASE_URL = "http://192.168.1.9:80/api/"
//const val BASE_URL = "http://todo.codealphainfotech.com/public/api/"
const val BASE_URL = "http://finalproject.site/todo/public/api/"
const val url_getAllTask = "${BASE_URL}todolist"
const val url_register = "${BASE_URL}userregistration"
const val url_login = "${BASE_URL}userlogin"
const val url_addtodo = "${BASE_URL}addtodo"
const val url_todostatus = "${BASE_URL}todostatus"
const val url_updatetodo = "${BASE_URL}updatetodo"
const val url_tododelete = "${BASE_URL}tododelete"

//sharerd pref
const val sp_isLogin = "is_login" //1 == login
const val sp_user_data = "user_data"
const val sp_staff_id = "staff_id"

/*
*
* userregistration
->email
->name
->password


userlogin
->email
->password
* */