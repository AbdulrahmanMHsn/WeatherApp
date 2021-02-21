package amhsn.weatherapp.database

import amhsn.weatherapp.network.response.ResponseAPIWeather
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WeatherDao {
    @Query("SELECT * FROM table_weather")
    fun getAllWeather(): LiveData<ResponseAPIWeather>

    @Query("SELECT * FROM table_weather")
    fun getWeather(): ResponseAPIWeather

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWeather(weather: ResponseAPIWeather)

    @Update
    fun updateWeather(weather: ResponseAPIWeather)

    @Query("DELETE FROM table_weather")
    fun deleteAllWeather()
}