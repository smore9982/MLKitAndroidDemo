package com.more.automldemo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

class TrackingView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttrs: Int = 0) : View(context, attrs, defStyleAttrs) {

    private val boundingBoxes = ArrayList<Rect>()
    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        paint.style = Paint.Style.STROKE
        paint.color = Color.parseColor("#0000ff")
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        boundingBoxes.forEach {
            canvas?.drawRect(it,paint)
        }
    }

    fun updateBoundingBoxes(newBoxes : List<Rect>) {
        boundingBoxes.clear()
        boundingBoxes.addAll(newBoxes)
        postInvalidate()
    }
}