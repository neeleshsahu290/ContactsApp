package com.example.contactsapp.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contactsapp.database.ContactsDatabase
import com.example.contactsapp.database.DBContacts
import com.example.contactsapp.models.Contacts
import com.example.contactsapp.utils.Constraints
import com.example.contactsapp.utils.ContactData
import com.example.contactsapp.utils.PreferenceManger
import com.example.contactsapp.utils.retrieveAllContacts
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.serialization.json.*


class HomeViewModel : ViewModel() {

    val db = FirebaseFirestore.getInstance()
    val arrayList:ArrayList<Contacts> = ArrayList()
    lateinit var  preferenceManger: PreferenceManger


    private val _familyMembers = MutableLiveData<ArrayList<Contacts>>()
    val familyMembers: LiveData<ArrayList<Contacts>> get() = _familyMembers
    private val _hasUser = MutableLiveData<Boolean>()
    val hasUser: LiveData<Boolean> get() = _hasUser


   @SuppressLint("MissingPermission")
    fun familyModels(context: Context){
       preferenceManger= PreferenceManger(context)
       viewModelScope.launch() {
           withContext(Dispatchers.IO) {
             //  notwork(context)

               async {
                   val mobConList: List<ContactData> =context.retrieveAllContacts()
                 //  var contactList:ArrayList<Contacts> = ArrayList()
                   for (i in mobConList.indices){
                       var phone:String?=""
                       if (mobConList[i].phoneNumber.isNotEmpty()){
                           phone= mobConList[i].phoneNumber[0]
                       }
                       val con= Contacts(firstname = mobConList[i].name, profile_img_url = mobConList[i].avatar?.path, phone =phone.toString())
                       arrayList.add(con)




                   }
               }
       async {
        val isloggedin=   preferenceManger.getBoolean(Constraints.KEY_IS_LOGGED_IN)
           if (isloggedin){
               val uuid= preferenceManger.getString(Constraints.KEY_UUID)
               uuid?.let {
                   try {


               db.collection(Constraints.KEY_COLLECTION_CONTACTS).document(uuid).collection(Constraints.KEY_COLLECTION_SUB_CONTACR)
                   .get()
                   .addOnSuccessListener { querysnapshot->
                       if (querysnapshot!=null && querysnapshot.documents.size>0){
                           for ( doc in querysnapshot.documents){
                               val data = doc.toObject(Contacts::class.java) as Contacts
                               Log.d("documents", doc.toString())
                               arrayList.add(data)
                           }



                       }
                   }
                   }catch (e:Exception){

                   }
           }
       }

       }
               async {
                   val db = ContactsDatabase.getInstance(context)
                 val list=   db.contactsDao().getAll()

                   for (i in list.indices){
                       val contact = Contacts(firstname = list[i].firstname, addresslabel = list[i].addresslabel, department = list[i].department, middlename = list[i].middlename,
                           surname = list[i].surname, address = list[i].address, phone = list[i].phone, profile_img_url =list[i].profile_img_url, phonelabel = list[i].phonelabel, company = list[i].company, email = list[i].email, has_profile_image = list[i].has_profile_image,
                           image_path = list[i].image_path, nickname = list[i].nickname, title = list[i].title, userid =list[i].userid, website = list[i].website )
                       arrayList.add(contact)
                   }

               }

        }.invokeOnCompletion {
               arrayList.sortBy { it.firstname }
           Log.d("completesnee","completed")
           _familyMembers.postValue(arrayList)

           if (arrayList.size>0){
               _hasUser.postValue(true)
           }else{
               _hasUser.postValue(false)
           }

       }
    }
   }


}

