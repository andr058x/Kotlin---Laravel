package com.raj.weathertodo.login

import android.content.Intent
import android.os.Bundle
import com.example.awesomedialog.AwesomeDialog
import com.example.awesomedialog.body
import com.example.awesomedialog.onPositive
import com.example.awesomedialog.title
import com.raj.weathertodo.MainActivity
import com.raj.weathertodo.databinding.ActivityLoginBinding
import com.raj.weathertodo.home.HomeActivity
import com.raj.weathertodo.utils.*
import org.json.JSONObject

class LoginActivity : MainActivity() {

    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        if (sharedPRef.getData(sp_isLogin) == "1") {
            startActivity(
                Intent(
                    this@LoginActivity,
                    HomeActivity::class.java
                )
            )
            finishAffinity()
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty()) {
                binding.etEmail.setError("Enter Email Id")
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.etPassword.setError("Enter Password ")
                return@setOnClickListener
            }

            makeLogin(email, password)
        }

        binding.btnRegister.setOnClickListener {
            val i = Intent(this, RegisterActivity::class.java)
            startActivity(i)

        }
    }

    private fun makeLogin(email: String, password: String) {
        val map = hashMapOf<String, String>(
            "email" to email,
            "password" to password

        )

        pd.show()
        webService.makeApiCall(
            url_login,
            webService.POST,
            map,
            object : ApiListner {
                override fun onResponse(data: String) {
                    pd.dismiss()
                    makeLog("login data : $data")

                    val obj = JSONObject(data)
                    val success = obj.getInt("success")
                    val message = obj.getString("message")
                    val data = obj.getString("data")

                    when (success) {
                        1 -> {//success
                            AwesomeDialog.build(this@LoginActivity)
                                .title("Login SuccessFull!")
                                .body("Start Organising Your Tasks. :)")
                                .onPositive("Ok") {
                                    //save data to pref
                                    sharedPRef.saveData(sp_user_data, data)
                                    sharedPRef.saveData(sp_isLogin, "1")
                                    startActivity(
                                        Intent(
                                            this@LoginActivity,
                                            HomeActivity::class.java
                                        )
                                    )
                                    finishAffinity()
                                }
                        }
                        else -> {
                            AwesomeDialog.build(this@LoginActivity)
                                .title("Login Failed!")
                                .body(message)
                                .onPositive("Ok") {

                                }
                        }
                    }


                }

                override fun onFailure() {

                }

            }
        )
    }
}