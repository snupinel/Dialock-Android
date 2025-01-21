package com.example.dailysummary.data

import com.example.dailysummary.dto.Setting

interface PrefRepository {
    fun getPref(key: String): String?
    fun setPref(key: String,data: String)
    fun clearPref(key:String)

    fun setRefSetting(setting: Setting)

    fun getRefSetting(): Setting?
}