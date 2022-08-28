package com.example.contactsapp.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.example.contactsapp.R


class Utility {

    val colorArray: Array<Int> = arrayOf(R.drawable.colorgradientblue,R.drawable.colorgradientgreen,
        R.drawable.colorgradientorange,R.drawable.colorgradientpurple,
        R.drawable.colorgradientyellow
    )

  /*  fun loadingDialog(context: Context): AlertDialog.Builder{
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setIcon(R.drawable.ic_back_btn_light)

        builder.create()
        return builder
    }*/


   internal fun hideKeyboard(activity: Activity) {
        // Check if no view has focus:
        val view = activity.currentFocus
        if (view != null) {
            val inputManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    fun setImage(context: Context,path:String,imageView: ImageView){
        Firebase.storage.reference.child(path).downloadUrl.addOnSuccessListener { url->
            Log.d("imageurl",url.toString())
            Glide
                .with(context)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.person)
                .into(imageView)
        }.addOnFailureListener {
            // Handle any errors
        }
    }

    fun setImageUrl(context: Context,url:String,imageView: ImageView){

            Glide
                .with(context)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.person)
                .into(imageView)

    }



}