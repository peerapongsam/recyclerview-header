package com.peerapongsam.recyclerview.stickyheader.renderer

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.peerapongsam.recyclerview.stickyheader.calculation.DimensionCalculator
import com.peerapongsam.recyclerview.stickyheader.orientation.OrientationProvider

class HeaderRenderer(
    private val orientationProvider: OrientationProvider,
    private val dimensionCalculator: DimensionCalculator = DimensionCalculator()
) {

    private val tempRect: Rect = Rect()

    fun drawHeader(recyclerView: RecyclerView, canvas: Canvas, header: View, offset: Rect) {
        canvas.save()

        if (recyclerView.layoutManager?.clipToPadding == true) {
            initClipRectForHeader(tempRect, recyclerView, header)
            canvas.clipRect(tempRect)
        }

        canvas.translate(offset.left.toFloat(), offset.top.toFloat())
        header.draw(canvas)

        canvas.restore()
    }

    private fun initClipRectForHeader(clipRect: Rect, recyclerView: RecyclerView, header: View) {
        dimensionCalculator.initMargins(clipRect, header)
        if (orientationProvider.getOrientation(recyclerView) == LinearLayout.VERTICAL) {
            clipRect.set(
                recyclerView.paddingLeft,
                recyclerView.paddingTop,
                recyclerView.width - recyclerView.paddingRight - clipRect.right,
                recyclerView.height - recyclerView.paddingBottom
            )
        } else {
            clipRect.set(
                recyclerView.paddingLeft,
                recyclerView.paddingTop,
                recyclerView.width - recyclerView.paddingRight,
                recyclerView.height - recyclerView.paddingBottom - clipRect.bottom
            )
        }
    }
}
