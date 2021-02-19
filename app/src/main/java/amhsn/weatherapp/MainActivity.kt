package amhsn.weatherapp

import amhsn.weatherapp.databinding.ActivityMainBinding
import amhsn.weatherapp.ui.ui.local.ContextWrapper
import amhsn.weatherapp.ui.ui.local.ContextWrapper.Companion.changeLang
import amhsn.weatherapp.ui.ui.local.ContextWrapper.Companion.setLocale
import amhsn.weatherapp.utils.MainApplication
import amhsn.weatherapp.utils.MyContextWrapper
import amhsn.weatherapp.utils.PrefHelper
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import java.util.*


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainActivity : AppCompatActivity() {


    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun attachBaseContext(newBase: Context) {
        val context: Context = changeLang(newBase, Locale(PrefHelper.getLocalLanguage(newBase)))
        super.attachBaseContext(context)
    }

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        setLocale(this, PrefHelper.getLocalLanguage(this))
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setupNavigation()
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    // Setting Up One Time Navigation
    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        navController = navHostFragment.navController

        setSupportActionBar(binding.toolbarMain.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        binding.navViewNotification.setupWithNavController(navController)
        binding.navViewNotification.setNavigationItemSelectedListener {
            it.setChecked(true)

            when (it.getItemId()) {
                R.id.nav_favorite -> navController.navigate(R.id.favouriteFragment)
                R.id.nav_settings -> navController.navigate(R.id.settingsFragment)
                R.id.nav_location->navController.navigate(R.id.alarmsFragment)
            }
            binding.drawerLayout.closeDrawers()

            true
        }

        if (PrefHelper.getLocalLanguage(this).equals("en")) {
            binding.drawerLayout.setViewScale(GravityCompat.START, 0.9f)
            binding.drawerLayout.setViewElevation(GravityCompat.START, 1f)
            binding.drawerLayout.useCustomBehavior(GravityCompat.START)
        } else {
            binding.drawerLayout.setViewScale(GravityCompat.END, 0.9f)
            binding.drawerLayout.setViewElevation(GravityCompat.END, 1f)
            binding.drawerLayout.useCustomBehavior(GravityCompat.END)
        }

        appBarConfiguration = AppBarConfiguration(setOf(R.id.home), binding.drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }


    override fun onBackPressed() {
        if (binding.drawerLayout.isOpen) {
            binding.drawerLayout.close()
        } else {
            super.onBackPressed()
        }
    }

    fun setLocale(activity: Activity, lang: String?) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        activity.resources.updateConfiguration(config, null)
    }

    fun EnglishToArabic(str: String): String {
        var result = ""
        var ar = '۰'
        for (ch in str) {
            ar = ch
            when (ch) {
                '0' -> ar = '۰'
                '1' -> ar = '۱'
                '2' -> ar = '۲'
                '3' -> ar = '۳'
                '4' -> ar = '۴'
                '5' -> ar = '۵'
                '6' -> ar = '۶'
                '7' -> ar = '۷'
                '8' -> ar = '۸'
                '9' -> ar = '۹'
            }
            result = "${result}$ar"
        }
        return result
    }


}