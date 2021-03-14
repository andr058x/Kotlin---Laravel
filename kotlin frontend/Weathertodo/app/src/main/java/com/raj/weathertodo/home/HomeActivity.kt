package com.raj.weathertodo.home

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.awesomedialog.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.raj.weathertodo.MainActivity
import com.raj.weathertodo.R
import com.raj.weathertodo.adapters.TaskAdapter
import com.raj.weathertodo.databinding.ActivityHomeBinding
import com.raj.weathertodo.login.LoginActivity
import com.raj.weathertodo.model.TaskClass
import com.raj.weathertodo.model.UserClass
import com.raj.weathertodo.model.weather.Weather
import com.raj.weathertodo.model.weather.weatherClass
import com.raj.weathertodo.utils.*
import org.json.JSONObject


class HomeActivity : MainActivity() {
    lateinit var binding: ActivityHomeBinding

    val READ_PER_CODE = 101
    val WRITE_PER_CODE = 102
    val MY_PERMISSIONS_REQUEST_LOCATION = 103

    lateinit var locationManager: LocationManager
    lateinit var locationListener: LocationListener

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    var taskList = ArrayList<TaskClass>()

    var user: UserClass? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        //locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val userStr = sharedPRef.getData(sp_user_data).toString()

        user = Gson().fromJson(userStr, UserClass::class.java)

        if (user != null) {
            binding.tvWelcomText.text = "Welcome, ${user?.name.toString().capitalize()}"
        }

        //logout
        binding.btnLogout.setOnClickListener {

            AwesomeDialog.build(this@HomeActivity)
                .title("confirm logout? ")
                .body("Are you sure you want to? ")
                .onPositive("Logout", buttonBackgroundColor = R.drawable.button_bg) {
                    sharedPRef.removeData(sp_user_data)
                    sharedPRef.removeData(sp_isLogin)

                    startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                    finishAffinity()
                }
                .onNegative("Cancel") {}

        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getCurrentLocation()

        //add task button
        binding.fabAddTask.setOnClickListener {
            val i = Intent(this, AddTaskActivity::class.java)
            startActivity(i)
        }

        //refresh weather
        binding.ivRefresh.setOnClickListener {
            getCurrentLocation()
        }

        /* if (checkLocationPermission()) {
             getCurrentLocation()
         }*/
        getWaether()
    }

    override fun onResume() {
        super.onResume()
        getWaether()
        loadAllTask()
    }

    fun loadAllTask() {

        taskList.clear()

        webService.makeApiCall(
            url_getAllTask,
            webService.POST,
            hashMapOf<String, String>(
                "user_id" to "${user?.id}"
            ),
            object : ApiListner {
                override fun onResponse(data: String) {
                   // makeLog("$data")
                    val obj = JSONObject(data)
                    val success = obj.getInt("success")
                    val message = obj.getString("message")
                    val data = obj.getJSONArray("data")

                    if (data.length() != 0) {

                        for (i in 0 until data.length()) {

                            val task = Gson().fromJson(data.getString(i), TaskClass::class.java)
                            taskList.add(task)
                        }

                        binding.rvTask.apply {
                            layoutManager = LinearLayoutManager(
                                this@HomeActivity,
                                RecyclerView.VERTICAL,
                                false
                            )
                            adapter = TaskAdapter(taskList, this@HomeActivity)
                        }

                    } else {
                        //makeLog("No Record")
                        makeToast("To Task Found")
                    }
                }

                override fun onFailure() {

                }
            }
        )
    }


