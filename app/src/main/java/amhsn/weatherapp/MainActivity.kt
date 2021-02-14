package amhsn.weatherapp

import amhsn.weatherapp.databinding.ActivityMainBinding
import amhsn.weatherapp.ui.home.Home
import amhsn.weatherapp.ui.ui.favourite.FavouriteFragment
import amhsn.weatherapp.ui.ui.location.LocationFragment
import amhsn.weatherapp.ui.ui.settings.SettingsFragment
import amhsn.weatherapp.utils.LocaleHelper
import amhsn.weatherapp.utils.PrefHelper
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isFirst: Boolean = false


    override fun attachBaseContext(newBase: Context?) {
//        newBase?.let { LocaleHelper.onAttach(it, "en") }

        super.attachBaseContext(newBase?.let { PrefHelper.getLocalLanguage(newBase)?.let { it1 ->
            LocaleHelper.onAttach(it,
                it1
            )
        } })
    }

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


            LocaleHelper.setLocale(this,PrefHelper.getLocalLanguage(this))


//         var localLanguage: String = LocaleHelper.getLanguage(this).toString()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

//        Toast.makeText(this,localLanguage,Toast.LENGTH_SHORT).show()

        binding.apply {
            setSupportActionBar(toolbarMain.toolbar)
            val toggle = ActionBarDrawerToggle(
                this@MainActivity,
                drawerLayout,
                toolbarMain.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
            )
            toggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_UNLOCKED)
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()


            drawerLayout.setViewScale(GravityCompat.START, 0.9f)
            drawerLayout.setViewElevation(GravityCompat.START, 1f)
            drawerLayout.useCustomBehavior(GravityCompat.START)

//            navViewNotification.setNavigationItemSelectedListener(this@MainActivity)

            navViewNotification.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.nav_home -> {
                        val homeFragment = Home()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment, homeFragment).commit()
                        supportActionBar!!.title = "WeatherApp"
                    }
                    R.id.nav_location -> {
                        val homeFragment = LocationFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment, homeFragment).commit()
                        supportActionBar!!.title = getString(R.string.location)
                    }
                    R.id.nav_settings -> {
                        val homeFragment = SettingsFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment, homeFragment).commit()
                        supportActionBar!!.title = getString(R.string.settings)
                    }

                    R.id.nav_favorite -> {
                        val homeFragment = FavouriteFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment, homeFragment).commit()
                        supportActionBar!!.hide()
                    }
                }
                binding.drawerLayout.close()
                true
            }

        }

    }


//    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.nav_home -> Toast.makeText(baseContext, "" + item.itemId, Toast.LENGTH_SHORT)
//                .show()
//            R.id.nav_location -> {
//                val homeFragment = LocationFragment()
//                supportFragmentManager.beginTransaction()
//                    .replace(R.id.fragment, homeFragment).commit()
//            }
//        }
//        binding.drawerLayout.closeDrawer(GravityCompat.START)
//        return true
//    }


//    override fun onRestart() {
//        super.onRestart()
//        isFirst = true
//    }
//
//
//    override fun onStart() {
//        super.onStart()
//        if (!isFirst)
//            binding.navViewNotification.getMenu().getItem(0).setChecked(true)
//    }


    override fun onBackPressed() {
        if (binding.drawerLayout.isOpen)
            binding.drawerLayout.close()
        else
            super.onBackPressed()
    }
}