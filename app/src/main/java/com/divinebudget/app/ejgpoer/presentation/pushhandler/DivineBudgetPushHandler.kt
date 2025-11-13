package com.divinebudget.app.ejgpoer.presentation.pushhandler

import android.os.Bundle
import android.util.Log
import com.divinebudget.app.ejgpoer.presentation.app.DivineBudgetApplication

class DivineBudgetPushHandler {
    fun divineBudgetHandlePush(extras: Bundle?) {
        Log.d(DivineBudgetApplication.DIVINE_BUDGET_MAIN_TAG, "Extras from Push = ${extras?.keySet()}")
        if (extras != null) {
            val map = divineBudgetBundleToMap(extras)
            Log.d(DivineBudgetApplication.DIVINE_BUDGET_MAIN_TAG, "Map from Push = $map")
            map?.let {
                if (map.containsKey("url")) {
                    DivineBudgetApplication.DIVINE_BUDGET_FB_LI = map["url"]
                    Log.d(DivineBudgetApplication.DIVINE_BUDGET_MAIN_TAG, "UrlFromActivity = $map")
                }
            }
        } else {
            Log.d(DivineBudgetApplication.DIVINE_BUDGET_MAIN_TAG, "Push data no!")
        }
    }

    private fun divineBudgetBundleToMap(extras: Bundle): Map<String, String?>? {
        val map: MutableMap<String, String?> = HashMap()
        val ks = extras.keySet()
        val iterator: Iterator<String> = ks.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            map[key] = extras.getString(key)
        }
        return map
    }

}