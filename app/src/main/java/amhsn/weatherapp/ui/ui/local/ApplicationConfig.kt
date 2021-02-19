package amhsn.weatherapp.ui.ui.local

import amhsn.weatherapp.utils.LocaleManager
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import java.util.*

class ApplicationConfig : Application() {
    //
//
    override fun onCreate() {
        super.onCreate()


//        val dataSaver = PreferenceManager.getDefaultSharedPreferences(applicationContext)
//
//        var lang = dataSaver?.getString(LANG, "en")
//
//
//
//        Log.i("asdsadsadsa", "" + lang)
//        when (lang) {
//            "ar" -> changeLocale("ar", applicationContext)
//
//
//            "en" -> changeLocale("en", applicationContext)
//
//
//        }
    }

    val TAG = "App"

    // for the sake of simplicity. use DI in real apps instead
//    lateinit var localeManager: LocaleManager
//
//    override fun attachBaseContext(base: Context) {
//        localeManager = LocaleManager(base)
//        super.attachBaseContext(localeManager.setLocale(base))
//        Log.d(TAG, "attachBaseContext")
//    }
//
//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//        localeManager.setLocale(this)
//        Log.d(TAG, "onConfigurationChanged: " + newConfig.locale.language)
//    }

    //
    fun changeLocale(s: String, context: Context) {


        var locale = Locale(s)
        Locale.setDefault(locale)
        var config = Configuration()
        config.locale = locale
        context.resources.updateConfiguration(
            config,
            context.resources.displayMetrics
        )
    }

    companion object {
        fun instance() = ApplicationConfig()

    }
}