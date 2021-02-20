package amhsn.weatherapp.viewmodel

import amhsn.weatherapp.database.AlarmDao
import amhsn.weatherapp.database.AppDatabase
import amhsn.weatherapp.pojo.CustomAlarm
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AlarmViewModel(application: Application) : AndroidViewModel(application) {

    var alarmDao: AlarmDao = AppDatabase.getDatabase(application).getAlarmDaoInstance()

    fun getAlarmLocalDataSource(): LiveData<List<CustomAlarm>> {
        return alarmDao.getAllAlarms()
    }

    fun insertAlarm(alarm: CustomAlarm) {
        GlobalScope.launch {
            Dispatchers.IO
            alarmDao.insertAlarm(alarm)
        }
    }


    fun deleteAlarm(alarm: CustomAlarm) {
        GlobalScope.launch {
            Dispatchers.IO
            alarmDao.deleteAlarm(alarm)
        }
    }

    fun updateAlarm(alarm: CustomAlarm) {
        GlobalScope.launch {
            Dispatchers.IO
            alarmDao.updateAlarm(alarm)
        }
    }
}