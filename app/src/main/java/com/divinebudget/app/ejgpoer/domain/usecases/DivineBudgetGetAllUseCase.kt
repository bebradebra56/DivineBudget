package com.divinebudget.app.ejgpoer.domain.usecases

import android.util.Log
import com.divinebudget.app.ejgpoer.data.repo.DivineBudgetRepository
import com.divinebudget.app.ejgpoer.data.utils.DivineBudgetPushToken
import com.divinebudget.app.ejgpoer.data.utils.DivineBudgetSystemService
import com.divinebudget.app.ejgpoer.domain.model.DivineBudgetEntity
import com.divinebudget.app.ejgpoer.domain.model.DivineBudgetParam
import com.divinebudget.app.ejgpoer.presentation.app.DivineBudgetApplication

class DivineBudgetGetAllUseCase(
    private val divineBudgetRepository: DivineBudgetRepository,
    private val divineBudgetSystemService: DivineBudgetSystemService,
    private val divineBudgetPushToken: DivineBudgetPushToken,
) {
    suspend operator fun invoke(conversion: MutableMap<String, Any>?) : DivineBudgetEntity?{
        val params = DivineBudgetParam(
            divineBudgetLocale = divineBudgetSystemService.divineBudgetGetLocale(),
            divineBudgetPushToken = divineBudgetPushToken.divineBudgetGetToken(),
            divineBudgetAfId = divineBudgetSystemService.divineBudgetGetAppsflyerId()
        )
        Log.d(DivineBudgetApplication.DIVINE_BUDGET_MAIN_TAG, "Params for request: $params")
        return divineBudgetRepository.divineBudgetGetClient(params, conversion)
    }



}