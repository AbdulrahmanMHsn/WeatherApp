package amhsn.weatherapp.network.response

import amhsn.weatherapp.pojo.Alerts
import com.google.gson.annotations.SerializedName

data class ResponseAPIWeather (

	@SerializedName("lat") var lat : Double,
	@SerializedName("lon") var lon : Double,
	@SerializedName("timezone") var timezone : String,
	@SerializedName("timezone_offset") var timezone_offset : Int,
	@SerializedName("current") var current : Current,
	@SerializedName("hourly") var hourly : List<Hourly>,
	@SerializedName("daily") var daily : List<Daily>,
	@SerializedName("alerts") var alerts : List<Alerts>
)