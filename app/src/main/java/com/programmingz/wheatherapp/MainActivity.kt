package com.programmingz.wheatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.core.content.ContextCompat
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
                    val humidity = responseBody?.main?.humidity
                    val windSpeed = responseBody?.wind?.speed
                    val sunRise = responseBody?.sys?.sunrise
                    val sunSet = responseBody?.sys?.sunset
                    val seaLevel = responseBody?.main?.pressure
                    val condition = responseBody?.weather?.firstOrNull()?.main?: "unknown"
                    val maxTemp = responseBody?.main?.temp_max
                    val minTamp = responseBody?.main?.temp_min

                    binding.temp.text = "$temperature °C"
                    binding.weather.text = condition
                    binding.max.text = "Max Temp $maxTemp °C"
                    binding.min.text = "Min Temp $minTamp °C"
                    binding.humidity.text = "$humidity %"
                    binding.wind.text = "$windSpeed m/s"
                    binding.sunrise.text = "$sunRise"
                    binding.sunset.text = "$sunSet"
                    binding.sea.text = "$seaLevel hPa"
                    binding.condition.text = condition
                    binding.cityName.text = cityName
                    binding.day.text = dayData(System.currentTimeMillis())
                    binding.date.text = date()

                    weatherCondition(condition)

                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                Toast.makeText(applicationContext, "Some error occur", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun weatherCondition(condition: String) {
        when(condition){
            "SMOKE", "CLOUDS", "SUNNY" -> {
                binding.weatherBackground.setBackgroundResource(R.drawable.cloudimg)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
        }

    }

    private fun date(): String? {
        val day = SimpleDateFormat("DD MMM YYYY",Locale.getDefault())
        return day.format((Date()))
    }

    fun dayData(timeStap: Long): String{

        val day = SimpleDateFormat("EEEE",Locale.getDefault())
        return day.format((Date()))

    }

}