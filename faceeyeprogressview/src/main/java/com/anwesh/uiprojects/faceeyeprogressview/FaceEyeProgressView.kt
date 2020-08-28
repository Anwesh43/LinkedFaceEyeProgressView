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

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += dir * scGap
            if (Math.abs(scale - prevScale) > 1) {
                scale += prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (!animated) {
                animated = true
            }
        }
    }

    data class FEPNode(var i : Int, val state : State = State()) {

        private var next : FEPNode? = null
        private var prev : FEPNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = FEPNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawFEPNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : FEPNode {
            var curr : FEPNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class FaceEyeProgress(var i : Int) {

        private var curr : FEPNode = FEPNode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : FaceEyeProgressView) {

        private val animator = Animator(view)
        private val fpe : FaceEyeProgress = FaceEyeProgress(0)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            fpe.draw(canvas, paint)
            animator.animate {
                fpe.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            fpe.startUpdating {
                animator.start()
            }
        }
    }
}