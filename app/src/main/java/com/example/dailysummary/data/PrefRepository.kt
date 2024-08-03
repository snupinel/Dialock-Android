package com.example.dailysummary.data

interface PrefRepository {
    fun getPref(key: String): String?
    fun setPref(key: String,data: String)
    fun clearPref(key:String)
}