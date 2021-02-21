package amhsn.weatherapp.network.response

import amhsn.weatherapp.pojo.Alerts
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "table_weather")
data class ResponseAPIWeather(
    @PrimaryKey
    @ColumnInfo(name = "weather_id")
    var id: Int,
    @SerializedName("lat") var lat: Double,
    @SerializedName("lon") var lon: Double,
    @SerializedName("timezone") var timezone: String,
    @SerializedName("timezone_offset") var timezone_offset: Int,
    @SerializedName("current") var current: Current,
    @SerializedName("hourly") var hourly: List<Hourly>,
    @SerializedName("daily") var daily: List<Daily>,
    @SerializedName("alerts") var alerts: List<Alerts>?
)