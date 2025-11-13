package com.divinebudget.app.ejgpoer.presentation.app

import android.app.Application
import android.util.Log
import android.view.WindowManager
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.appsflyer.deeplink.DeepLink
import com.appsflyer.deeplink.DeepLinkListener
import com.appsflyer.deeplink.DeepLinkResult
import com.divinebudget.app.data.AppDatabase
import com.divinebudget.app.data.preferences.UserPreferences
import com.divinebudget.app.data.repository.GoalRepository
import com.divinebudget.app.data.repository.TransactionRepository
import com.divinebudget.app.ejgpoer.presentation.di.divineBudgetModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


sealed interface DivineBudgetAppsFlyerState {
    data object DivineBudgetDefault : DivineBudgetAppsFlyerState
    data class DivineBudgetSuccess(val divineBudgetData: MutableMap<String, Any>?) :
        DivineBudgetAppsFlyerState

    data object DivineBudgetError : DivineBudgetAppsFlyerState
}

interface DivineBudgetAppsApi {
    @Headers("Content-Type: application/json")
    @GET(DIVINE_BUDGET_LIN)
    fun divineBudgetGetClient(
        @Query("devkey") devkey: String,
        @Query("device_id") deviceId: String,
    ): Call<MutableMap<String, Any>?>
}

private const val DIVINE_BUDGET_APP_DEV = "tRwkGQBaamvoua5AkSm8e"
private const val DIVINE_BUDGET_LIN = "com.divinebudget.app"

