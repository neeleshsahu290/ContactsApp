package com.example.contactsapp.login

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.contactsapp.R
import com.example.contactsapp.databinding.ActivityCreateNewAccountBinding
import com.example.contactsapp.databinding.ActivityCreateNewBinding
import com.example.contactsapp.utils.Constraints
import com.example.contactsapp.utils.NetworkConnection
import com.example.contactsapp.utils.PreferenceManger
import com.google.firebase.firestore.FirebaseFirestore

class CreateNewAccountActivity : AppCompatActivity() {
   lateinit var email:String
    lateinit var name:String
    lateinit var password:String
    lateinit var rePassword:String
    lateinit var preferenceManager: PreferenceManger


    lateinit var binding: ActivityCreateNewAccountBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateNewAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setValues()
        setListners()

    }

    private fun setValues(){
        preferenceManager= PreferenceManger(this)

    }
    private fun setListners(){
        binding.Createbtn.setOnClickListener {

                name = binding.edttxtname.editText?.text.toString()
                email = binding.edttxtemail.editText?.text.toString()
                password = binding.edttxtpassword.editText?.text.toString()
                rePassword = binding.edttxtcheckpassword.editText?.text.toString()
                //    isSending = true
                isLoading(true)
                if (isValid()) {
                    checkUserAlreadyRegistered()
                } else {
                    isLoading(false)
                }


        }
    }

    private fun checkUserAlreadyRegistered(){
        if (NetworkConnection().isNetworkAvailable(this)){
            FirebaseFirestore.getInstance().collection(Constraints.KEY_COLLECTION_CONTACTS)
                .whereEqualTo(Constraints.KEY_EMAIL,email)
                .get()
                .addOnSuccessListener {query->
                    if (query.documents.size==0){
                        signIn()
                    }else{
                        Toast.makeText(this,"Email Already Exist".toString(), Toast.LENGTH_SHORT).show()

                        isLoading(false)
                        //    isSending=false
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
    private fun signIn(){
        val data = hashMapOf(
            Constraints.KEY_NAME to name,
            Constraints.KEY_EMAIL to email,
            Constraints.KEY_PASSWORD to password,

        )


        if (NetworkConnection().isNetworkAvailable(this)) {
            FirebaseFirestore.getInstance().collection(Constraints.KEY_COLLECTION_CONTACTS)
                .add(data).addOnSuccessListener {
                    finshActivity(it.id)

                }
                .addOnFailureListener {
                    isLoading(false)
                    Log.e(ContentValues.TAG, it.toString())
                }
        }else{
            NetworkConnection().errorSnackBar(binding.root)
        }
    }

    private fun finshActivity(uuid: String) {
        preferenceManager.putString(Constraints.KEY_UUID, uuid)
        preferenceManager.putBoolean(Constraints.KEY_IS_LOGGED_IN, true)
        preferenceManager.putString(Constraints.KEY_NAME,name)
        finish()
    }

    private fun isValid(): Boolean {
        binding.edttxtemail.isErrorEnabled = false
        binding.edttxtpassword.isErrorEnabled=false
        binding.edttxtcheckpassword.isErrorEnabled=false
        binding.edttxtname.isErrorEnabled=false
        var valid = true
        if (TextUtils.isEmpty(email)) {
            binding.edttxtemail.apply {
                error = "Please enter a  Email"
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

        if (TextUtils.isEmpty(name)) {
            binding.edttxtname.apply {
                error = "Please enter a Name"
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
        if (TextUtils.isEmpty(rePassword)) {
            binding.edttxtcheckpassword.apply {
                error = "Please Re enter a Password"
                isErrorEnabled = true

            }
            valid = false
        }
        return valid


    }
}