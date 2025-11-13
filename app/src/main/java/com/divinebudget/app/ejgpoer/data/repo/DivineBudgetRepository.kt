package com.divinebudget.app.ejgpoer.data.repo

import android.util.Log
import com.divinebudget.app.ejgpoer.domain.model.DivineBudgetEntity
import com.divinebudget.app.ejgpoer.domain.model.DivineBudgetParam
import com.divinebudget.app.ejgpoer.presentation.app.DivineBudgetApplication.Companion.DIVINE_BUDGET_MAIN_TAG
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface DivineBudgetApi {
    @Headers("Content-Type: application/json")
    @POST("config.php")
    fun divineBudgetGetClient(
        @Body jsonString: JsonObject,
    ): Call<DivineBudgetEntity>
}


private const val DIVINE_BUDGET_MAIN = "https://divinebudget.com/"
class DivineBudgetRepository {

    suspend fun divineBudgetGetClient(
        divineBudgetParam: DivineBudgetParam,
        divineBudgetConversion: MutableMap<String, Any>?
    ): DivineBudgetEntity? {
        val gson = Gson()
        val api = divineBudgetGetApi(DIVINE_BUDGET_MAIN, null)

        val divineBudgetJsonObject = gson.toJsonTree(divineBudgetParam).asJsonObject
        divineBudgetConversion?.forEach { (key, value) ->
            val element: JsonElement = gson.toJsonTree(value)
            divineBudgetJsonObject.add(key, element)
        }
        return try {
            val divineBudgetRequest: Call<DivineBudgetEntity> = api.divineBudgetGetClient(
                jsonString = divineBudgetJsonObject,
            )
            val divineBudgetResult = divineBudgetRequest.awaitResponse()
            Log.d(DIVINE_BUDGET_MAIN_TAG, "Retrofit: Result code: ${divineBudgetResult.code()}")
            if (divineBudgetResult.code() == 200) {
                Log.d(DIVINE_BUDGET_MAIN_TAG, "Retrofit: Get request success")
                Log.d(DIVINE_BUDGET_MAIN_TAG, "Retrofit: Code = ${divineBudgetResult.code()}")
                Log.d(DIVINE_BUDGET_MAIN_TAG, "Retrofit: ${divineBudgetResult.body()}")
                divineBudgetResult.body()
            } else {
                null
            }
        } catch (e: java.lang.Exception) {
            Log.d(DIVINE_BUDGET_MAIN_TAG, "Retrofit: Get request failed")
            Log.d(DIVINE_BUDGET_MAIN_TAG, "Retrofit: ${e.message}")
            null
        }
    }


    private fun divineBudgetGetApi(url: String, client: OkHttpClient?) : DivineBudgetApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }


}
