package com.example.climapresente

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    // weather url to get JSON
    var weather_url1 = ""

    // api id for url

    var api_key = "130727cd01971e3affad196da6b7d4bc"


    private lateinit var botonInicio : Button
    private lateinit var cuadroTemperatura: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        botonInicio = findViewById(R.id.inicio)
        cuadroTemperatura = findViewById(R.id.temperatura)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        botonInicio.setOnClickListener {
            buscarPermisos()
        }
    }

    private fun buscarPermisos() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            obtenerUbicacion()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                obtenerUbicacion()
            } else {
                Toast.makeText(this, "Permisos denegados por el dispositivo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun obtenerUbicacion() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    weather_url1 = "https://api.openweathermap.org/data/2.5/weather?lat=${location.latitude}&lon=${location.longitude}&units=metric&appid=${api_key}"
                }
                obtenerTemperatura()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Permisos no concedidos por el celular.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun obtenerTemperatura() {
        val queue = Volley.newRequestQueue(this)
        val url: String = weather_url1

        val stringReq = StringRequest(Request.Method.GET, url, { response ->
            val obj = JSONObject(response)
            val main: JSONObject = obj.getJSONObject("main")
            val temperatura = main.getString("temp")
            val ciudad = obj.getString("name")

            cuadroTemperatura.text = "${ciudad} está a ${temperatura}º"
            System.out.println(obj.toString())
        },
            { cuadroTemperatura.text = "No se pudo conectar." })
        queue.add(stringReq)
    }
}
