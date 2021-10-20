package com.example.bedashingapp.helper

import android.content.Context
import android.content.SharedPreferences

class SessionManager(_context: Context) {

    private var sharedPreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private var context: Context? = null

    //Shared Preference mode
    private val PRIVATE_MODE: Int = 0

    //Shared Preferences filename
    private val SHARED_PREFS = "BedashingAppPrefs"



    //Shared Preferences variables
    companion object{
        private const val WAREHOUSE_ID = "wareHouseID"
        private const val WAREHOUSE_NAME = "wareHouseName"
        private const val PREV_WAREHOUSE_ID = "prevWareHouseID"
        private const val STORAGE = "storage"
        private const val PREV_STORAGE = "prevStorage"
        private const val IS_LOGGED_IN = "isLoggedIn"
        private const val PREV_USERNAME = "previousUserName"
        private const val PREV_PASSWORD = "previousPassword"
        private const val CURR_USERNAME = "currentUserName"
        private const val CURR_PASSWORD = "currentPassword"
        private const val IS_SYNCED = "isSynced"
        private const val LAST_UPDATED = "lastUpdated"
        private const val EMP_ID = "employeeID"
        private const val CURR_LANGUAGE = "currentLanguage"
        private const val BASE_URL = "baseURL"
    }

    init{
        context = _context
        sharedPreferences = context?.getSharedPreferences(SHARED_PREFS, PRIVATE_MODE)
        editor = sharedPreferences?.edit()
    }

    //setters
    fun putWareHouseID(wareHouseID: String){
        editor?.putString(WAREHOUSE_ID, wareHouseID)
        editor?.commit()
    }
    fun putWareHouseName(wareHouseName: String){
        editor?.putString(WAREHOUSE_NAME, wareHouseName)
        editor?.commit()
    }

    fun putPreviousWareHouseID(wareHouseID: String){
        editor?.putString(PREV_WAREHOUSE_ID, wareHouseID)
        editor?.commit()
    }

    fun putIsSynced(flag: Boolean){
        editor?.putBoolean(IS_SYNCED, flag)
        editor?.commit()
    }

    fun putIsLoggedIn(flag: Boolean){
        editor?.putBoolean(IS_LOGGED_IN, flag)
        editor?.commit()
    }

    fun putEmployeeID(id: String){
        editor?.putString(EMP_ID, id)
        editor?.commit()
    }

    fun putStorageLocation(storageLocation: String){
        editor?.putString(STORAGE, storageLocation)
        editor?.commit()
    }

    fun putPreviousStorageLocation(storageLocation: String){
        editor?.putString(PREV_STORAGE, storageLocation)
        editor?.commit()
    }

    fun putCurrentUserName(userName: String){
        editor?.putString(CURR_USERNAME, userName)
        editor?.commit()
    }

    fun putCurrentPassword(pass: String){
        editor?.putString(CURR_PASSWORD, pass)
        editor?.commit()
    }

    fun putPreviousUserName(userName: String){
        editor?.putString(PREV_USERNAME, userName)
        editor?.commit()
    }

    fun putPreviousPassword(pass: String){
        editor?.putString(PREV_PASSWORD, pass)
        editor?.commit()
    }

    fun putLastUpdated(date: String){
        editor?.putString(LAST_UPDATED, date)
        editor?.commit()
    }

    fun putCurrentLanguage(languageCode: String){
        editor?.putString(CURR_LANGUAGE, languageCode)
        editor?.commit()
    }

    fun putBaseURL(baseURL: String){
        editor?.putString(BASE_URL, baseURL)
        editor?.commit()
    }






    //getters
    fun getWareHouseID(): String{
        return sharedPreferences?.getString(WAREHOUSE_ID, "")!!
    }

    fun getWareHouseName(): String{
        return sharedPreferences?.getString(WAREHOUSE_NAME, "")!!
    }

    fun getPreviousWareHouseID(): String{
        return sharedPreferences?.getString(PREV_WAREHOUSE_ID, "")!!
    }

    fun isLoggedIn() : Boolean{
        return sharedPreferences?.getBoolean(IS_LOGGED_IN, false)!!
    }

    fun isSynced(): Boolean{
        return sharedPreferences?.getBoolean(IS_SYNCED, false)!!
    }

    fun getCurrentUserName(): String{
        return sharedPreferences?.getString(CURR_USERNAME, "")!!
    }

    fun getCurrentPassword(): String{
        return sharedPreferences?.getString(CURR_PASSWORD, "")!!
    }

    fun getEmployeeID(): String{
        return sharedPreferences?.getString(EMP_ID, "")!!
    }

    fun getLastUpdated(): String{
        return sharedPreferences?.getString(LAST_UPDATED, "XX/XX/XXXX")!!
    }

    fun isPreviousUser(): Boolean{
        val currUser = sharedPreferences?.getString(CURR_USERNAME, "")
        val currPass = sharedPreferences?.getString(CURR_PASSWORD, "")
        val prevUser = sharedPreferences?.getString(PREV_USERNAME, "")
        val prevPass = sharedPreferences?.getString(PREV_PASSWORD, "")

        if(currUser!!.isEmpty() || prevUser!!.isEmpty() || currPass!!.isEmpty() || prevPass!!.isEmpty()){
            return false
        }
        else{
            if(currUser == prevUser && currPass == prevPass){
                return true
            }
            return false
        }
    }

    fun isPreviousWarehouse(): Boolean{
        val currWareHouseID = sharedPreferences?.getString(WAREHOUSE_ID, "")
        val prevWareHouseID = sharedPreferences?.getString(PREV_WAREHOUSE_ID, "")

        return currWareHouseID == prevWareHouseID
    }

    fun isPreviousStorageLocation() : Boolean{
        val currStorageLocation = sharedPreferences?.getString(STORAGE, "S")
        val prevStorageLocation = sharedPreferences?.getString(PREV_STORAGE, "")

        return currStorageLocation == prevStorageLocation
    }

    fun getStorageLocation(): String{
        return sharedPreferences?.getString(STORAGE, "S")!!
    }

    fun getPreviousStorageLocation(): String{
        return sharedPreferences?.getString(PREV_STORAGE, "")!!
    }

    fun getCurrentLanguage(): String{
        return sharedPreferences?.getString(CURR_LANGUAGE, "")!!
    }

    fun getBaseURL(): String{
        return sharedPreferences?.getString(BASE_URL, "37.224.100.109:802")!!
    }



}