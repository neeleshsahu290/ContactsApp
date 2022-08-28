package com.example.contactsapp.activity

import android.R
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.example.contactsapp.database.ContactsDatabase
import com.example.contactsapp.database.DBContacts
import com.example.contactsapp.databinding.ActivityCreateNewBinding
import com.example.contactsapp.databinding.ActivityMainBinding
import com.example.contactsapp.utils.ClickToSelectEditText
import com.example.contactsapp.utils.Constraints
import com.example.contactsapp.utils.NetworkConnection
import com.example.contactsapp.utils.PreferenceManger
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class CreateNewActivity : AppCompatActivity() , CoroutineScope by MainScope(),
    AdapterView.OnItemSelectedListener {

    var courses = arrayOf<String?>("phone", "online")

   // lateinit var settings: Settings
    var firstName:String?=""
    var middleName:String?=""
    var surName:String?=""
    var nickName:String?=""
    var company:String?=""
    var department:String?=""
    var title:String?=""
    var phone:String?=""
    var phoneLabel:String?=""
    var address:String?=""
    var addressLabel:String?=""
    var email:String?=""
    var website:String?=""
    var db= FirebaseFirestore.getInstance()
    var imageURI: Uri? =null
    var hasImage=false
    lateinit var resultLauncher: ActivityResultLauncher<Intent>
    var saveOnline:Boolean=false

    lateinit var binding: ActivityCreateNewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityCreateNewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setValues()
        setListners()
        setSpinner2()
        setSpinnerValues()
        getCollapsableBar()
    }


    private  fun getCollapsableBar(){
        val collapsingToolbar = binding.collapsingToolbar
        binding.appbarlayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (collapsingToolbar.height + verticalOffset < collapsingToolbar.scrimVisibleHeightTrigger) {

                binding.baraddnewcon.text="Add New Contact"
                binding.txtaddnewcon.text=""
            } else {
                binding.txtaddnewcon.text="Add New Contact"
                binding.baraddnewcon.text=""

            }
        })
    }

    private fun setListners(){

        binding.saveBtn.setOnClickListener {
            firstName = binding.edttxtfirstname.editText?.text.toString()
            middleName= binding.edttxtmiddlename.editText?.text.toString()
            surName=binding.edttxtsurname.editText?.text.toString()
            nickName=binding.edttxtnickname.editText?.text.toString()
            company= binding.edttxtcompany.editText?.text.toString()
            department= binding.edttxtdepartment.editText?.text.toString()
            title=binding.edttxttitle.editText?.text.toString()
            phone= binding.edttxtphone.editText?.text.toString()
            address= binding.edttxtaddress.editText?.text.toString()
            email= binding.edttxtemail.editText?.text.toString()
            website=binding.edttxtwebsite.editText?.text.toString()


            if (isValid()){
                isLoading(true)

                if (saveOnline){
                    saveOnFirebase()
                }else{
                    saveOnDatabase()


                }

            }else{
                Toast.makeText(this,"Check Form Fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.profilePhotoadd.setOnClickListener{
            val intent = Intent(this, SelectImageActivity::class.java)
            resultLauncher.launch(intent)
        }

        binding.toolbar.setNavigationOnClickListener{
            finish()
        }
    }

    private fun saveOnDatabase(){
        val db = ContactsDatabase.getInstance(this)

        launch(Dispatchers.IO) {
            val contact = DBContacts(firstname = firstName, addresslabel = addressLabel, department = department, middlename = middleName,
            surname = surName, address = address, phone = phone, profile_img_url ="", phonelabel = phoneLabel, company = company, email = email, has_profile_image = hasImage,
            image_path = "", nickname = nickName, title = title, userid ="12200", website = "dms" )
            db.contactsDao().insert(contact)
            Handler(Looper.getMainLooper()).post {
                //code that runs in main
                      isLoading(false)
                finish()
            }




        }

    }

    private fun saveOnFirebase(){
        if (NetworkConnection().isNetworkAvailable(this)){
            //  val userid= PreferenceManger(this).getString(Constraints.KEY_UUID)
            val userid="jfodf"
            val data: HashMap<String, Any?> = hashMapOf(

                Constraints.KEY_USER_ID to userid,
                Constraints.KEY_FIRST_NAME to firstName,
                Constraints.KEY_MIDDLE_NAME to middleName,
                Constraints.KEY_SURNAME to surName,
                Constraints.KEY_NICKNAME to nickName,
                Constraints.KEY_COMPANY to company,
                Constraints.KEY_DEPARTMENT to department,
                Constraints.KEY_TITLE to title,
                Constraints.KEY_PHONE to phone,
                Constraints.KEY_PHONE_LABEL to phoneLabel,
                Constraints.KEY_ADDRESS to address,
                Constraints.KEY_ADDRESS_LABEL to addressLabel,
                Constraints.KEY_EMAIL to email,
                Constraints.KEY_WEBSITE to website
            )

            Log.d("neelesh","neelesh")

            if (hasImage){
                imageURI?.let { it1 ->uploadImage(it1,this,data) }
            }else{
                data[Constraints.KEY_HAS_PROFILE_IMAGE]= false

                saveData(data)
            }


        }else{
            NetworkConnection().errorSnackBar(binding.root)
            isLoading(false)
        }

    }



    private fun saveData(data: HashMap<String, Any?>){
        val  uuid= PreferenceManger(this).getString(Constraints.KEY_UUID)
        uuid?.let {
            val U=it
            try {
                db.collection(Constraints.KEY_COLLECTION_CONTACTS).document(it)
                    .collection(Constraints.KEY_COLLECTION_SUB_CONTACR).add(data)
                    .addOnSuccessListener {
                        db.collection(Constraints.KEY_COLLECTION_CONTACTS).document(U).collection(Constraints.KEY_COLLECTION_SUB_CONTACR).document(it.id)
                            .update(Constraints.KEY_CONTACTS_UUID, it.id)
                            .addOnSuccessListener {
                                isLoading(false)
                                finish()
                                Toast.makeText(this, "successfully added", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    }
                    .addOnFailureListener {
                        isLoading(false)
                        Log.d("error", it.toString())
                        Toast.makeText(this, "Failed to add", Toast.LENGTH_SHORT).show()
                    }
            } catch (e: Exception) {
                Log.e("exception", e.toString())
            }
        }

    }


    private fun setSpinnerValues(){


        val listPhone:ArrayList<String> = ArrayList()
        val listAddress:ArrayList<String> = ArrayList()

        for (i in 1..5){
            listPhone.add("phone$i")
            listAddress.add("address$i")
        }




        setSpinner(listPhone,binding.txtDolPhone)
        setSpinner(listAddress,binding.txtDolAddress)
      //  setSpinner(listOccupation,binding.edttxtinputoccuption)


    }
    private fun setSpinner(list: ArrayList<String>, textview: ClickToSelectEditText<Any?>){
        val adapter = ArrayAdapter(this@CreateNewActivity, R.layout.simple_spinner_dropdown_item, list)
        textview.setAdapter(adapter)
        textview.setOnItemSelectedListener(object :
            ClickToSelectEditText.OnItemSelectedListener<Any?> {
            override fun onItemSelectedListener(item: Any?, selectedIndex: Int) {
                textview.setText(item.toString())
                if (item.toString()=="clear"){
                    textview.setText("")

                }
            }
        })
    }

    private fun isLoading(loading: Boolean) {
        if (loading) {
            binding.loading.loadinglyt.visibility = View.VISIBLE

        } else {
            binding.loading.loadinglyt.visibility = View.GONE

        }

    }

    private fun isValid(): Boolean {
        binding.edttxtfirstname.isErrorEnabled = false

        var valid = true

        if (TextUtils.isEmpty(firstName)) {
            binding.edttxtfirstname.apply {
                error = "Please enter a First Name"
                isErrorEnabled = true

            }
            valid = false
        }


        return valid


    }

    private fun setValues(){
        binding.llsaveto.isVisible=false

       val loggedin= PreferenceManger(this).getBoolean(Constraints.KEY_IS_LOGGED_IN)
        if (loggedin){
            binding.llsaveto.isVisible=true
        }

        resultLauncher= registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                imageURI = Uri.parse(data?.getStringExtra("result"))
                binding.profieimg.setImageURI(imageURI)
                hasImage=true
            }
        }
    }

    private fun uploadImage(file: Uri, context: Context, data: HashMap<String, Any?>) {
        try {


            val storage = FirebaseStorage.getInstance()

            val storageRef = storage.reference

            val metadata = storageMetadata {
                contentType = "image/jpeg"
            }

            val uploadTask =
                storageRef.child("images/${file.lastPathSegment}").putFile(file, metadata)
            uploadTask.addOnProgressListener { bytesTransferred ->
                val progress = (bytesTransferred)
                val msg =
                    "Upload is ${(progress.bytesTransferred / progress.totalByteCount) * 100}% done"
                //  Toast.makeText(context, msg.toString(), Toast.LENGTH_SHORT).show()

                Log.d(ContentValues.TAG, msg)
            }.addOnPausedListener {
                Log.d(ContentValues.TAG, "Upload is paused")
            }.addOnFailureListener {
                Toast.makeText(context, "failure" + it.toString(), Toast.LENGTH_SHORT).show()
            }.addOnSuccessListener {
                Log.d("storagepath", it.storage.path)
                data[Constraints.KEY_PROFILEIMAGE_PATH] = it.storage.path

                // Log.d("imageurl", it.storage.downloadUrl.result.toString())
                Firebase.storage.reference.child(it.storage.path).downloadUrl.addOnSuccessListener { url ->
                    data[Constraints.KEY_HAS_PROFILE_IMAGE] = true
                    data[Constraints.KEY_PROFILE_IMG_URL] = url
                    saveData(data)

                }.addOnFailureListener {
                    Log.e("error", it.toString())
                }

                //Toast.makeText(context, it.storage.toString(), Toast.LENGTH_SHORT).show()

            }
        }catch (e:Exception){
            Log.e("erroroccured",e.toString())
        }

    }

    private fun setSpinner2(){
        binding.coursesspinner.onItemSelectedListener = this

        val ad: ArrayAdapter<*> = ArrayAdapter<Any?>(
            this,
            android.R.layout.simple_spinner_item,
            courses)

        ad.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item)
        binding.coursesspinner.adapter = ad
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        saveOnline = courses[p2]=="online"
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

}