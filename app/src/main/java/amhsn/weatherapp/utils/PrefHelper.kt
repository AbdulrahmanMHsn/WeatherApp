package amhsn.weatherapp.utils

import android.content.Context
import android.content.SharedPreferences

object PrefHelper {

    private lateinit var mSharedPreferences: SharedPreferences
    private const val pref_file = "settings"


    fun setUnitTemp(unit: String, mContext: Context): Boolean {
        mSharedPreferences =
            mContext.applicationContext.getSharedPreferences(pref_file, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putString("unit", unit)
        return editor.commit()
    }


    fun getUnitTemp(mContext: Context): String? {
        mSharedPreferences =
            mContext.applicationContext.getSharedPreferences(pref_file, Context.MODE_PRIVATE)
        return mSharedPreferences.getString("unit", "metric")
    }


    fun setLastUpdate(update: Long, mContext: Context): Boolean {
        mSharedPreferences =
            mContext.applicationContext.getSharedPreferences(pref_file, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putLong("update", update)
        return editor.commit()
    }

    fun getLastUpdate(mContext: Context): Long? {
        mSharedPreferences =
            mContext.applicationContext.getSharedPreferences(pref_file, Context.MODE_PRIVATE)
        return mSharedPreferences.getLong("update", 0)
    }


    fun setUnitWind(unit: String, mContext: Context): Boolean {
        mSharedPreferences =
            mContext.applicationContext.getSharedPreferences(pref_file, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putString("unitWind", unit)
        return editor.commit()
    }

    fun getUnitWind(mContext: Context): String? {
        mSharedPreferences =
            mContext.applicationContext.getSharedPreferences(pref_file, Context.MODE_PRIVATE)
        return mSharedPreferences.getString("unitWind", "metric")
    }

    fun setLatLng(lat: Double, lon: Double, mContext: Context): Boolean {
        mSharedPreferences =
            mContext.applicationContext.getSharedPreferences(pref_file, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putFloat("lat", lat.toFloat())
        editor.putFloat("long", lon.toFloat())
        return editor.commit()
    }

    fun getLatitude(mContext: Context): Double? {
        mSharedPreferences =
            mContext.applicationContext.getSharedPreferences(pref_file, Context.MODE_PRIVATE)
        return mSharedPreferences.getFloat("lat", 0f).toDouble()
    }

    fun getLongitude(mContext: Context): Double? {
        mSharedPreferences =
            mContext.applicationContext.getSharedPreferences(pref_file, Context.MODE_PRIVATE)
        return mSharedPreferences.getFloat("long", 0f).toDouble()
    }


    fun setLocalLanguage(language: String, mContext: Context): Boolean {
        mSharedPreferences =
            mContext.applicationContext.getSharedPreferences(pref_file, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putString("language", language)
        return editor.commit()
    }

    fun getLocalLanguage(mContext: Context): String? {
        mSharedPreferences =
            mContext.applicationContext.getSharedPreferences(pref_file, Context.MODE_PRIVATE)
        return mSharedPreferences.getString("language", "en")
    }

    fun setAddress(address: String, mContext: Context): Boolean {
        mSharedPreferences =
            mContext.applicationContext.getSharedPreferences(pref_file, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putString("address", address)
        return editor.commit()
    }

    fun getAddress(mContext: Context): String? {
        mSharedPreferences =
            mContext.applicationContext.getSharedPreferences(pref_file, Context.MODE_PRIVATE)
        return mSharedPreferences.getString("address", "")
    }


    fun setEnableCurrentLocation(enable: Boolean, mContext: Context): Boolean {
        mSharedPreferences =
            mContext.applicationContext.getSharedPreferences(pref_file, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putBoolean("enable", enable)
        return editor.commit()
    }

    fun getEnableCurrentLocation(mContext: Context): Boolean? {
        mSharedPreferences =
            mContext.applicationContext.getSharedPreferences(pref_file, Context.MODE_PRIVATE)
        return mSharedPreferences.getBoolean("enable", false)
    }


    fun setEnableShowDialogAlert(enable: Boolean, mContext: Context): Boolean {
        mSharedPreferences =
            mContext.applicationContext.getSharedPreferences(pref_file, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putBoolean("alert", enable)
        return editor.commit()
    }

    fun getEnableShowDialogAlert(mContext: Context): Boolean? {
        mSharedPreferences =
            mContext.applicationContext.getSharedPreferences(pref_file, Context.MODE_PRIVATE)
        return mSharedPreferences.getBoolean("alert", false)
    }


    fun setIsFirst(enable: Boolean, mContext: Context): Boolean {
        mSharedPreferences =
            mContext.applicationContext.getSharedPreferences(pref_file, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putBoolean("first", enable)
        return editor.commit()
    }

    fun getIsFirst(mContext: Context): Boolean? {
        mSharedPreferences =
            mContext.applicationContext.getSharedPreferences(pref_file, Context.MODE_PRIVATE)
        return mSharedPreferences.getBoolean("first", false)
    }


    fun setEnableNotification(enable: Boolean, mContext: Context): Boolean {
        mSharedPreferences =
            mContext.applicationContext.getSharedPreferences(pref_file, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putBoolean("notify", enable)
        return editor.commit()
    }

    fun getEnableNotification(mContext: Context): Boolean? {
        mSharedPreferences =
            mContext.applicationContext.getSharedPreferences(pref_file, Context.MODE_PRIVATE)
        return mSharedPreferences.getBoolean("notify", true)
    }

//    fun setIsFindLocalWeather(enable: Boolean, mContext: Context): Boolean {
//        mSharedPreferences =
//            mContext.applicationContext.getSharedPreferences(pref_file, Context.MODE_PRIVATE)
//        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
//        editor.putBoolean("localWeather", enable)
//        return editor.commit()
//    }
//
//    fun getIsFindLocalWeather(mContext: Context): Boolean? {
//        mSharedPreferences =
//            mContext.applicationContext.getSharedPreferences(pref_file, Context.MODE_PRIVATE)
//        return mSharedPreferences.getBoolean("localWeather", false)
//    }

}