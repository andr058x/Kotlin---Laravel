package com.raj.weathertodo.login

import android.os.Bundle
import com.example.awesomedialog.AwesomeDialog
import com.example.awesomedialog.body
import com.example.awesomedialog.onPositive
import com.example.awesomedialog.title
import com.raj.weathertodo.MainActivity
import com.raj.weathertodo.databinding.ActivityRegisterBinding
import com.raj.weathertodo.utils.ApiListner
import com.raj.weathertodo.utils.makeLog
import com.raj.weathertodo.utils.url_register

class RegisterActivity : MainActivity() {
    lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.btnRegister.setOnClickListener {
            val userName = binding.etUserName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (userName.isEmpty()) {
                binding.etUserName.setError("Enter User Name")
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                binding.etEmail.setError("Enter Email Id")
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.etPassword.setError("Enter Password ")
                return@setOnClickListener
            }

            makeRegister(userName, email, password)
        }

        //go back
        binding.goBack.setOnClickListener {
            onBackPressed()
        }
    }


    fun makeRegister(userName: String, email: String, password: String) {

        val map = hashMapOf<String, String>(
            "email" to email,
            "name" to userName,
            "password" to password

        )

        pd.show()
        webService.makeApiCall(
            url_register,
            webService.POST,
            map,
            object : ApiListner {
                override fun onResponse(data: String) {
                    pd.dismiss()
                    makeLog("register data : $data")
                    AwesomeDialog.build(this@RegisterActivity)
                        .title("Register SuccessFull!")
                        .body("You are Successfully Registered. Login to start Organising Your Task.")
                        .onPositive("Ok") {
                            onBackPressed()
                        }

                }

                override fun onFailure() {

                }

            }
        )
    }
}