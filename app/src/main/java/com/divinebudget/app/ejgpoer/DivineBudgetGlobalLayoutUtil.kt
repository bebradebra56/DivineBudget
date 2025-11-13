package com.divinebudget.app.ejgpoer

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import com.divinebudget.app.ejgpoer.presentation.app.DivineBudgetApplication

class DivineBudgetGlobalLayoutUtil {

    private var divineBudgetMChildOfContent: View? = null
    private var divineBudgetUsableHeightPrevious = 0

    fun divineBudgetAssistActivity(activity: Activity) {
        val content = activity.findViewById<FrameLayout>(android.R.id.content)
        divineBudgetMChildOfContent = content.getChildAt(0)

        divineBudgetMChildOfContent?.viewTreeObserver?.addOnGlobalLayoutListener {
            possiblyResizeChildOfContent(activity)
        }
    }

    private fun possiblyResizeChildOfContent(activity: Activity) {
        val divineBudgetUsableHeightNow = divineBudgetComputeUsableHeight()
        if (divineBudgetUsableHeightNow != divineBudgetUsableHeightPrevious) {
            val divineBudgetUsableHeightSansKeyboard = divineBudgetMChildOfContent?.rootView?.height ?: 0
            val divineBudgetHeightDifference = divineBudgetUsableHeightSansKeyboard - divineBudgetUsableHeightNow

            if (divineBudgetHeightDifference > (divineBudgetUsableHeightSansKeyboard / 4)) {
                activity.window.setSoftInputMode(DivineBudgetApplication.divineBudgetInputMode)
            } else {
                activity.window.setSoftInputMode(DivineBudgetApplication.divineBudgetInputMode)
            }
//            mChildOfContent?.requestLayout()
            divineBudgetUsableHeightPrevious = divineBudgetUsableHeightNow
        }
    }

    private fun divineBudgetComputeUsableHeight(): Int {
        val r = Rect()
        divineBudgetMChildOfContent?.getWindowVisibleDisplayFrame(r)
        return r.bottom - r.top  // Visible height без status bar
    }
}