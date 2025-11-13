package com.divinebudget.app.ejgpoer.presentation.ui.view

import android.annotation.SuppressLint
import android.widget.FrameLayout
import androidx.lifecycle.ViewModel

class DivineBudgetDataStore : ViewModel(){
    val divineBudgetViList: MutableList<DivineBudgetVi> = mutableListOf()
    var divineBudgetIsFirstCreate = true
    @SuppressLint("StaticFieldLeak")
    lateinit var divineBudgetContainerView: FrameLayout
    @SuppressLint("StaticFieldLeak")
    lateinit var divineBudgetView: DivineBudgetVi

}