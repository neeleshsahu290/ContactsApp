package com.example.contactsapp.database

import androidx.room.*
import com.example.contactsapp.utils.Constraints
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

@Entity
data class DBContacts(
    @PrimaryKey (autoGenerate = true)val id: Long=0,
    @ColumnInfo(name = Constraints.KEY_ADDRESS)val address:String?,
    @ColumnInfo(name = Constraints.KEY_ADDRESS_LABEL)val addresslabel:String?,
    @ColumnInfo(name = Constraints.KEY_COMPANY)val company:String?,
    @ColumnInfo(name = Constraints.KEY_DEPARTMENT)val department:String?,
    @ColumnInfo(name = Constraints.KEY_EMAIL)val email:String?,
    @ColumnInfo(name = Constraints.KEY_FIRST_NAME)val firstname:String?,
    @ColumnInfo(name = Constraints.KEY_HAS_PROFILE_IMAGE)val has_profile_image:Boolean?,
    @ColumnInfo(name = Constraints.KEY_PROFILEIMAGE_PATH)val image_path:String?,
    @ColumnInfo(name = Constraints.KEY_MIDDLE_NAME)val middlename:String?,
    @ColumnInfo(name = Constraints.KEY_NICKNAME)val nickname:String?,
    @ColumnInfo(name = Constraints.KEY_PHONE)val phone:String?,
    @ColumnInfo(name = Constraints.KEY_PHONE_LABEL)val phonelabel:String?,
    @ColumnInfo(name = Constraints.KEY_PROFILE_IMG_URL)val profile_img_url:String?,
    @ColumnInfo(name = Constraints.KEY_SURNAME)val surname:String?,
    @ColumnInfo(name = Constraints.KEY_TITLE)val title:String?,
    @ColumnInfo(name = Constraints.KEY_USER_ID)val userid:String?,
    @ColumnInfo(name = Constraints.KEY_WEBSITE)val website:String?,
)




@Dao
interface ContactsDao {
    @Insert
    fun insertAll(vararg contacts: DBContacts)

    @Insert
    fun insert (contacts: DBContacts)

    @Delete
    fun delete(contacts: DBContacts)

    @Query("SELECT * FROM DBContacts")
    fun getAll(): List<DBContacts>


/*    @Query("SELECT * FROM user where first_name Like :search ")
    fun getselescteduser(search: String):List<User>


    @Query("SELECT first_name FROM User where last_name LIKE :search ")
    fun getfirstnames(search: String): List<String>

    // select a single entity from table
    @Query("SELECT first_name FROM User where id > :idd ")
    fun getfirstnam(idd:Int): List<String>

    *//**
     * contains data with primary key
     *//*
    @Query("SELECT count(*)!=0 FROM User WHERE id = :uid ")
    fun containsPrimaryKey(uid: Int): Boolean


    // delete data from table
    @Query("DELETE FROM User")
    fun nukeTable()*/


}

/** to convert list to json  */
object Converters {
    @TypeConverter
    fun fromString(value: String?): ArrayList<valuesofdb> {
        val listType: Type = object : TypeToken<ArrayList<valuesofdb?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: java.util.ArrayList<valuesofdb>): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}

data class valuesofdb (var firstvalue:String, val secondvalue:String)