class DivineBudgetApplication : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val transactionRepository by lazy { TransactionRepository(database.transactionDao()) }
    val goalRepository by lazy { GoalRepository(database.goalDao()) }
    val userPreferences by lazy { UserPreferences(this) }
    
    private var divineBudgetIsResumed = false
    private var divineBudgetConversionTimeoutJob: Job? = null
    private var divineBudgetDeepLinkData: MutableMap<String, Any>? = null

    override fun onCreate() {
        super.onCreate()

        val appsflyer = AppsFlyerLib.getInstance()
        divineBudgetSetDebufLogger(appsflyer)
        divineBudgetMinTimeBetween(appsflyer)

        AppsFlyerLib.getInstance().subscribeForDeepLink(object : DeepLinkListener {
            override fun onDeepLinking(p0: DeepLinkResult) {
                when (p0.status) {
                    DeepLinkResult.Status.FOUND -> {
                        divineBudgetExtractDeepMap(p0.deepLink)
                        Log.d(DIVINE_BUDGET_MAIN_TAG, "onDeepLinking found: ${p0.deepLink}")

                    }

                    DeepLinkResult.Status.NOT_FOUND -> {
                        Log.d(DIVINE_BUDGET_MAIN_TAG, "onDeepLinking not found: ${p0.deepLink}")
                    }

                    DeepLinkResult.Status.ERROR -> {
                        Log.d(DIVINE_BUDGET_MAIN_TAG, "onDeepLinking error: ${p0.error}")
                    }
                }
            }

        })


        appsflyer.init(
            DIVINE_BUDGET_APP_DEV,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                    divineBudgetConversionTimeoutJob?.cancel()
                    Log.d(DIVINE_BUDGET_MAIN_TAG, "onConversionDataSuccess: $p0")

                    val afStatus = p0?.get("af_status")?.toString() ?: "null"
                    if (afStatus == "Organic") {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                delay(5000)
                                val api = divineBudgetGetApi(
                                    "https://gcdsdk.appsflyer.com/install_data/v4.0/",
                                    null
                                )
                                val response = api.divineBudgetGetClient(
                                    devkey = DIVINE_BUDGET_APP_DEV,
                                    deviceId = divineBudgetGetAppsflyerId()
                                ).awaitResponse()

                                val resp = response.body()
                                Log.d(DIVINE_BUDGET_MAIN_TAG, "After 5s: $resp")
                                if (resp?.get("af_status") == "Organic" || resp?.get("af_status") == null) {
                                    divineBudgetResume(DivineBudgetAppsFlyerState.DivineBudgetError)
                                } else {
                                    divineBudgetResume(
                                        DivineBudgetAppsFlyerState.DivineBudgetSuccess(resp)
                                    )
                                }
                            } catch (d: Exception) {
                                Log.d(DIVINE_BUDGET_MAIN_TAG, "Error: ${d.message}")
                                divineBudgetResume(DivineBudgetAppsFlyerState.DivineBudgetError)
                            }
                        }
                    } else {
                        divineBudgetResume(DivineBudgetAppsFlyerState.DivineBudgetSuccess(p0))
                    }
                }

                override fun onConversionDataFail(p0: String?) {
                    divineBudgetConversionTimeoutJob?.cancel()
                    Log.d(DIVINE_BUDGET_MAIN_TAG, "onConversionDataFail: $p0")
                    divineBudgetResume(DivineBudgetAppsFlyerState.DivineBudgetError)
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    Log.d(DIVINE_BUDGET_MAIN_TAG, "onAppOpenAttribution")
                }

                override fun onAttributionFailure(p0: String?) {
                    Log.d(DIVINE_BUDGET_MAIN_TAG, "onAttributionFailure: $p0")
                }
            },
            this
        )

        appsflyer.start(this, DIVINE_BUDGET_APP_DEV, object :
            AppsFlyerRequestListener {
            override fun onSuccess() {
                Log.d(DIVINE_BUDGET_MAIN_TAG, "AppsFlyer started")
            }

            override fun onError(p0: Int, p1: String) {
                Log.d(DIVINE_BUDGET_MAIN_TAG, "AppsFlyer start error: $p0 - $p1")
            }
        })
        divineBudgetStartConversionTimeout()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@DivineBudgetApplication)
            modules(
                listOf(
                    divineBudgetModule
                )
            )
        }
    }

    private fun divineBudgetExtractDeepMap(dl: DeepLink) {
        val map = mutableMapOf<String, Any>()
        dl.deepLinkValue?.let { map["deep_link_value"] = it }
        dl.mediaSource?.let { map["media_source"] = it }
        dl.campaign?.let { map["campaign"] = it }
        dl.campaignId?.let { map["campaign_id"] = it }
        dl.afSub1?.let { map["af_sub1"] = it }
        dl.afSub2?.let { map["af_sub2"] = it }
        dl.afSub3?.let { map["af_sub3"] = it }
        dl.afSub4?.let { map["af_sub4"] = it }
        dl.afSub5?.let { map["af_sub5"] = it }
        dl.matchType?.let { map["match_type"] = it }
        dl.clickHttpReferrer?.let { map["click_http_referrer"] = it }
        dl.getStringValue("timestamp")?.let { map["timestamp"] = it }
        dl.isDeferred?.let { map["is_deferred"] = it }
        for (i in 1..10) {
            val key = "deep_link_sub$i"
            dl.getStringValue(key)?.let {
                if (!map.containsKey(key)) {
                    map[key] = it
                }
            }
        }
        Log.d(DIVINE_BUDGET_MAIN_TAG, "Extracted DeepLink data: $map")
        divineBudgetDeepLinkData = map
    }

    private fun divineBudgetStartConversionTimeout() {
        divineBudgetConversionTimeoutJob = CoroutineScope(Dispatchers.Main).launch {
            delay(30000)
            if (!divineBudgetIsResumed) {
                Log.d(DIVINE_BUDGET_MAIN_TAG, "TIMEOUT: No conversion data received in 30s")
                divineBudgetResume(DivineBudgetAppsFlyerState.DivineBudgetError)
            }
        }
    }

    private fun divineBudgetResume(state: DivineBudgetAppsFlyerState) {
        divineBudgetConversionTimeoutJob?.cancel()
        if (state is DivineBudgetAppsFlyerState.DivineBudgetSuccess) {
            val convData = state.divineBudgetData ?: mutableMapOf()
            val deepData = divineBudgetDeepLinkData ?: mutableMapOf()
            val merged = mutableMapOf<String, Any>().apply {
                putAll(convData)
                for ((key, value) in deepData) {
                    if (!containsKey(key)) {
                        put(key, value)
                    }
                }
            }
            if (!divineBudgetIsResumed) {
                divineBudgetIsResumed = true
                divineBudgetConversionFlow.value = DivineBudgetAppsFlyerState.DivineBudgetSuccess(merged)
            }
        } else {
            if (!divineBudgetIsResumed) {
                divineBudgetIsResumed = true
                divineBudgetConversionFlow.value = state
            }
        }
    }

    private fun divineBudgetGetAppsflyerId(): String {
        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(this) ?: ""
        Log.d(DIVINE_BUDGET_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
        return appsflyrid
    }

    private fun divineBudgetSetDebufLogger(appsflyer: AppsFlyerLib) {
        appsflyer.setDebugLog(true)
    }

    private fun divineBudgetMinTimeBetween(appsflyer: AppsFlyerLib) {
        appsflyer.setMinTimeBetweenSessions(0)
    }

    private fun divineBudgetGetApi(url: String, client: OkHttpClient?): DivineBudgetAppsApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }

    companion object {
        var divineBudgetInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        val divineBudgetConversionFlow: MutableStateFlow<DivineBudgetAppsFlyerState> = MutableStateFlow(
            DivineBudgetAppsFlyerState.DivineBudgetDefault
        )
        var DIVINE_BUDGET_FB_LI: String? = null
        const val DIVINE_BUDGET_MAIN_TAG = "DivineBudgetMainTag"
    }
}