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

    fun getLongitude (mContext: Context): Double? {
        mSharedPreferences =
            mContext.applicationContext.getSharedPreferences(pref_file, Context.MODE_PRIVATE)
        return mSharedPreferences.getFloat("long", 0f).toDouble()
    }


}