    /*  fun checkLocationPermission(): Boolean {
          return if (ContextCompat.checkSelfPermission(
                  this,
                  Manifest.permission.ACCESS_FINE_LOCATION
              )
              != PackageManager.PERMISSION_GRANTED
          ) {

              // Should we show an explanation?
              if (ActivityCompat.shouldShowRequestPermissionRationale(
                      this,
                      Manifest.permission.ACCESS_FINE_LOCATION
                  )
              ) {

                  // Show an explanation to the user *asynchronously* -- don't block
                  // this thread waiting for the user's response! After the user
                  // sees the explanation, try again to request the permission.

                  AwesomeDialog.build(this)
                      .title("Location Permission Needed")
                      .body("For Accurate Weather Grant location permission.")
                      .onPositive("OK") {
                          ActivityCompat.requestPermissions(
                              this@HomeActivity,
                              arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                              MY_PERMISSIONS_REQUEST_LOCATION
                          )
                      }
              } else {
                  // No explanation needed, we can request the permission.
                  ActivityCompat.requestPermissions(
                      this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                      MY_PERMISSIONS_REQUEST_LOCATION
                  )
              }
              false
          } else {
              turnGPSOn()
              true
          }
      }*/

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {

                        //Request location updates:
                        //locationManager.requestLocationUpdates(provider, 400, 1, this);
                       // makeLog("Permission granted")
                    }

                } else {

                   // makeLog("Permission not granted ")
                }
                return

            }

        }

    }

    private fun turnGPSOn() {
        val provider: String =
            Settings.Secure.getString(contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED)
        if (!provider.contains("gps")) { //if gps is disabled
            //makeLog("turn in GPS Please")

            val intent = Intent("android.location.GPS_ENABLED_CHANGE")
            intent.putExtra("enabled", true)
            sendBroadcast(intent)

            val poke = Intent()
            poke.setClassName(
                "com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider"
            )
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE)
            poke.data = Uri.parse("3")
            sendBroadcast(poke)
        }
    }


    fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                makeLog("location == ${location.toString()}")
                if (location != null) {
                    val url =
                        "http://api.openweathermap.org/data/2.5/weather?lat=${location.latitude}&lon=${location.longitude}&units=metric&appid=1890c332f5dd8f6cce75b7faaa746329"

                    webService.makeApiCall(url,
                        webService.GET,
                        hashMapOf(),
                        object : ApiListner {
                            override fun onResponse(data: String) {
                                makeLog("$data")
                                var weather :weatherClass? = null

                                try{
                                   weather = Gson().fromJson(data, weatherClass::class.java)
                                }catch (e: Exception){
                                    makeLog("error :${e.message}")
                                }

                                binding.tvWeatherTemp.text =
                                    "${weather!!.main.temp} ${getString(R.string.celcius)}"


                                var drawble = getDrawable(R.drawable.sun)

                                when (weather!!.weather[0].main) {

                                    "Clear" -> {
                                        drawble = getDrawable(R.drawable.sun)
                                    }

                                    "Rain" -> {
                                        drawble = getDrawable(R.drawable.rain)
                                    }

                                    "Extreme " -> {
                                        drawble = getDrawable(R.drawable.storm)
                                    }

                                    else -> {
                                        drawble = getDrawable(R.drawable.cloudy)
                                    }
                                }
                                binding.ivWeatherIcon.setImageDrawable(drawble)
                                binding.tvWeatherTemp.textSize = 24f
                            }

                            override fun onFailure() {

                            }

                        }
                    )
                } else {
                    binding.tvWeatherTemp.text = "Please Turn on Location and Click Refresh!"

                }
            }
    }

    //new
    fun getWaether(): Boolean {
        if (!checkGPSEnabled()) {
            return false
        } else {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    //Location Permission already granted
                    getCurrentLocation()
                } else {
                    //Request Location Permission
                    checkLocationPermission()
                }
            } else {
                getCurrentLocation()
            }

            return true
        }

    }

    private fun checkGPSEnabled(): Boolean {
        if (!isLocationEnabled())
            showAlert()
        return isLocationEnabled()
    }

    private fun showAlert() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Enable Location")
            .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " + "use this app")
            .setPositiveButton("Location Settings") { paramDialogInterface, paramInt ->
                val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(myIntent)
            }
            .setNegativeButton("Cancel") { paramDialogInterface, paramInt -> }
        dialog.show()
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager!!.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            MY_PERMISSIONS_REQUEST_LOCATION
                        )
                    })
                    .create()
                    .show()

            } else ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        }
    }
}