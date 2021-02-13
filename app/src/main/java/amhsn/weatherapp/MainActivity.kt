package amhsn.weatherapp

import amhsn.weatherapp.databinding.ActivityMainBinding
import amhsn.weatherapp.ui.home.Home
import amhsn.weatherapp.ui.ui.location.LocationFragment
import amhsn.weatherapp.ui.ui.settings.SettingsFragment
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isFirst: Boolean = false


    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


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
                        supportActionBar!!.title = "Location"
                    }
                    R.id.nav_settings -> {
                        val homeFragment = SettingsFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment, homeFragment).commit()
                        supportActionBar!!.title = "Settings"
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


    override fun onRestart() {
        super.onRestart()
        isFirst = true
    }


    override fun onStart() {
        super.onStart()
        if (!isFirst)
            binding.navViewNotification.getMenu().getItem(0).setChecked(true)
    }


    override fun onBackPressed() {
        if (binding.drawerLayout.isOpen)
            binding.drawerLayout.close()
        else
            super.onBackPressed()
    }
}