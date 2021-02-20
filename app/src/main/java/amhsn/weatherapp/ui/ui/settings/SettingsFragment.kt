package amhsn.weatherapp.ui.ui.settings

import amhsn.weatherapp.MainActivity
import amhsn.weatherapp.R
import amhsn.weatherapp.databinding.FragmentSettingsBinding
import amhsn.weatherapp.ui.ui.local.ContextWrapper.Companion.setLocale
import amhsn.weatherapp.utils.Dialogs
import amhsn.weatherapp.utils.LocationHelper
import amhsn.weatherapp.utils.NetworkConnection
import amhsn.weatherapp.utils.PrefHelper
import amhsn.weatherapp.viewmodel.LocationViewModel
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.work.WorkManager


class SettingsFragment : Fragment() {

    private lateinit var mProgress: Dialog
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var binding: FragmentSettingsBinding
    private var isConnected: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocationHelper.init(requireContext())
        locationViewModel = ViewModelProviders.of(this).get(LocationViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        mProgress = Dialogs.createProgressBarDialog(context, "")
        // definition NetworkConnection
        val networkConnection = NetworkConnection(requireContext())
        networkConnection.observe(viewLifecycleOwner, Observer {
            isConnected = it
        })

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (PrefHelper.getEnableCurrentLocation(requireContext())!!.equals(true)) {
            binding.settingsSwitchLocation.isChecked = true
        } else {
            binding.settingsSwitchLocation.isChecked = false
        }

        if (PrefHelper.getEnableShowDialogAlert(requireContext())!!.equals(true)) {
            binding.settingsSwitchAlart.isChecked = true
        } else {
            binding.settingsSwitchAlart.isChecked = false
        }


        binding.settingsSwitchAlart.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                PrefHelper.setEnableShowDialogAlert(true, requireContext())
                binding.settingsSwitchAlart.isChecked = true
            } else {
                PrefHelper.setEnableShowDialogAlert(false, requireContext())
                @Suppress("DEPRECATION")
                WorkManager.getInstance().cancelAllWorkByTag("AlarmWorkerMANAGER_PeriodicWorkRequest")
                binding.settingsSwitchAlart.isChecked = false
            }
        }

        binding.layoutGetCustomLocation.setOnClickListener {
            if (isConnected) {
                if (PrefHelper.getEnableCurrentLocation(requireContext())!!.equals(true)) {
                    PrefHelper.setEnableCurrentLocation(false, requireContext())
                    binding.settingsSwitchLocation.isChecked = false
                }

                val bundle = Bundle()
                bundle.putString("settings", "location")
                Navigation.findNavController(it)
                    .navigate(R.id.action_settingsFragment_to_mapFragment, bundle)
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.internet),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

        }

        binding.settingsSwitchLocation.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isConnected) {
                if (isChecked) {

                    if (LocationHelper.checkGPS()) {
                        mProgress.show()
                        locationViewModel.getLocationData()
                            .observe(viewLifecycleOwner, Observer {
                                mProgress.cancel()
                                PrefHelper.setLatLng(it.latitude, it.longitude, requireContext())
                            })
                    } else {
                        LocationHelper.openGPS()
                    }
                    PrefHelper.setEnableCurrentLocation(true, requireContext())
                } else {
                    PrefHelper.setEnableCurrentLocation(false, requireContext())
                }

            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.internet),
                    Toast.LENGTH_SHORT
                )
                    .show()

                if (PrefHelper.getEnableCurrentLocation(requireContext())!!.equals(true)) {
                    binding.settingsSwitchLocation.isChecked = true
                } else {
                    binding.settingsSwitchLocation.isChecked = false
                }
            }
        }

        if (PrefHelper.getUnitTemp(requireContext()).equals("default")) {
            binding.radioGroupUint.check(R.id.radioK)
        } else if (PrefHelper.getUnitTemp(requireContext()).equals("metric")) {
            binding.radioGroupUint.check(R.id.radioC)
        } else if (PrefHelper.getUnitTemp(requireContext()).equals("imperial")) {
            binding.radioGroupUint.check(R.id.radioF)
        }


        if (PrefHelper.getLocalLanguage(requireContext()).equals("en")) {
            binding.radioGroupLang.check(R.id.radioEn)
        } else if (PrefHelper.getLocalLanguage(requireContext()).equals("ar")) {
            binding.radioGroupLang.check(R.id.radioAr)
        }


        binding.radioGroupUint.setOnCheckedChangeListener { group, checkedId ->
            if (isConnected) {
                if (R.id.radioK == checkedId) {
                    PrefHelper.setUnitTemp("default", requireContext())
                } else if (R.id.radioC == checkedId) {
                    PrefHelper.setUnitTemp("metric", requireContext())
                } else if (R.id.radioF == checkedId) {
                    PrefHelper.setUnitTemp("imperial", requireContext())
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.internet), Toast.LENGTH_SHORT)
                    .show()
                if (PrefHelper.getUnitTemp(requireContext()).equals("default")) {
                    binding.radioGroupUint.check(R.id.radioK)
                } else if (PrefHelper.getUnitTemp(requireContext()).equals("metric")) {
                    binding.radioGroupUint.check(R.id.radioC)
                } else if (PrefHelper.getUnitTemp(requireContext()).equals("imperial")) {
                    binding.radioGroupUint.check(R.id.radioF)
                }
            }
        }

        binding.radioGroupLang.setOnCheckedChangeListener { group, checkedId ->
            if (isConnected) {
                if (R.id.radioAr == checkedId) {
                    PrefHelper.setLocalLanguage("ar", requireContext())
                    activity?.let { it1 -> setLocale(it1, "ar") }
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                } else {
                    PrefHelper.setLocalLanguage("en", requireContext())
                    activity?.let { it1 -> setLocale(it1, "en") }
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.internet), Toast.LENGTH_SHORT)
                    .show()
                if (PrefHelper.getLocalLanguage(requireContext()).equals("en")) {
                    binding.radioGroupLang.check(R.id.radioEn)
                } else if (PrefHelper.getLocalLanguage(requireContext()).equals("ar")) {
                    binding.radioGroupLang.check(R.id.radioAr)
                }
            }
        }

    }


}