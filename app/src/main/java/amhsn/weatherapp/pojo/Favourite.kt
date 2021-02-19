package amhsn.weatherapp.pojo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_table")
class Favourite {
    @PrimaryKey(autoGenerate = true)
    var id: Int =0

    @ColumnInfo(name = "city")
    var city: String = ""

    @ColumnInfo(name = "country")
    var country: String  = ""

    @ColumnInfo(name = "lat")
    var lat: Double = 0.0

    @ColumnInfo(name = "lon")
    var lon: Double = 0.0


    override fun toString(): String {
        return "Favourite(id=$id, city='$city', country='$country', lat=$lat, lon=$lon)"
    }


}