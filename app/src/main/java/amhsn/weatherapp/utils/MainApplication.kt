package amhsn.weatherapp.utils

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log

@Suppress("DEPRECATION")
class MainApplication: Application() {


//    override fun attachBaseContext(base: Context) {
//        super.attachBaseContext(LocaleHelper.onAttach(baseContext,"en"))
//    }

    val TAG = "App"

    // for the sake of simplicity. use DI in real apps instead
    lateinit var localeManager: LocaleManager

    override fun attachBaseContext(base: Context) {
        localeManager = LocaleManager(base)
        super.attachBaseContext(localeManager.setLocale(base))
        Log.d(TAG, "attachBaseContext")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        localeManager.setLocale(this)
        Log.d(TAG, "onConfigurationChanged: " + newConfig.locale.language)
    }

}