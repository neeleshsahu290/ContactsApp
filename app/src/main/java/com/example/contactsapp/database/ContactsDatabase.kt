package com.example.contactsapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.*
import androidx.room.RoomDatabase


import com.example.contactsapp.database.subscribeOnBackground

    @Database(entities = [DBContacts::class ], version = 1)
    abstract class ContactsDatabase : RoomDatabase() {

        abstract fun contactsDao(): ContactsDao

        companion object {
            private var instance: ContactsDatabase? = null

            @Synchronized
            fun getInstance(ctx: Context): ContactsDatabase {
                if(instance == null)
                    instance = Room.databaseBuilder(ctx.applicationContext, ContactsDatabase::class.java,
                        "user_database")
                        .fallbackToDestructiveMigration()
                        .build()

                return instance!!

            }


        }
    }
