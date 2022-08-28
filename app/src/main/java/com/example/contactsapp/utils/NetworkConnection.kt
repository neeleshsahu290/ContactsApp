package com.example.contactsapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.res.TypedArrayUtils.getText
import com.google.android.material.snackbar.Snackbar
import com.example.contactsapp.R

 class NetworkConnection {
  @SuppressLint("NewApi")
  internal fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
         //   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        return true
                    }
           //     }
            }
        }
        return false
    }

   internal fun errorSnackBar(view: View){
        val snack = Snackbar.make(view, R.string.internet_error, Snackbar.LENGTH_LONG)
        snack.setBackgroundTint(Color.RED)

        snack.show()
    }

}