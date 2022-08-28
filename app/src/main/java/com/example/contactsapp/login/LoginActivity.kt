package com.example.contactsapp.login

import android.content.ContentValues
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.contactsapp.R
import com.example.contactsapp.databinding.ActivityLoginBinding
import com.example.contactsapp.utils.Constraints
import com.example.contactsapp.utils.NetworkConnection
import com.example.contactsapp.utils.PreferenceManger
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    lateinit var email:String
    lateinit var password:String
    lateinit var preferenceManager: PreferenceManger
    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setValues()
        setListners()
    }

    private fun setValues(){
        preferenceManager= PreferenceManger(this)

    }
    private fun setListners(){
        binding.Loginbtn.setOnClickListener {

            email = binding.edttxtemail.editText?.text.toString()
            password = binding.edttxtpassword.editText?.text.toString()
            //    isSending = true
            isLoading(true)
            if (isValid()) {
                logIn()
            } else {
                isLoading(false)
            }


        }
        binding.txtcreatenewacc.setOnClickListener {
            startActivity(Intent(this,CreateNewAccountActivity::class.java))
            finish()
        }
    }

    private fun logIn(){
        if (NetworkConnection().isNetworkAvailable(this)){
            FirebaseFirestore.getInstance().collection(Constraints.KEY_COLLECTION_CONTACTS)
                .whereEqualTo(Constraints.KEY_EMAIL,email)
                .whereEqualTo(Constraints.KEY_PASSWORD,password)
                .get()
                .addOnSuccessListener {query->
                    if (query.documents.size==0){
                        Toast.makeText(this,"Email and Password Doesnot Mstch".toString(), Toast.LENGTH_SHORT).show()

                        isLoading(false)
                    }else{
                        for (doc in query.documents)
                        {
                            preferenceManager.putString(Constraints.KEY_UUID, doc.id)
                            preferenceManager.putBoolean(Constraints.KEY_IS_LOGGED_IN, true)
                            doc.getString(Constraints.KEY_NAME)
                                ?.let { preferenceManager.putString(Constraints.KEY_NAME, it) }
                        }

                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this,it.toString(), Toast.LENGTH_SHORT).show()
                    isLoading(false)
                }
        } else{
            isLoading(false)
            //    isSending=false
            NetworkConnection().errorSnackBar(binding.root)
        }
    }
    private fun isLoading(loading: Boolean) {
        if (loading) {
            binding.loading.loadinglyt.visibility = View.VISIBLE

        } else {
            binding.loading.loadinglyt.visibility = View.GONE

        }

    }




    private fun isValid(): Boolean {
        binding.edttxtemail.isErrorEnabled = false
        binding.edttxtpassword.isErrorEnabled=false
        var valid = true
  /*      if (TextUtils.isEmpty(email)) {
            binding.edttxtemail.apply {
                error = "Please enter a Mobile Number"
                isErrorEnabled = true

            }
            valid = false
        }*/
        if (TextUtils.isEmpty(email)) {
            binding.edttxtemail.apply {
                error = "Please enter a Email"
                isErrorEnabled = true

            }
            valid = false
        }else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edttxtemail.apply {
                error = "Please enter a Valid Email"
                isErrorEnabled = true

            }
            valid = false
        }



        if (TextUtils.isEmpty(password)) {
            binding.edttxtpassword.apply {
                error = "Please enter a Password"
                isErrorEnabled = true

            }
            valid = false
        }

        return valid


    }
}