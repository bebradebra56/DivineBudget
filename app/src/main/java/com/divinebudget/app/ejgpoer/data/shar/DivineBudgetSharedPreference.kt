package com.divinebudget.app.ejgpoer.data.shar

import android.content.Context
import androidx.core.content.edit

class DivineBudgetSharedPreference(context: Context) {
    private val divineBudgetPrefs = context.getSharedPreferences("divineBudgetSharedPrefsAb", Context.MODE_PRIVATE)

    var divineBudgetSavedUrl: String
        get() = divineBudgetPrefs.getString(DIVINE_BUDGET_SAVED_URL, "") ?: ""
        set(value) = divineBudgetPrefs.edit { putString(DIVINE_BUDGET_SAVED_URL, value) }

    var divineBudgetExpired : Long
        get() = divineBudgetPrefs.getLong(DIVINE_BUDGET_EXPIRED, 0L)
        set(value) = divineBudgetPrefs.edit { putLong(DIVINE_BUDGET_EXPIRED, value) }

    var divineBudgetAppState: Int
        get() = divineBudgetPrefs.getInt(DIVINE_BUDGET_APPLICATION_STATE, 0)
        set(value) = divineBudgetPrefs.edit { putInt(DIVINE_BUDGET_APPLICATION_STATE, value) }

    var divineBudgetNotificationRequest: Long
        get() = divineBudgetPrefs.getLong(DIVINE_BUDGET_NOTIFICAITON_REQUEST, 0L)
        set(value) = divineBudgetPrefs.edit { putLong(DIVINE_BUDGET_NOTIFICAITON_REQUEST, value) }

    var divineBudgetNotificationRequestedBefore: Boolean
        get() = divineBudgetPrefs.getBoolean(DIVINE_BUDGET_NOTIFICATION_REQUEST_BEFORE, false)
        set(value) = divineBudgetPrefs.edit { putBoolean(
            DIVINE_BUDGET_NOTIFICATION_REQUEST_BEFORE, value) }

    companion object {
        private const val DIVINE_BUDGET_SAVED_URL = "divineBudgetSavedUrl"
        private const val DIVINE_BUDGET_EXPIRED = "divineBudgetExpired"
        private const val DIVINE_BUDGET_APPLICATION_STATE = "divineBudgetApplicationState"
        private const val DIVINE_BUDGET_NOTIFICAITON_REQUEST = "divineBudgetNotificationRequest"
        private const val DIVINE_BUDGET_NOTIFICATION_REQUEST_BEFORE = "divineBudgetNotificationRequestedBefore"
    }
}