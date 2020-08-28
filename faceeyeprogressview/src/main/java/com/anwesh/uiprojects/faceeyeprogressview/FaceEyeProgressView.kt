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

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n))
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()
