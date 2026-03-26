package com.sohaib.qibla.compass.sensor.accelerometer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.PointF
import androidx.core.content.ContextCompat
import com.sohaib.qibla.compass.R

class AccelerometerCustomDrawer(context: Context, private val isSimple: Boolean) : ViewDrawer<PointF?> {

    private val pathPaint: Paint
    private val ballPaint: Paint
    private val innerCirclePaint: Paint
    private var path: Path? = null
    private val center: Point
    private var xPos = 0f
    private var radius = 0

    var yPos = 0f

    override fun layout(width: Int, height: Int) {
        radius = width / 12
        center[width / 2] = height / 2

        if (path == null) {
            val radius = width / 2f - width * 0.03f
            path = Path()
            path!!.moveTo(center.x - radius, center.y.toFloat())
            //path!!.lineTo(center.x + radius, center.y.toFloat())
            path!!.moveTo(center.x.toFloat(), center.y - radius)
            //path!!.lineTo(center.x.toFloat(), center.y + radius)


            if (!isSimple) {
                path!!.addCircle(center.x.toFloat(), center.y.toFloat(), radius, Path.Direction.CCW)
            }
        }
    }

    override fun draw(canvas: Canvas?) {
        //Draw grid
        canvas!!.drawPath(path!!, pathPaint)
        //Draw ball
        canvas.drawCircle(center.x - xPos, center.y + yPos, radius.toFloat(), ballPaint)
        canvas.drawCircle(center.x.toFloat(), center.y.toFloat(), radius.toFloat(), innerCirclePaint)
    }

    override fun update(value: PointF?) {
        xPos = value!!.x
        yPos = value.y
    }

    init {
        val gridColor = ContextCompat.getColor(context, R.color.colorNeutral500)
        val ballColor: Int = ContextCompat.getColor(context, R.color.colorPrimary)

        pathPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        pathPaint.strokeWidth = 1f
        pathPaint.style = Paint.Style.STROKE
        pathPaint.color = gridColor

        ballPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        ballPaint.style = Paint.Style.FILL
        ballPaint.color = ballColor
        ballPaint.alpha = Color.alpha(R.color.colorNeutral400)
        center = Point(0, 0)

        innerCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        innerCirclePaint.style = Paint.Style.STROKE
        innerCirclePaint.color = gridColor
        innerCirclePaint.alpha = Color.alpha(R.color.colorNeutral400)
    }
}