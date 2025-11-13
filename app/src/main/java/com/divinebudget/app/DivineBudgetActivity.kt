package com.divinebudget.app

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.divinebudget.app.ejgpoer.DivineBudgetGlobalLayoutUtil
import com.divinebudget.app.ejgpoer.divineBudgetSetupSystemBars
import com.divinebudget.app.ejgpoer.presentation.app.DivineBudgetApplication
import com.divinebudget.app.ejgpoer.presentation.pushhandler.DivineBudgetPushHandler
import org.koin.android.ext.android.inject

class DivineBudgetActivity : AppCompatActivity() {

    private val divineBudgetPushHandler by inject<DivineBudgetPushHandler>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        divineBudgetSetupSystemBars()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_divine_budget)

        val divineBudgetRootView = findViewById<View>(android.R.id.content)
        DivineBudgetGlobalLayoutUtil().divineBudgetAssistActivity(this)
        ViewCompat.setOnApplyWindowInsetsListener(divineBudgetRootView) { divineBudgetView, divineBudgetInsets ->
            val divineBudgetSystemBars = divineBudgetInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val divineBudgetDisplayCutout = divineBudgetInsets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val divineBudgetIme = divineBudgetInsets.getInsets(WindowInsetsCompat.Type.ime())


            val divineBudgetTopPadding = maxOf(divineBudgetSystemBars.top, divineBudgetDisplayCutout.top)
            val divineBudgetLeftPadding = maxOf(divineBudgetSystemBars.left, divineBudgetDisplayCutout.left)
            val divineBudgetRightPadding = maxOf(divineBudgetSystemBars.right, divineBudgetDisplayCutout.right)
            window.setSoftInputMode(DivineBudgetApplication.divineBudgetInputMode)

            if (window.attributes.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) {
                Log.d(DivineBudgetApplication.DIVINE_BUDGET_MAIN_TAG, "ADJUST PUN")
                val divineBudgetBottomInset = maxOf(divineBudgetSystemBars.bottom, divineBudgetDisplayCutout.bottom)

                divineBudgetView.setPadding(divineBudgetLeftPadding, divineBudgetTopPadding, divineBudgetRightPadding, 0)

                divineBudgetView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = divineBudgetBottomInset
                }
            } else {
                Log.d(DivineBudgetApplication.DIVINE_BUDGET_MAIN_TAG, "ADJUST RESIZE")

                val divineBudgetBottomInset = maxOf(divineBudgetSystemBars.bottom, divineBudgetDisplayCutout.bottom, divineBudgetIme.bottom)

                divineBudgetView.setPadding(divineBudgetLeftPadding, divineBudgetTopPadding, divineBudgetRightPadding, 0)

                divineBudgetView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = divineBudgetBottomInset
                }
            }



            WindowInsetsCompat.CONSUMED
        }
        Log.d(DivineBudgetApplication.DIVINE_BUDGET_MAIN_TAG, "Activity onCreate()")
        divineBudgetPushHandler.divineBudgetHandlePush(intent.extras)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            divineBudgetSetupSystemBars()
        }
    }

    override fun onResume() {
        super.onResume()
        divineBudgetSetupSystemBars()
    }
}