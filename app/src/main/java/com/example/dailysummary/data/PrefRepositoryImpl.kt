package com.example.dailysummary.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
): PrefRepository {

    private val sharedPref = context.getSharedPreferences(
        "DS",
        Context.MODE_PRIVATE
    )
    override fun getPref(key: String): String? {
        return sharedPref.getString(key, null)
    }
    override fun setPref(key: String,data: String) {
        with(sharedPref.edit()){
            putString(key,data)
            apply()
        }
    }

    override fun clearPref(key: String) {
        with(sharedPref.edit()){
            this.remove(key)
            apply()
        }
    }

}