package com.divinebudget.app.ejgpoer.presentation.ui.load

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.divinebudget.app.ejgpoer.data.shar.DivineBudgetSharedPreference
import com.divinebudget.app.ejgpoer.data.utils.DivineBudgetSystemService
import com.divinebudget.app.ejgpoer.domain.usecases.DivineBudgetGetAllUseCase
import com.divinebudget.app.ejgpoer.presentation.app.DivineBudgetAppsFlyerState
import com.divinebudget.app.ejgpoer.presentation.app.DivineBudgetApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DivineBudgetLoadViewModel(
    private val divineBudgetGetAllUseCase: DivineBudgetGetAllUseCase,
    private val divineBudgetSharedPreference: DivineBudgetSharedPreference,
    private val divineBudgetSystemService: DivineBudgetSystemService
) : ViewModel() {

    private val _divineBudgetHomeScreenState: MutableStateFlow<DivineBudgetHomeScreenState> =
        MutableStateFlow(DivineBudgetHomeScreenState.DivineBudgetLoading)
    val divineBudgetHomeScreenState = _divineBudgetHomeScreenState.asStateFlow()

    private var divineBudgetGetApps = false


    init {
        viewModelScope.launch {
            when (divineBudgetSharedPreference.divineBudgetAppState) {
                0 -> {
                    if (divineBudgetSystemService.divineBudgetIsOnline()) {
                        DivineBudgetApplication.divineBudgetConversionFlow.collect {
                            when(it) {
                                DivineBudgetAppsFlyerState.DivineBudgetDefault -> {}
                                DivineBudgetAppsFlyerState.DivineBudgetError -> {
                                    divineBudgetSharedPreference.divineBudgetAppState = 2
                                    _divineBudgetHomeScreenState.value =
                                        DivineBudgetHomeScreenState.DivineBudgetError
                                    divineBudgetGetApps = true
                                }
                                is DivineBudgetAppsFlyerState.DivineBudgetSuccess -> {
                                    if (!divineBudgetGetApps) {
                                        divineBudgetGetData(it.divineBudgetData)
                                        divineBudgetGetApps = true
                                    }
                                }
                            }
                        }
                    } else {
                        _divineBudgetHomeScreenState.value =
                            DivineBudgetHomeScreenState.DivineBudgetNotInternet
                    }
                }
                1 -> {
                    if (divineBudgetSystemService.divineBudgetIsOnline()) {
                        if (DivineBudgetApplication.DIVINE_BUDGET_FB_LI != null) {
                            _divineBudgetHomeScreenState.value =
                                DivineBudgetHomeScreenState.DivineBudgetSuccess(
                                    DivineBudgetApplication.DIVINE_BUDGET_FB_LI.toString()
                                )
                        } else if (System.currentTimeMillis() / 1000 > divineBudgetSharedPreference.divineBudgetExpired) {
                            Log.d(DivineBudgetApplication.DIVINE_BUDGET_MAIN_TAG, "Current time more then expired, repeat request")
                            DivineBudgetApplication.divineBudgetConversionFlow.collect {
                                when(it) {
                                    DivineBudgetAppsFlyerState.DivineBudgetDefault -> {}
                                    DivineBudgetAppsFlyerState.DivineBudgetError -> {
                                        _divineBudgetHomeScreenState.value =
                                            DivineBudgetHomeScreenState.DivineBudgetSuccess(
                                                divineBudgetSharedPreference.divineBudgetSavedUrl
                                            )
                                        divineBudgetGetApps = true
                                    }
                                    is DivineBudgetAppsFlyerState.DivineBudgetSuccess -> {
                                        if (!divineBudgetGetApps) {
                                            divineBudgetGetData(it.divineBudgetData)
                                            divineBudgetGetApps = true
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(DivineBudgetApplication.DIVINE_BUDGET_MAIN_TAG, "Current time less then expired, use saved url")
                            _divineBudgetHomeScreenState.value =
                                DivineBudgetHomeScreenState.DivineBudgetSuccess(
                                    divineBudgetSharedPreference.divineBudgetSavedUrl
                                )
                        }
                    } else {
                        _divineBudgetHomeScreenState.value =
                            DivineBudgetHomeScreenState.DivineBudgetNotInternet
                    }
                }
                2 -> {
                    _divineBudgetHomeScreenState.value =
                        DivineBudgetHomeScreenState.DivineBudgetError
                }
            }
        }
    }


    private suspend fun divineBudgetGetData(conversation: MutableMap<String, Any>?) {
        val divineBudgetData = divineBudgetGetAllUseCase.invoke(conversation)
        if (divineBudgetSharedPreference.divineBudgetAppState == 0) {
            if (divineBudgetData == null) {
                divineBudgetSharedPreference.divineBudgetAppState = 2
                _divineBudgetHomeScreenState.value =
                    DivineBudgetHomeScreenState.DivineBudgetError
            } else {
                divineBudgetSharedPreference.divineBudgetAppState = 1
                divineBudgetSharedPreference.apply {
                    divineBudgetExpired = divineBudgetData.divineBudgetExpires
                    divineBudgetSavedUrl = divineBudgetData.divineBudgetUrl
                }
                _divineBudgetHomeScreenState.value =
                    DivineBudgetHomeScreenState.DivineBudgetSuccess(divineBudgetData.divineBudgetUrl)
            }
        } else  {
            if (divineBudgetData == null) {
                _divineBudgetHomeScreenState.value =
                    DivineBudgetHomeScreenState.DivineBudgetSuccess(divineBudgetSharedPreference.divineBudgetSavedUrl)
            } else {
                divineBudgetSharedPreference.apply {
                    divineBudgetExpired = divineBudgetData.divineBudgetExpires
                    divineBudgetSavedUrl = divineBudgetData.divineBudgetUrl
                }
                _divineBudgetHomeScreenState.value =
                    DivineBudgetHomeScreenState.DivineBudgetSuccess(divineBudgetData.divineBudgetUrl)
            }
        }
    }


    sealed class DivineBudgetHomeScreenState {
        data object DivineBudgetLoading : DivineBudgetHomeScreenState()
        data object DivineBudgetError : DivineBudgetHomeScreenState()
        data class DivineBudgetSuccess(val data: String) : DivineBudgetHomeScreenState()
        data object DivineBudgetNotInternet: DivineBudgetHomeScreenState()
    }
}