package amhsn.weatherapp.pojo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarm_table")
class CustomAlarm {
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: Long = 0

    @ColumnInfo(name = "idAlarm")
    var idAlarm: String = ""

    @ColumnInfo(name = "timestamp")
    var timestamp: Long = 0

    @ColumnInfo(name = "turn")
    var isTurn: Boolean = true


    override fun toString(): String {
        return "CustomAlarm(id=$id, idAlarm='$idAlarm', timestamp=$timestamp, isTurn=$isTurn)"
    }
}