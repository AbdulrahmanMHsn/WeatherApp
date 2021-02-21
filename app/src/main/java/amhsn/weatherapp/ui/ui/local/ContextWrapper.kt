package amhsn.weatherapp.ui.ui.local

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import java.util.*

@Suppress("DEPRECATION")
class ContextWrapper(base: Context?) : android.content.ContextWrapper(base) {
    companion object {
        fun changeLang(
            context: Context,
            newLocale: Locale?
        ): ContextWrapper {
            var context = context
            val res = context.resources
            val configuration = res.configuration
            if (Build.VERSION.SDK_INT >= 24) {
                configuration.setLocale(newLocale)
                val localeList = LocaleList(newLocale)
                LocaleList.setDefault(localeList)
                configuration.setLocales(localeList)
                context = context.createConfigurationContext(configuration)
            } else if (Build.VERSION.SDK_INT >= 17) {
                configuration.setLocale(newLocale)
                context = context.createConfigurationContext(configuration)
            } else {
                configuration.locale = newLocale
                res.updateConfiguration(configuration, res.displayMetrics)
            }
            return ContextWrapper(context)
        }

        fun setLocale(activity: Activity, lang: String?) {
            val locale = Locale(lang)
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
            activity.resources.updateConfiguration(config, null)
        }
    }
}