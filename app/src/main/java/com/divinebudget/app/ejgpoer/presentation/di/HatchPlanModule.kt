package com.divinebudget.app.ejgpoer.presentation.di

import com.divinebudget.app.ejgpoer.data.repo.DivineBudgetRepository
import com.divinebudget.app.ejgpoer.data.shar.DivineBudgetSharedPreference
import com.divinebudget.app.ejgpoer.data.utils.DivineBudgetPushToken
import com.divinebudget.app.ejgpoer.data.utils.DivineBudgetSystemService
import com.divinebudget.app.ejgpoer.domain.usecases.DivineBudgetGetAllUseCase
import com.divinebudget.app.ejgpoer.presentation.pushhandler.DivineBudgetPushHandler
import com.divinebudget.app.ejgpoer.presentation.ui.load.DivineBudgetLoadViewModel
import com.divinebudget.app.ejgpoer.presentation.ui.view.DivineBudgetViFun
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val divineBudgetModule = module {
    factory {
        DivineBudgetPushHandler()
    }
    single {
        DivineBudgetRepository()
    }
    single {
        DivineBudgetSharedPreference(get())
    }
    factory {
        DivineBudgetPushToken()
    }
    factory {
        DivineBudgetSystemService(get())
    }
    factory {
        DivineBudgetGetAllUseCase(
            get(), get(), get()
        )
    }
    factory {
        DivineBudgetViFun(get())
    }
    viewModel {
        DivineBudgetLoadViewModel(get(), get(), get())
    }
}