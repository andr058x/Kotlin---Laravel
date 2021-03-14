package com.raj.weathertodo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.raj.weathertodo.utils.ProgressDialog
import com.raj.weathertodo.utils.SharedPref
import com.raj.weathertodo.utils.WebService

open class MainActivity : AppCompatActivity() {

    lateinit var webService: WebService
    lateinit var sharedPRef: SharedPref
    lateinit var pd: ProgressDialog

    lateinit var storageReference : StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        webService = WebService(this)
        sharedPRef = SharedPref(this)

        pd = ProgressDialog(this)

        storageReference = FirebaseStorage.getInstance().reference

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()

    }
}