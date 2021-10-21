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
    companion object {
        private const val WAREHOUSE_ID = "wareHouseID"
        private const val WAREHOUSE_NAME = "wareHouseName"
        private const val PREV_WAREHOUSE_ID = "prevWareHouseID"
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

        // Shared preferences variables
        private const val SESSION_ID = "sessionId"
        private const val SERVER = "ServerName"
        private const val PORT = "Port"
        private const val PLATFORM = "Platform"
        private const val COMPANY = "Company"
        private const val USER_NAME = "Name"
        private const val USER_ID = "Id"
        private const val USER_CODE = "UserCode"
        private const val USER_EMAIL = "Email"
        private const val USER_PHONE = "Phone"
        private const val USER_SUPER_USER = "Superuser"
        private const val USER_DEFAULTS = "Defaults"
        private const val USER_FAX_NUMBER = "FaxeNumber"
        private const val USER_BRANCH = "Branch"
        private const val USER_DEPARTMENT = "Dapartment"
        private const val USER_LANGUAGE_CODE = "LanguageCode"
        private const val USER_LOCKED = "Locked"
        private const val USER_GROUP = "Group"
        private const val USER_HEAD_OFFICE_CARD_CODE = "CardCode"

        private const val USER_POSITION = "Position"
        private const val USER_EMPLOYEE_ID = "EmployeeId"


        private const val USER_BPLID = "BplId"

        private const val DOC_UPLOAD_IP = "incomingPayment"
        private const val SPECIAL_PRICES_FLAG = "specialPricesFlag"

        private const val USER_TIME_FORMAT = ""
        private const val USER_PRICE_LIST = ""
        private const val USER_LANGUAGE = ""
        private const val USER_BRANCH_ID = ""
        private const val USER_AUTH_GROUP = ""
        private const val USER_SYNC_TIME = ""
        private const val USER_DEFAULT_WHS = ""

        private const val USER_DFLT_REGION = "user_default_region"
        private const val USER_DFLT_STORE = "user_default_store"
        private const val SESSION_TIMEOUT = "session_timeout"
    }

    init {
        context = _context
        sharedPreferences = context?.getSharedPreferences(SHARED_PREFS, PRIVATE_MODE)
        editor = sharedPreferences?.edit()
    }

    //setters
    fun putWareHouseID(wareHouseID: String) {
        editor?.putString(WAREHOUSE_ID, wareHouseID)
        editor?.commit()
    }

    fun putWareHouseName(wareHouseName: String) {
        editor?.putString(WAREHOUSE_NAME, wareHouseName)
        editor?.commit()
    }

    fun putPreviousWareHouseID(wareHouseID: String) {
        editor?.putString(PREV_WAREHOUSE_ID, wareHouseID)
        editor?.commit()
    }

    fun putIsSynced(flag: Boolean) {
        editor?.putBoolean(IS_SYNCED, flag)
        editor?.commit()
    }

    fun putIsLoggedIn(flag: Boolean) {
        editor?.putBoolean(IS_LOGGED_IN, flag)
        editor?.commit()
    }

    fun putEmployeeID(id: String) {
        editor?.putString(EMP_ID, id)
        editor?.commit()
    }


    fun putCurrentUserName(userName: String) {
        editor?.putString(CURR_USERNAME, userName)
        editor?.commit()
    }

    fun putCurrentPassword(pass: String) {
        editor?.putString(CURR_PASSWORD, pass)
        editor?.commit()
    }

    fun putPreviousUserName(userName: String) {
        editor?.putString(PREV_USERNAME, userName)
        editor?.commit()
    }

    fun putPreviousPassword(pass: String) {
        editor?.putString(PREV_PASSWORD, pass)
        editor?.commit()
    }

    fun putLastUpdated(date: String) {
        editor?.putString(LAST_UPDATED, date)
        editor?.commit()
    }

    fun putCurrentLanguage(languageCode: String) {
        editor?.putString(CURR_LANGUAGE, languageCode)
        editor?.commit()
    }

    fun putBaseURL(baseURL: String) {
        editor?.putString(BASE_URL, baseURL)
        editor?.commit()
    }

    fun setUserDfltRegion(userDfltRegion: String?) {
        editor!!.putString(
            USER_DFLT_REGION,
            userDfltRegion
        )
        editor!!.commit()
    }

    fun setUserDfltStore(userDfltStore: String?) {
        editor!!.putString(
            USER_DFLT_STORE,
            userDfltStore
        )
        editor!!.commit()
    }

    fun setUserHeadOfficeCardCode(userHeadOfficeCardCode: String?) {
        editor!!.putString(
            USER_HEAD_OFFICE_CARD_CODE,
            userHeadOfficeCardCode
        )
        editor!!.commit()
    }

    fun setServer(server: String?) {
        editor!!.putString(SERVER, server)
        editor!!.commit()
    }

    fun setPort(port: String?) {
        editor!!.putString(PORT, port)
        editor!!.commit()
    }

    fun setPlatform(platform: String?) {
        editor!!.putString(PLATFORM, platform)
        editor!!.commit()
    }

    fun setCompany(company: String?) {
        editor!!.putString(COMPANY, company)
        editor!!.commit()
    }

    fun setSessionId(sessionId: String?) {
        editor!!.putString(
            SESSION_ID,
            sessionId
        )
        editor!!.commit()
    }


    fun setName(name: String?) {
        editor!!.putString(USER_NAME, name)
        editor!!.commit()
    }

    fun setUserId(id: String?) {
        editor!!.putString(USER_ID, id)
        editor!!.commit()
    }

    fun setUserEmail(email: String?) {
        editor!!.putString(USER_EMAIL, email)
        editor!!.commit()
    }

    fun setUserPhone(phone: String?) {
        editor!!.putString(USER_PHONE, phone)
        editor!!.commit()
    }

    fun setUserSuperUser(superUser: String?) {
        editor!!.putString(
            USER_SUPER_USER,
            superUser
        )
        editor!!.commit()
    }

    fun setUserDefaults(defaults: String?) {
        editor!!.putString(
            USER_DEFAULTS,
            defaults
        )
        editor!!.commit()
    }

    fun setUserFaxNumber(faxNumber: String?) {
        editor!!.putString(
            USER_FAX_NUMBER,
            faxNumber
        )
        editor!!.commit()
    }

    fun setUserBranch(branch: String?) {
        editor!!.putString(USER_BRANCH, branch)
        editor!!.commit()
    }

    fun setUserDepartment(department: String?) {
        editor!!.putString(
            USER_DEPARTMENT,
            department
        )
        editor!!.commit()
    }

    fun setUserCode(userCode: String?) {
        editor!!.putString(USER_CODE, userCode)
        editor!!.commit()
    }

    fun setUserLanguageCode(languageCode: String?) {
        editor!!.putString(
            USER_LANGUAGE_CODE,
            languageCode
        )
        editor!!.commit()
    }

    fun setUserLocked(locked: String?) {
        editor!!.putString(USER_LOCKED, locked)
        editor!!.commit()
    }

    fun setUserGroup(group: String?) {
        editor!!.putString(USER_GROUP, group)
        editor!!.commit()
    }

    fun setUserBPLID(BplId: String?) {
        editor!!.putString(USER_BPLID, BplId)
        editor!!.commit()
    }

    fun setUserAuthGroup(authGroup: String?) {
        editor!!.putString(
            USER_AUTH_GROUP,
            authGroup
        )
        editor!!.commit()
    }

    fun setUserTimeFormat(timeFormat: String?) {
        editor!!.putString(
            USER_TIME_FORMAT,
            timeFormat
        )
        editor!!.commit()
    }

    fun setUserDefaultWhs(defaultWhs: String?) {
        editor!!.putString(
            USER_DEFAULT_WHS,
            defaultWhs
        )
        editor!!.commit()
    }

    fun setSessionTimeOut(timeout: Int){
        editor!!.putInt(SESSION_TIMEOUT, timeout).commit()
    }


    //getters
    fun getWareHouseID(): String {
        return sharedPreferences?.getString(WAREHOUSE_ID, "")!!
    }

    fun getWareHouseName(): String {
        return sharedPreferences?.getString(WAREHOUSE_NAME, "")!!
    }

    fun getPreviousWareHouseID(): String {
        return sharedPreferences?.getString(PREV_WAREHOUSE_ID, "")!!
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences?.getBoolean(IS_LOGGED_IN, false)!!
    }

    fun isSynced(): Boolean {
        return sharedPreferences?.getBoolean(IS_SYNCED, false)!!
    }

    fun getCurrentUserName(): String {
        return sharedPreferences?.getString(CURR_USERNAME, "")!!
    }

    fun getCurrentPassword(): String {
        return sharedPreferences?.getString(CURR_PASSWORD, "")!!
    }

    fun getEmployeeID(): String {
        return sharedPreferences?.getString(EMP_ID, "")!!
    }

    fun getLastUpdated(): String {
        return sharedPreferences?.getString(LAST_UPDATED, "XX/XX/XXXX")!!
    }

    fun isPreviousUser(): Boolean {
        val currUser = sharedPreferences?.getString(CURR_USERNAME, "")
        val currPass = sharedPreferences?.getString(CURR_PASSWORD, "")
        val prevUser = sharedPreferences?.getString(PREV_USERNAME, "")
        val prevPass = sharedPreferences?.getString(PREV_PASSWORD, "")

        if (currUser!!.isEmpty() || prevUser!!.isEmpty() || currPass!!.isEmpty() || prevPass!!.isEmpty()) {
            return false
        } else {
            if (currUser == prevUser && currPass == prevPass) {
                return true
            }
            return false
        }
    }

    fun getBaseURL(): String {
        return getServer() + ":" + getPort()
    }


    fun getUserDfltRegion(): String {
        return sharedPreferences?.getString(
            USER_DFLT_REGION,
            ""
        )!!
    }

    fun getUserDfltStore(): String {
        return sharedPreferences?.getString(
            USER_DFLT_STORE,
            ""
        )!!
    }

    fun getServer(): String {
        return sharedPreferences?.getString(SERVER, "")!!
    }

    fun getPort(): String {
        return sharedPreferences?.getString(PORT, "")!!
    }

    fun getPlatform(): String {
        return sharedPreferences?.getString(PLATFORM, "")!!
    }

    fun getCompany(): String {
        return sharedPreferences?.getString(COMPANY, "")!!
    }



    fun getUserName(): String {
        return sharedPreferences?.getString(USER_NAME, "")!!
    }

    fun getUserId(): String {
        return sharedPreferences?.getString(USER_ID, "")!!
    }

    fun getSessionId(): String {
        return sharedPreferences?.getString(SESSION_ID, "")!!
    }

    fun getUserDefaultWhs(): String {
        return sharedPreferences?.getString(
            USER_DEFAULT_WHS,
            ""
        )!!
    }

    fun getUserBranch(): String {
        return sharedPreferences?.getString(USER_BRANCH, "")!!
    }

    fun getUserCode(): String {
        return sharedPreferences?.getString(USER_CODE, "")!!
    }

    fun getUserBplid(): String {
        return sharedPreferences?.getString(USER_BPLID, "")!!
    }

    fun getUserHeadOfficeCardCode(): String {
        return sharedPreferences?.getString(
            USER_HEAD_OFFICE_CARD_CODE,
            ""
        )!!
    }

    fun getUserAuthGroup(): String {
        return sharedPreferences?.getString(
            USER_AUTH_GROUP,
            ""
        )!!
    }

    fun getUserSyncTime(): String {
        return sharedPreferences?.getString(
            USER_SYNC_TIME,
            ""
        )!!
    }

    fun getUserTimeFormat(): String {
        return sharedPreferences?.getString(
            USER_TIME_FORMAT,
            ""
        )!!
    }

    fun getSessionTimeout(): Int {
        return sharedPreferences?.getInt(SESSION_TIMEOUT, 0)!!
    }


}