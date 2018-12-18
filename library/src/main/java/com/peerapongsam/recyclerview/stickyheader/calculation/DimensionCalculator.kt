package com.peerapongsam.recyclerview.stickyheader.calculation

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup

class DimensionCalculator {

    fun initMargins(margins: Rect, view: View) {
        val layoutParams = view.layoutParams
        if (layoutParams is ViewGroup.MarginLayoutParams) {
            initMarginRect(margins, layoutParams)
        } else {
            margins.set(0, 0, 0, 0)
        }
    }

    private fun initMarginRect(margins: Rect, layoutParams: ViewGroup.MarginLayoutParams) {
        margins.set(
            layoutParams.leftMargin,
            layoutParams.topMargin,
            layoutParams.rightMargin,
            layoutParams.bottomMargin
        )
    }
}
