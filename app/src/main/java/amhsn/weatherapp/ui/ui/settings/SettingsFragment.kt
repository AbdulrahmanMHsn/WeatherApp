package amhsn.weatherapp.ui.ui.settings

import amhsn.weatherapp.MainActivity
import amhsn.weatherapp.R
import amhsn.weatherapp.databinding.FragmentSettingsBinding
import amhsn.weatherapp.ui.ui.local.ContextWrapper.Companion.setLocale
import amhsn.weatherapp.utils.PrefHelper
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment


class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            if(R.id.radioK == checkedId){
                PrefHelper.setUnitTemp("default", requireContext())
            } else if (R.id.radioC == checkedId) {
                PrefHelper.setUnitTemp("metric", requireContext())
            } else if (R.id.radioF == checkedId) {
                PrefHelper.setUnitTemp("imperial", requireContext())
            }
        }

        binding.radioGroupLang.setOnCheckedChangeListener { group, checkedId ->
            if (R.id.radioAr == checkedId) {
                PrefHelper.setLocalLanguage("ar", requireContext())
                activity?.let { it1 -> setLocale(it1,"ar") }
                startActivity(Intent(requireContext(),MainActivity::class.java))
            } else {
                PrefHelper.setLocalLanguage("en", requireContext())
                activity?.let { it1 -> setLocale(it1,"en") }
                startActivity(Intent(requireContext(),MainActivity::class.java))
            }
        }

    }



}