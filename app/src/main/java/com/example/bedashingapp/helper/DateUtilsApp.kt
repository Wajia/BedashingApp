package com.example.bedashingapp.helper

import android.text.TextUtils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


object DateUtilsApp {
    val inputFormate = "yyyy-MM-dd hh:mm:ss"
    val outputFormate = "dd MMM yyyy hh:mm a"
    const val DATEFORMATE = "yyyy-MM-dd"

    private fun getDateFromString(sdf: SimpleDateFormat, dateTimeString: String): Date {
        val calendar = Calendar.getInstance()
        try {
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            calendar.time = sdf.parse(dateTimeString)
            val date = sdf.parse(dateTimeString)
            sdf.timeZone = TimeZone.getDefault()
            calendar.time = sdf.parse(sdf.format(date))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return calendar.time
    }
    fun getFormattedDateStringFromStringDate(output : SimpleDateFormat,input: SimpleDateFormat,dateString: String?) : String{

        val date = input.parse(dateString)
        val date2 = output.format(date)
        return date2
    }

    fun parseDate(dateString: String?): Long {
        try {
            if (TextUtils.isEmpty(dateString) || dateString.equals(
                    "null",
                    ignoreCase = true
                )
            ) return 0
            var simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            if (dateString!!.contains("T")) {
                simpleDateFormat = if (dateString.contains("."))
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
                else
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
            } else if (dateString.contains(" "))
                simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            val date =
                getDateFromString(simpleDateFormat, dateString.replace("Z$".toRegex(), "+0000"))
            return date.time
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0L
    }


    fun getTimeInMilliSecond(dateTimeMS: String,inputFormate: String): Long {
        val cal = Calendar.getInstance(Locale.getDefault())
        try {
            if (TextUtils.isEmpty(dateTimeMS)) return cal.timeInMillis
            if (dateTimeMS.equals("N/A", ignoreCase = true)) return cal.timeInMillis
            var simpleDateFormat = SimpleDateFormat(inputFormate)
            if (dateTimeMS.contains("T")) {
                if (dateTimeMS.contains(".")) {
                    simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                } else
                    simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            }
            val updateDate = simpleDateFormat.parse(dateTimeMS)
            cal.timeInMillis = updateDate.time
            return updateDate.time
        } catch (e1: Exception) {
            e1.printStackTrace()
            return cal.timeInMillis
        }
    }

    fun getDateFromSecond(dateTimeSecond: String): Date {
        val calendar = Calendar.getInstance()
        try {
            if (!TextUtils.isEmpty(dateTimeSecond)) {
                val timestamp = java.lang.Long.parseLong(dateTimeSecond) * 1000
                calendar.timeInMillis = timestamp
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return calendar.time
    }

    val currentDateTimeMS: Long
        get() {
            try {
                val calendar = Calendar.getInstance()
                return calendar.timeInMillis
            } catch (e: Exception) {
                e.printStackTrace()
                return 0
            }
        }

    fun getUTCCurrentDateTimeMS(): Long {
        return try {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    fun getUpdateTime(dateTimeMS: String): String {
        try {
            val HH_MM = SimpleDateFormat("hh:mm a", Locale.getDefault())
            //            HH_MM.setTimeZone(TimeZone.getTimeZone("UTC"));
            if (TextUtils.isEmpty(dateTimeMS)) return "N/A"
            if (dateTimeMS.equals("N/A", ignoreCase = true)) return "N/A"
            val cal = Calendar.getInstance(Locale.getDefault())
            cal.timeInMillis = java.lang.Long.parseLong(dateTimeMS)
            return HH_MM.format(cal.time)
        } catch (e1: Exception) {
            e1.printStackTrace()
            return "N/A"
        }
    }

    fun getCurrentTime(): String {
        try {
            val HH_MM = SimpleDateFormat("hhmm", Locale.getDefault())
            //            HH_MM.setTimeZone(TimeZone.getTimeZone("UTC"));
            val cal = Calendar.getInstance(Locale.getDefault())
//            cal.timeInMillis = java.lang.Long.parseLong(dateTimeMS)
            return HH_MM.format(cal.time)
        } catch (e1: Exception) {
            e1.printStackTrace()
            return "N/A"
        }
    }

    fun getUTCFormattedDateTimeString(sdf: SimpleDateFormat, date: Date?): String {
        try {
            if (date == null) return ""
//            sdf.timeZone = TimeZone.getTimeZone("UTC")
            return sdf.format(date)
        } catch (e: Exception) {
            return date!!.toString()
        }
    }



    fun convertUTCToLocalTimeDate(
        input: SimpleDateFormat,
        output: SimpleDateFormat,
        dateTime: String
    ): Date {
        if (TextUtils.isEmpty(dateTime))
            return Date()
        if (dateTime == "0000-00-00 00:00:00") {
            return Date()
        }
        return try {
            input.timeZone = TimeZone.getTimeZone("UTC")
            if (dateTime.equals("N/A", ignoreCase = true)) Date() else input.parse(dateTime)
        } catch (e1: Exception) {
            e1.printStackTrace()
            Date()
        }
    }

    fun convertToLocalTime(
        input: SimpleDateFormat,
        output: SimpleDateFormat,
        dateTime: String
    ): String {
        if (TextUtils.isEmpty(dateTime))
            return output.format(Date())
        try {
            if (TextUtils.isEmpty(dateTime)) return "N/A"
            val cal = Calendar.getInstance(Locale.getDefault())
            val updateDate = input.parse(dateTime)
            cal.timeInMillis = updateDate.time
            return output.format(cal.time)
        } catch (e1: Exception) {
            e1.printStackTrace()
            return output.format(Date())
        }
    }

    fun convertDateFormat(inputDate: String,inputDateFormat:String = "yyyy-MM-dd",outputDateFormat:String = "dd MMM yyyy"): String{
        val mParsedDate: Date
        val mOutputDateString: String
        val mInputDateFormat = SimpleDateFormat(inputDateFormat, java.util.Locale.getDefault())
        val mOutputDateFormat =
            SimpleDateFormat(outputDateFormat, java.util.Locale.getDefault())
        mParsedDate = mInputDateFormat.parse(inputDate)
        mOutputDateString = mOutputDateFormat.format(mParsedDate)
        return mOutputDateString
    }

    fun convertDateFromUnixFormat(inputDate: String, outputDateFormat: String = "dd/MM/yyyy"): String{
        var num = inputDate.replace("/", "")
        num = num.replace("(", "")
        num = num.replace(")", "")
        num = num.replace("Date", "")

        var date = Date(num.toLong())
        return SimpleDateFormat(outputDateFormat).format(date)
    }

    fun getDateTimeFromMiliSecond(milliSeconds: Long, dateFormat: String?): String? {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat)

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    fun getTimeFromMiliSecond(milliSeconds: Long): String{
        val formatter: DateFormat = SimpleDateFormat("hh:mm:ss a")
        formatter.timeZone = TimeZone.getTimeZone("GMT")
        return formatter.format(Date(milliSeconds))
    }

    fun getSecondFromMiliSecond(milliSeconds: Long): String{
        val buf = StringBuffer()
        val second = (milliSeconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()
        buf.append(String.format("%02d",second))
        return  buf.toString()
    }

    fun getHours(milliSeconds: Long): Int{
        val hours = (milliSeconds / (1000 * 60 * 60)).toInt()
        return hours
    }



}
