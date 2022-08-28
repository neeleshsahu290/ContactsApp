package com.example.contactsapp.listners

import com.example.contactsapp.models.Contacts


interface ContactsDetailsListener {

    fun onContactsClicked(contacts: Contacts,index:Int, color:Int)


}