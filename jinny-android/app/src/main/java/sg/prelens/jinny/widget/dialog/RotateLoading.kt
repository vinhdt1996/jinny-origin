package sg.prelens.jinny.widget.dialog

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.LinearInterpolator
import sg.prelens.jinny.R

/**
 * Author : BIMBIM<br>.
 * Create Date : 3/12/18<br>.
 */
class RotateLoading : View {
    companion object {
        const val DEFAULT_WIDTH = 6
        const val DEFAULT_SHADOW_POSITION = 6
        const val DEFAULT_SPEED_OF_DEGREE = 6
    }

    private var mPaint: Paint? = null
    private var loadingRectF: RectF? = null
    private var shadowRectF: RectF? = null
    private var topDegree = 10
    private var bottomDegree = 190
    private var arc: Float = 0.toFloat()
    private var mWidth: Int = 0
    private var changeBigger = true
    private var shadowPosition: Int = 0
    private var isStart = false
    private var color: Int = 0
    private var speedOfDegree: Int = 0
    private var speedOfArc: Float = 0.toFloat()

    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        color = Color.WHITE
        mWidth = dpToPx(context, DEFAULT_WIDTH.toFloat())
        shadowPosition = dpToPx(getContext(), DEFAULT_SHADOW_POSITION.toFloat())
        speedOfDegree = DEFAULT_SPEED_OF_DEGREE
        if (attrs != null) {
            var typedArray = context.obtainStyledAttributes(attrs, R.styleable.RotateLoading)
            color = typedArray.getColor(R.styleable.RotateLoading_loading_color, Color.WHITE)
            mWidth = typedArray.getDimensionPixelSize(R.styleable.RotateLoading_loading_width, dpToPx(context, DEFAULT_WIDTH.toFloat()))
            shadowPosition = typedArray.getInt(R.styleable.RotateLoading_shadow_position, DEFAULT_SHADOW_POSITION)
            speedOfDegree = typedArray.getInt(R.styleable.RotateLoading_loading_speed, DEFAULT_SPEED_OF_DEGREE)
            typedArray.recycle()
        }
        speedOfArc = (speedOfDegree / 4).toFloat()
        mPaint = Paint()
        mPaint?.setColor(color)
        mPaint?.setAntiAlias(true)
        mPaint?.setStyle(Paint.Style.STROKE)
        mPaint?.setStrokeWidth(mWidth.toFloat())
        mPaint?.setStrokeCap(Paint.Cap.ROUND)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        arc = 10f
        loadingRectF = RectF((2 * mWidth).toFloat(), (2 * mWidth).toFloat(), (w - 2 * mWidth).toFloat(), (h - 2 * mWidth).toFloat())
        shadowRectF = RectF((2 * mWidth + shadowPosition).toFloat(), (2 * mWidth + shadowPosition).toFloat(), (w - 2 * mWidth + shadowPosition).toFloat(), (h - 2 * mWidth + shadowPosition).toFloat())
    }

    fun dpToPx(context: Context, dpVal: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context.resources.displayMetrics) as Int
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (!isStart) {
            return
        }
        mPaint?.setColor(Color.parseColor("#1a000000"))
        if (mPaint != null && shadowRectF != null) {
            canvas?.drawArc(shadowRectF, topDegree.toFloat(), arc, false, mPaint)
            canvas?.drawArc(shadowRectF, bottomDegree.toFloat(), arc, false, mPaint)
        }
        mPaint?.setColor(color)
        if (mPaint != null && loadingRectF != null) {
            canvas?.drawArc(loadingRectF, topDegree.toFloat(), arc, false, mPaint)
            canvas?.drawArc(loadingRectF, bottomDegree.toFloat(), arc, false, mPaint)
        }
        topDegree += speedOfDegree
        bottomDegree += speedOfDegree
        if (topDegree > 360) {
            topDegree = topDegree - 360
        }
        if (bottomDegree > 360) {
            bottomDegree = bottomDegree - 360
        }

        if (changeBigger) {
            if (arc < 160) {
                arc += speedOfArc
                invalidate()
            }
        } else {
            if (arc > speedOfDegree) {
                arc -= 2 * speedOfArc
                invalidate()
            }
        }
        if (arc >= 160 || arc <= 10) {
            changeBigger = !changeBigger
            invalidate()
        }
    }

    fun setLoadingColor(color: Int) {
        this.color = color
    }

    fun getLoadingColor(): Int {
        return color
    }

    fun start() {
        startAnimator()
        isStart = true
        invalidate()
    }

    fun stop() {
        stopAnimator()
        invalidate()
    }

    fun isStart(): Boolean {
        return isStart
    }

    private fun startAnimator() {
        val scaleXAnimator = ObjectAnimator.ofFloat(this, "scaleX", 0.0f, 1F)
        val scaleYAnimator = ObjectAnimator.ofFloat(this, "scaleY", 0.0f, 1F)
        scaleXAnimator.setDuration(300)
        scaleXAnimator.setInterpolator(LinearInterpolator())
        scaleYAnimator.setDuration(300)
        scaleYAnimator.setInterpolator(LinearInterpolator())
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator)
        animatorSet.start()
    }

    private fun stopAnimator() {
        val scaleXAnimator = ObjectAnimator.ofFloat(this, "scaleX", 1F, 0F)
        val scaleYAnimator = ObjectAnimator.ofFloat(this, "scaleY", 1F, 0F)
        scaleXAnimator.setDuration(300)
        scaleXAnimator.setInterpolator(LinearInterpolator())
        scaleYAnimator.setDuration(300)
        scaleYAnimator.setInterpolator(LinearInterpolator())
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator)
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                isStart = false
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })
        animatorSet.start()
    }
}