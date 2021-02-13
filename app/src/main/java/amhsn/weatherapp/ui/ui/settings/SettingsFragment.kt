package amhsn.weatherapp.ui.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import amhsn.weatherapp.R
import amhsn.weatherapp.databinding.FragmentSettingsBinding
import amhsn.weatherapp.utils.PrefHelper
import android.widget.Toast
import androidx.databinding.DataBindingUtil


class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private var intSelectButton: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
        }else if (PrefHelper.getUnitTemp(requireContext()).equals("imperial")) {
            binding.radioGroupUint.check(R.id.radioF)
        }

        binding.radioGroupUint.setOnCheckedChangeListener { group, checkedId ->
            if (R.id.radioK == checkedId) {
                PrefHelper.setUnitTemp("default", requireContext())
            } else if (R.id.radioC == checkedId) {
                PrefHelper.setUnitTemp("metric", requireContext())
            } else if (R.id.radioF == checkedId) {
                PrefHelper.setUnitTemp("imperial", requireContext())
            }
        }

    }


}