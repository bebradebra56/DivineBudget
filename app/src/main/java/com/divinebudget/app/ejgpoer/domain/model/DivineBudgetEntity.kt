package com.divinebudget.app.ejgpoer.domain.model

import com.google.gson.annotations.SerializedName


data class DivineBudgetEntity (
    @SerializedName("ok")
    val divineBudgetOk: String,
    @SerializedName("url")
    val divineBudgetUrl: String,
    @SerializedName("expires")
    val divineBudgetExpires: Long,
)