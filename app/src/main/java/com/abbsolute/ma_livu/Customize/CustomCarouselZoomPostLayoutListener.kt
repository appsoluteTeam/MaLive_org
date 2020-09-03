package com.example.sampletest

import android.view.View
import com.azoft.carousellayoutmanager.CarouselLayoutManager
import com.azoft.carousellayoutmanager.ItemTransformation

class CustomCarouselZoomPostLayoutListener : CarouselLayoutManager.PostLayoutListener {
    override fun transformChild(child: View, itemPositionToCenterDiff: Float, orientation: Int): ItemTransformation {
        val scale = (2 * (2 * -StrictMath.atan(Math.abs(itemPositionToCenterDiff) + 1.0) / Math.PI + 1)).toFloat()
        // because scaling will make view smaller in its center, then we should move this item to the top or bottom to make it visible
        val translateY: Float
        val translateX: Float
        val ratio = 1.0f // Change this ratio to get your customized spacing
        if (CarouselLayoutManager.VERTICAL == orientation) {
            val translateYGeneral = child.measuredHeight * (1 - scale) * ratio
            translateY = Math.signum(itemPositionToCenterDiff) * translateYGeneral
            translateX = 0f
        } else {
            val translateXGeneral = child.measuredWidth * (1 - scale) * ratio
            translateX = Math.signum(itemPositionToCenterDiff) * translateXGeneral
            translateY = 0f
        }

        return ItemTransformation(scale, scale, translateX, translateY)
    }
}