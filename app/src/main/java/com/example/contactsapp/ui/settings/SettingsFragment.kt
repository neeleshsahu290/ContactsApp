package com.example.contactsapp.ui.settings

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.contactsapp.R
import com.example.contactsapp.databinding.FragmentSettingsBinding
import com.example.contactsapp.login.LoginActivity
import com.example.contactsapp.utils.Constraints
import com.example.contactsapp.utils.PreferenceManger

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    lateinit var preferenceManger: PreferenceManger
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        preferenceManger= PreferenceManger(requireContext())

        /*val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/
        setListners()
        return root
    }

    private fun setListners(){



        binding.btn.setOnClickListener {
            val loggedin= preferenceManger.getBoolean(Constraints.KEY_IS_LOGGED_IN)
            if (loggedin){
                    val builder = AlertDialog.Builder(context)
                    builder.setMessage(getString(R.string.logoutmessage))
                        .setPositiveButton(getString(R.string.logout)
                        ) { dialog, id ->
                            preferenceManger.clear()
                            binding.btn.text="Login"
                        }
                        .setNegativeButton(getString(R.string.cancel)
                        ) { dialog, id ->
                            dialog.dismiss()
                        }
                    // Create the AlertDialog object and return it
                    builder.create()
                        .show()



            }else{
                startActivity(Intent(requireContext(),LoginActivity::class.java))
            }

        }
    }

    override fun onResume() {
        super.onResume()
        var loggedin= preferenceManger.getBoolean(Constraints.KEY_IS_LOGGED_IN)

        if (loggedin){

            binding.btn.text="Logout"
        }else{
            binding.btn.text="Login"

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}