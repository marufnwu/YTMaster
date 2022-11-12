package com.sikderithub.ytmaster.layout_manager

import android.content.Context
import android.graphics.PointF
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView


class SmoothScrollingLayoutManager(context: Context?, orientation: Int, reverseLayout: Boolean) : LinearLayoutManager(context, orientation, reverseLayout) {

    private val MILLISECONDS_PER_INCH = 200f

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
    }

    override fun smoothScrollToPosition(recyclerView: RecyclerView?, state: RecyclerView.State?, position: Int) {

        val linearSmoothScroller: LinearSmoothScroller = object : LinearSmoothScroller(
            recyclerView!!.context
        ) {
            override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
                return super.computeScrollVectorForPosition(targetPosition)
            }

            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi
            }
        }

        linearSmoothScroller.targetPosition = position
        startSmoothScroll(linearSmoothScroller)


    }
}