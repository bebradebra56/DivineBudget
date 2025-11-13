package com.divinebudget.app.ejgpoer.domain.model

import com.google.gson.annotations.SerializedName


private const val DIVINE_BUDGET_A = "com.divinebudget.app"
private const val DIVINE_BUDGET_B = "divinebudget"
data class DivineBudgetParam (
    @SerializedName("af_id")
    val divineBudgetAfId: String,
    @SerializedName("bundle_id")
    val divineBudgetBundleId: String = DIVINE_BUDGET_A,
    @SerializedName("os")
    val divineBudgetOs: String = "Android",
    @SerializedName("store_id")
    val divineBudgetStoreId: String = DIVINE_BUDGET_A,
    @SerializedName("locale")
    val divineBudgetLocale: String,
    @SerializedName("push_token")
    val divineBudgetPushToken: String,
    @SerializedName("firebase_project_id")
    val divineBudgetFirebaseProjectId: String = DIVINE_BUDGET_B,

    )