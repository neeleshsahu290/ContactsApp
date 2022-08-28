package com.example.contactsapp.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storageMetadata



class  UtiltyView {
/*     fun setProfileViewPopup(imageView: ImageView, context: Context){
        val popupMenu = PopupMenu(context,imageView)
        popupMenu.menuInflater.inflate(R.menu.profile_view_menu,popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.viewproile -> {
                    val intent = Intent(context, ProfileActivity ::class.java)
                    //      intent.putExtra(Constraints.KEY_FAMILY_INTENT, members)
                    context.startActivity(intent) }
                R.id.logout ->{
                    popupLogout(context)
                }
            }
            true
        }
        popupMenu.show()

    }
    fun popupLogout(context: Context){
        val builder = AlertDialog.Builder(context)
        builder.setMessage(R.string.logout_message)
            .setPositiveButton(R.string.logout
            ) { dialog, id ->
                PreferenceManger(context).clear()
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
                (context as Activity).finish()
            }
            .setNegativeButton(R.string.cancel
            ) { dialog, id ->
                dialog.dismiss()
            }
        // Create the AlertDialog object and return it
        builder.create()
            .show()
    }*/



}