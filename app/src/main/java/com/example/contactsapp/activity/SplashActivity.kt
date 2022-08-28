package com.example.contactsapp.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.contactsapp.MainActivity
import com.example.contactsapp.R
import com.example.contactsapp.databinding.ActivitySelectImageBinding
import com.example.contactsapp.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashBinding
    lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isReadPermissionGranted = false
    private var isWritePermissionGranted = false
    private var isCallPermissionGranted=false
    private var isReadContactsPermissionGranted=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }
    private fun init() {




        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permissions ->

            isReadPermissionGranted = permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: isReadPermissionGranted
            isWritePermissionGranted = permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: isWritePermissionGranted
            isCallPermissionGranted = permissions[android.Manifest.permission.CALL_PHONE] ?: isCallPermissionGranted
            isReadContactsPermissionGranted= permissions[android.Manifest.permission.READ_CONTACTS] ?: isReadContactsPermissionGranted

            checkPermission()



        }

        requestPermission(this)


    }

    private fun checkPermission(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE ) == PackageManager.PERMISSION_GRANTED
        ) {
            Handler().postDelayed(
                {

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                }, 300
            )
        } else {

            requestPermission(this)
            Toast.makeText(this,"Allow Permission to access the App",Toast.LENGTH_LONG).show()
        }
    }

   private fun requestPermission(context: Context){


        val isReadPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val isWritePermission = ContextCompat.checkSelfPermission(context,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

       val isCallPermission = ContextCompat.checkSelfPermission(context,
           android.Manifest.permission.CALL_PHONE
       ) == PackageManager.PERMISSION_GRANTED

       val isReadContactsPermission = ContextCompat.checkSelfPermission(context,
           android.Manifest.permission.READ_CONTACTS
       ) == PackageManager.PERMISSION_GRANTED

        val minSdkLevel = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        isReadPermissionGranted = isReadPermission
        isWritePermissionGranted = isWritePermission || minSdkLevel
       isCallPermissionGranted= isCallPermission
       isReadContactsPermissionGranted= isReadContactsPermission

        Log.d("permission","readpermission: "+ isReadPermissionGranted+"writepermission: "+isWritePermissionGranted)

        val permissionRequest = mutableListOf<String>()
        if (!isWritePermissionGranted){

            permissionRequest.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        }
        if (!isReadPermissionGranted){

            permissionRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)

        }
       if (!isCallPermissionGranted){

           permissionRequest.add(android.Manifest.permission.CALL_PHONE)

       }
       if (!isReadContactsPermissionGranted){

           permissionRequest.add(android.Manifest.permission.READ_CONTACTS)

       }

        if (permissionRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }else{
            Handler().postDelayed(
                {

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                }, 1000
            )
        }

    }
}