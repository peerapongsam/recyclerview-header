package com.peerapongsam.recyclerview.stickyheader

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.peerapongsam.recyclerview.stickyheader.caching.HeaderProvider
import com.peerapongsam.recyclerview.stickyheader.calculation.DimensionCalculator
import com.peerapongsam.recyclerview.stickyheader.orientation.OrientationProvider

class HeaderPositionCalculator(
    private val adapter: StickyHeaderRecyclerViewAdapter<*>,
    private val headerProvider: HeaderProvider,
    private val orientationProvider: OrientationProvider,
    private val dimensionCalculator: DimensionCalculator
) {
    private val tempRect1 = Rect()
    private val tempRect2 = Rect()

    fun hasStickyHeader(itemView: View, orientation: Int, position: Int): Boolean {
        val offset: Int
        val margin: Int
        dimensionCalculator.initMargins(tempRect1, itemView)
        if (orientation == LinearLayout.VERTICAL) {
            offset = itemView.top
            margin = tempRect1.top
        } else {
            offset = itemView.left
            margin = tempRect1.left
        }
        return offset <= margin && adapter.getHeaderId(position) >= 0
    }

    fun hasNewHeader(position: Int, isReverseLayout: Boolean): Boolean {
        if (indexOutOfBounds(position)) {
            return false
        }

        val headerId = adapter.getHeaderId(position)
        if (headerId < 0) {
            return false
        }

        var nextItemHeaderId: Long = -1
        val nextItemPosition = position + (if (isReverseLayout) 1 else -1)
        if (!indexOutOfBounds(nextItemPosition)) {
            nextItemHeaderId = adapter.getHeaderId(nextItemPosition)
        }
        val firstItemPosition = if (isReverseLayout) adapter.getItemCount() - 1 else 0

        return position == firstItemPosition || headerId != nextItemHeaderId
    }

    private fun indexOutOfBounds(position: Int): Boolean {
        return position < 0 || position >= adapter.getItemCount()
    }

    fun initHeaderBounds(
        bounds: Rect,
        recyclerView: RecyclerView,
        header: View,
        firstView: View,
        firstHeader: Boolean
    ) {
        val orientation = orientationProvider.getOrientation(recyclerView)
        initDefaultHeaderOffset(bounds, recyclerView, header, firstView, orientation)

        if (firstHeader && isStickyHeaderBeingPushedOffscreen(recyclerView, header)) {
            val viewAfterNextHeader = getFirstViewUnobscuredByHeader(recyclerView, header) ?: return
            val firstViewUnderHeaderPosition = recyclerView.getChildAdapterPosition(viewAfterNextHeader)
            val secondHeader = headerProvider.getHeader(recyclerView, firstViewUnderHeaderPosition)
            translateHeaderWithNextHeader(
                recyclerView,
                orientationProvider.getOrientation(recyclerView),
                bounds,
                header,
                viewAfterNextHeader,
                secondHeader
            )
        }

    }

    private fun initDefaultHeaderOffset(
        headerMargins: Rect,
        recyclerView: RecyclerView,
        header: View,
        firstView: View,
        orientation: Int
    ) {
        val translationX: Int
        val translationY: Int
        dimensionCalculator.initMargins(tempRect1, header)

        val layoutParams = firstView.layoutParams
        var leftMargin = 0
        var topMargin = 0
        if (layoutParams is ViewGroup.MarginLayoutParams) {
            leftMargin = layoutParams.leftMargin
            topMargin = layoutParams.topMargin
        }

        if (orientation == LinearLayout.VERTICAL) {
            translationX = firstView.left - leftMargin + tempRect1.left
            translationY = Math.max(
                firstView.top - topMargin - header.height - tempRect1.bottom,
                getListTop(recyclerView) + tempRect1.top
            )
        } else {
            translationY = firstView.top - topMargin - tempRect1.top
            translationX = Math.max(
                firstView.left - leftMargin - header.width - tempRect1.right,
                getListLeft(recyclerView) + tempRect1.left
            )
        }

        headerMargins.set(translationX, translationY, translationX + header.width, translationY + header.height)
    }

    private fun isStickyHeaderBeingPushedOffscreen(recyclerView: RecyclerView, header: View): Boolean {
        val viewAfterHeader = getFirstViewUnobscuredByHeader(recyclerView, header) ?: return false
        val firstViewUnderHeaderPosition = recyclerView.getChildAdapterPosition(viewAfterHeader)
        if (firstViewUnderHeaderPosition == RecyclerView.NO_POSITION) {
            return false
        }

        val reverseLayout = orientationProvider.isReverseLayout(recyclerView)
        if (firstViewUnderHeaderPosition > 0 && hasNewHeader(firstViewUnderHeaderPosition, reverseLayout)) {
            val nextHeader = headerProvider.getHeader(recyclerView, firstViewUnderHeaderPosition)
            dimensionCalculator.initMargins(tempRect1, nextHeader)
            dimensionCalculator.initMargins(tempRect2, header)

            if (orientationProvider.getOrientation(recyclerView) == LinearLayout.VERTICAL) {
                val topOfNextHeader = viewAfterHeader.top - tempRect1.bottom - nextHeader.height - tempRect1.top
                val bottomOfThisHeader = recyclerView.paddingTop + header.bottom + tempRect2.top + tempRect2.bottom
                if (topOfNextHeader < bottomOfThisHeader) {
                    return true
                }
            } else {
                val leftOfNextHeader = viewAfterHeader.left - tempRect1.right - nextHeader.width - tempRect1.left
                val rightOfThisHeader = recyclerView.paddingLeft + header.right + tempRect2.left + tempRect2.right
                if (leftOfNextHeader < rightOfThisHeader) {
                    return true
                }
            }
        }

        return false
    }

    private fun translateHeaderWithNextHeader(
        recyclerView: RecyclerView,
        orientation: Int,
        translation: Rect,
        currentHeader: View,
        viewAfterNextHeader: View,
        nextHeader: View
    ) {
        dimensionCalculator.initMargins(tempRect1, nextHeader)
        dimensionCalculator.initMargins(tempRect2, currentHeader)

        if (orientation == LinearLayout.VERTICAL) {
            val topOfStickyHeader = getListTop(recyclerView) + tempRect2.top + tempRect2.bottom
            val shiftFromNextHeader =
                viewAfterNextHeader.top - nextHeader.height - tempRect1.bottom - tempRect1.top - currentHeader.height - topOfStickyHeader
            if (shiftFromNextHeader < topOfStickyHeader) {
                translation.top += shiftFromNextHeader
            }
        } else {
            val leftOfStickyHeader = getListLeft(recyclerView) + tempRect2.left + tempRect2.right
            val shiftFromNextHeader =
                viewAfterNextHeader.left - nextHeader.width - tempRect1.right - tempRect1.left - currentHeader.width - leftOfStickyHeader
            if (shiftFromNextHeader < leftOfStickyHeader) {
                translation.left += shiftFromNextHeader
            }
        }
    }

    private fun getFirstViewUnobscuredByHeader(recyclerView: RecyclerView, firstView: View): View? {
        val reverseLayout = orientationProvider.isReverseLayout(recyclerView)
        val step = if (reverseLayout) -1 else 1
        val from = if (reverseLayout) recyclerView.childCount - 1 else 0
        for (i in from..recyclerView.childCount step step) {
            val child = recyclerView.getChildAt(i)
            if (!itemIsObscuredByHeader(
                    recyclerView,
                    child,
                    firstView,
                    orientationProvider.getOrientation(recyclerView)
                )
            ) {
                return child
            }
        }
        return null
    }

    private fun itemIsObscuredByHeader(
        recyclerView: RecyclerView,
        itemView: View,
        headerView: View,
        orientation: Int
    ): Boolean {
        val layoutParams = itemView.layoutParams as RecyclerView.LayoutParams
        dimensionCalculator.initMargins(tempRect1, headerView)

        val adapterPosition = recyclerView.getChildAdapterPosition(itemView)
        if (adapterPosition == RecyclerView.NO_POSITION ||
            headerProvider.getHeader(recyclerView, adapterPosition) != headerView
        ) {
            return false
        }

        if (orientation == LinearLayout.VERTICAL) {
            val itemTop = itemView.top - layoutParams.topMargin
            val headerBottom = getListTop(recyclerView) + headerView.bottom + tempRect1.bottom + tempRect1.top
            if (itemTop >= headerBottom) {
                return false
            }
        } else {
            val itemLeft = itemView.left - layoutParams.leftMargin
            val headerRight = getListLeft(recyclerView) + headerView.right + tempRect1.right + tempRect1.left
            if (itemLeft >= headerRight) {
                return false
            }
        }
        return true
    }

    private fun getListTop(recyclerView: RecyclerView): Int {
        return if (recyclerView.layoutManager?.clipToPadding == true) {
            recyclerView.paddingTop
        } else {
            0
        }
    }

    private fun getListLeft(recyclerView: RecyclerView): Int {
        return if (recyclerView.layoutManager?.clipToPadding == true) {
            recyclerView.paddingLeft
        } else {
            0
        }
    }
}
