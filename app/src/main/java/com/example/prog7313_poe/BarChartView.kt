package com.example.prog7313_poe

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class BarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var data = mutableListOf<BarData>()
    private var minGoal = 0f
    private var maxGoal = 0f
    private var maxValue = 0f

    data class BarData(
        val label: String,
        val value: Float
    )

    fun setData(newData: List<BarData>, minGoal: Float, maxGoal: Float) {
        this.data = newData.toMutableList()
        this.minGoal = minGoal
        this.maxGoal = maxGoal
        this.maxValue = if (data.isEmpty()) 1f else data.maxOf { it.value }.coerceAtLeast(maxGoal)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (data.isEmpty()) {
            drawNoDataMessage(canvas)
            return
        }
        val width = width.toFloat()
        val height = height.toFloat()
        val leftMargin = 60f
        val rightMargin = 40f
        val topMargin = 60f
        val bottomMargin = 80f
        val chartWidth = width - leftMargin - rightMargin
        val chartHeight = height - topMargin - bottomMargin

        // Y-axis
        linePaint.color = Color.BLACK
        linePaint.strokeWidth = 2f
        canvas.drawLine(leftMargin, topMargin, leftMargin, height - bottomMargin, linePaint)

        // X-axis
        canvas.drawLine(leftMargin, height - bottomMargin, width - rightMargin, height - bottomMargin, linePaint)

        // Y-axis labels and grid lines
        textPaint.color = Color.BLACK
        textPaint.textSize = 12f
        val numYLines = 5
        for (i in 0..numYLines) {
            val yValue = (maxValue / numYLines) * i
            val y = height - bottomMargin - (chartHeight * (yValue / maxValue))
            val label = String.format("%.0f", yValue)
            val labelWidth = textPaint.measureText(label)
            canvas.drawText(label, leftMargin - labelWidth - 4f, y + 4, textPaint)
            linePaint.color = Color.LTGRAY
            linePaint.strokeWidth = 1f
            canvas.drawLine(leftMargin, y, width - rightMargin, y, linePaint)
        }

        // Draw bars
        val barWidth = (chartWidth / data.size) * 0.7f
        val barSpacing = (chartWidth / data.size) * 0.3f
        var x = leftMargin + barSpacing / 2
        paint.color = ContextCompat.getColor(context, R.color.custom_blue)
        for (i in data.indices) {
            val barHeight = (chartHeight * (data[i].value / maxValue))
            val barTop = height - bottomMargin - barHeight
            canvas.drawRect(x, barTop, x + barWidth, height - bottomMargin, paint)
            textPaint.color = Color.BLACK
            textPaint.textSize = 10f
            val label = data[i].label.take(10)
            val labelWidth = textPaint.measureText(label)
            canvas.drawText(label, x + barWidth / 2 - labelWidth / 2, height - bottomMargin + 20, textPaint)
            x += barWidth + barSpacing
        }

        // Dashed lines for min and max goals
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeWidth = 2f
        if (minGoal > 0 && minGoal <= maxValue) {
            val yMin = height - bottomMargin - (chartHeight * (minGoal / maxValue))
            linePaint.color = ContextCompat.getColor(context, R.color.income_green)
            linePaint.pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
            canvas.drawLine(leftMargin, yMin, width - rightMargin, yMin, linePaint)
            textPaint.color = ContextCompat.getColor(context, R.color.income_green)
            canvas.drawText("Min", leftMargin + 4, yMin - 4, textPaint)
        }
        if (maxGoal > 0 && maxGoal <= maxValue) {
            val yMax = height - bottomMargin - (chartHeight * (maxGoal / maxValue))
            linePaint.color = ContextCompat.getColor(context, R.color.expense_red)
            linePaint.pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
            canvas.drawLine(leftMargin, yMax, width - rightMargin, yMax, linePaint)
            textPaint.color = ContextCompat.getColor(context, R.color.expense_red)
            canvas.drawText("Max", leftMargin + 4, yMax - 4, textPaint)
        }
        linePaint.pathEffect = null
        linePaint.style = Paint.Style.FILL_AND_STROKE
    }

    private fun drawNoDataMessage(canvas: Canvas) {
        textPaint.color = Color.GRAY
        textPaint.textSize = 16f
        val msg = "No expense data"
        val msgWidth = textPaint.measureText(msg)
        canvas.drawText(msg, width / 2 - msgWidth / 2, (height / 2).toFloat(), textPaint)
    }
}