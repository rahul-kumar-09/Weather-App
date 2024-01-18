package com.programmingz.wheatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import com.programmingz.wheatherapp.data.WeatherApp
import com.programmingz.wheatherapp.databinding.ActivityMainBinding
import com.programmingz.wheatherapp.util.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //6e9f08f08e2d8c72e76a80ecddc2b878  <- API key


        getCurrentWeather("noida")
        searchCityData()
    }

    private fun searchCityData() {
        val searchBar = binding.searchView
        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null){
                    getCurrentWeather(query)
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }

        })
    }

    private fun getCurrentWeather(cityName: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        val response = retrofit.getWeatherData(cityName, "6e9f08f08e2d8c72e76a80ecddc2b878", "metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && response.body() != null){
                    val temperature = responseBody?.main?.temp.toString()
                    val maxTamp = responseBody?.main?.temp_max.toString()
                    val minTamp = responseBody?.main?.temp_min.toString()
                    val condition = responseBody?.weather?.firstOrNull()?.main?: "unknown"
                   // Log.d("TAG", "onResponse: $temperature")
                    binding.temp.text = "$temperature °C"
                    binding.minTamp.text = "Min  Temp $minTamp"
                    binding.maxTemp.text = "Max Temp $maxTamp"
                    binding.humidity.text = "$condition"
                    binding.location.text = " $cityName"
                    binding.day.text = dayData(System.currentTimeMillis())
                    binding.date.text = date()
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {

            }
        })

    }

    private fun date(): String? {
        val day = SimpleDateFormat("dd mm yy",Locale.getDefault())
        return day.format((Date()))
    }

    fun dayData(timeStap: Long): String{

        val day = SimpleDateFormat("EEEE",Locale.getDefault())
        return day.format((Date()))

    }

}