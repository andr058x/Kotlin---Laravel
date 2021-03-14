package com.raj.weathertodo.utils

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


fun Context.makeToast(str: String) {
    Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
}


fun makeLog(string: String) {
    Log.e("FF", string)
}

//show view
fun View.showView() {
    this.visibility = View.VISIBLE
}

// hide view
fun View.hideView() {
    this.visibility = View.GONE
}

//make view enable & disable
fun View.makeEnable() {
    this.isEnabled = true
}

fun View.makeDisable() {
    this.isEnabled = false
}

//function calculate commission amount
fun calculateEarning(amount: Float, cp: Int): Float {

    val comission = (cp.toFloat() / 100)  * amount
    makeLog("calculateEarning cp :: $cp")
    val rider_cut = amount - comission
    makeLog("amount = $amount, comission= $comission, rider_cut = $rider_cut")
    return rider_cut
}


//conver fload to 2 decimal places
fun convertFloat2Deci(num: Float): String {
    val newNum = ("%.2f".format(num))
    return newNum
}

//Change date format
fun showDate(dataStr : String?) : String{
    return try {

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val sdfNew = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
        val parse = sdf.parse(dataStr)
        val outString = sdfNew.format(parse)

        outString

    }catch (e : Exception){
        makeLog("Date Error ${e.message}")
        ""
    }
}

//load all device ids

//send notification to all id
fun sendNotiVolly(context: Context, Ids: String, _title: String, _msg: String) {

    val json = JSONObject()
    try {
        val userData = JSONObject()
        userData.put("title", "$_title")
        userData.put("body", "$_msg")
        json.put("data", userData)
        json.put("to", Ids)
    } catch (e: JSONException) {
        e.printStackTrace()
    }
    val url = "https://fcm.googleapis.com/fcm/send"
    val method = Request.Method.POST
    val SERVER_API_KEY =
        "AAAA8IOan-Q:APA91bFUj2CYRkixpggaggYS5MR2Hq8Mkqzf5gt0y1VzGCkMGy3gOlxkcSKhrxlLwqoMA1sS_fFaX9GPQVmqEBZQbTmqcAQRyuuPD_CimLVCbXKYAyROhql2EFT6B5L3vOtPmu0bGTYz"

    val queue = Volley.newRequestQueue(context)

    val jObj = object : JsonObjectRequest(Request.Method.POST,
        url,
        json,
        {
            makeLog("responce = $it")
        },
        {
            makeLog("sendNotiVolly: Error : ${it.message}")
        }) {
        override fun getHeaders(): MutableMap<String, String> {
            val params: MutableMap<String, String> = HashMap()
            params.put("Authorization", "key=$SERVER_API_KEY")
            params.put("Content-Type", "application/json")

            return params
        }
    }


    queue.add(jObj)

}





