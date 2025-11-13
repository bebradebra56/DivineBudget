package com.divinebudget.app.ejgpoer.data.utils

import android.util.Log
import com.divinebudget.app.ejgpoer.presentation.app.DivineBudgetApplication
import com.google.firebase.messaging.FirebaseMessaging
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DivineBudgetPushToken {

    suspend fun divineBudgetGetToken(): String = suspendCoroutine { continuation ->
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                if (!it.isSuccessful) {
                    continuation.resume(it.result)
                    Log.d(DivineBudgetApplication.DIVINE_BUDGET_MAIN_TAG, "Token error: ${it.exception}")
                } else {
                    continuation.resume(it.result)
                }
            }
        } catch (e: Exception) {
            Log.d(DivineBudgetApplication.DIVINE_BUDGET_MAIN_TAG, "FirebaseMessagingPushToken = null")
            continuation.resume("")
        }
    }


}