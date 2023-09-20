package com.example.catchmind.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var drawPath: Path = Path()
    private var drawPaint: Paint = Paint()
    private var canvasBitmap: Bitmap? = null
    private var drawCanvas: Canvas? = null

    init {
        setupDrawing()
    }

    // DrawingView를 활성화 또는 비활성화하는 플래그
    private var isDrawingEnabled = true

    // DrawingView를 비활성화하거나 활성화하는 메서드
    fun setDrawingEnabled(enabled: Boolean) {
        isDrawingEnabled = enabled
    }

    // 터치 이벤트를 처리하는 메서드

     fun setColor(color: Int){
        drawPaint.color = color
    }

    private fun setupDrawing() {
        drawPaint.color = Color.BLACK
        drawPaint.isAntiAlias = true
        drawPaint.strokeWidth = 5f
        drawPaint.style = Paint.Style.STROKE
        drawPaint.strokeJoin = Paint.Join.ROUND
        drawPaint.strokeCap = Paint.Cap.ROUND
    }

    fun setDrawing() {

        if (canvasBitmap == null) {
            canvasBitmap = Bitmap.createBitmap(getDeviceWidth(context), getDeviceHeight(context), Bitmap.Config.ARGB_8888)
            drawCanvas = Canvas(canvasBitmap!!)
        }

        drawPath.reset()

        if (canvasBitmap != null) {
            canvasBitmap!!.eraseColor(Color.TRANSPARENT)
            val options = BitmapFactory.Options()
            options.inMutable = true
            drawCanvas = Canvas(canvasBitmap!!)
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawBitmap(canvasBitmap!!, 0f, 0f, drawPaint)
        canvas?.drawPath(drawPath, drawPaint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if (!isDrawingEnabled) {
            // DrawingView가 비활성화되어 있으면 터치 이벤트를 무시
            return false
        }

        val touchX = event?.x
        val touchY = event?.y

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                drawPath.moveTo(touchX!!, touchY!!)
            }
            MotionEvent.ACTION_MOVE -> {
                drawPath.lineTo(touchX!!, touchY!!)
            }
            MotionEvent.ACTION_UP -> {
                drawCanvas?.drawPath(drawPath, drawPaint)
                drawPath.reset()
            }
            else -> return false
        }

        invalidate()
        return true
    }

    fun getDeviceWidth(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        return metrics.widthPixels
    }

    fun getDeviceHeight(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        return metrics.heightPixels
    }
}