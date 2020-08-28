package com.anwesh.uiprojects.faceeyeprogressview

/**
 * Created by anweshmishra on 29/08/20.
 */

import android.view.View
import android.graphics.Paint
import android.view.MotionEvent
import android.graphics.Color
import android.graphics.Canvas
import android.graphics.RectF
import android.app.Activity
import android.content.Context

val colors : Array<Int> = arrayOf(
        "#F44336",
        "#009688",
        "#3F51B5",
        "#4CAF50",
        "#2196F3"
        ).map {Color.parseColor(it)}
        .toTypedArray()
val parts : Int = 2
val scGap : Float = 0.02f / (parts + 1)
val strokeFactor : Float = 90f
val rFactor : Float = 3.8f
val concFactor : Float = 2.8f
val eyeFactor : Float = 8.9f
val backColor : Int = Color.parseColor("#BDBDBD")
val delay : Long = 20
val rot : Float = 360f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n))
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawFaceEyeProgress(scale : Float, w : Float, h : Float, paint : Paint) {
    val sf : Float = scale.sinify()
    val sf1 = sf.divideScale(0, parts + 1)
    val sf2 : Float = sf.divideScale(1, parts + 1)
    val sc1 : Float = sf2.divideScale(0, 2)
    val sc2 : Float = sf2.divideScale(1, 2)
    val eyeR : Float = Math.min(w, h) / eyeFactor
    val faceR : Float = Math.min(w, h) / rFactor
    val concR : Float = Math.min(w, h) / concFactor

    save()
    translate(w / 2, h / 2)
    save()
    rotate(rot * sf2)
    paint.style = Paint.Style.FILL
    for (j in 0..1) {
        drawCircle(-2 * concR * (1f - 2 * j), -eyeR / 2, eyeR * sf1, paint)
    }
    paint.style = Paint.Style.STROKE
    drawArc(RectF(-faceR, -faceR, faceR, faceR), 0f, 360f * sf1, false, paint)
    restore()
    drawArc(RectF(-concR, -concR, concR, concR), 360f * sc2, 360f * sc1, false, paint)
    restore()
}

fun Canvas.drawFEPNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawFaceEyeProgress(scale, w, h, paint)
}

class FaceEyeProgressView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}