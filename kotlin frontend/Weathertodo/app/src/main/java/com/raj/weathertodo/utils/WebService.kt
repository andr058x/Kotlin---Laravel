package com.raj.weathertodo.utils

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley


/* class for web Service Call */

class WebService(val context: Context) {
    private var queue: RequestQueue = Volley.newRequestQueue(context)


    val POST = Request.Method.POST
    val GET = Request.Method.GET

    fun makeApiCall(
        url: String,
        method: Int,
        map: HashMap<String, String>,
        onResponse: ApiListner
    ) {

        val stringRequest = object : StringRequest(method, url,
            { response ->

                // Display the first 500 characters of the response string.
                onResponse.onResponse(response)
            },
            {
                makeLog("API Error : ${it.message}")

            }) {
            override fun getParams(): MutableMap<String, String> {
                makeLog("getParams")
                for (i in map.keys) {
                    makeLog("param: $i : ${map[i]}")
                }
                return map
            }
        }


        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }
}