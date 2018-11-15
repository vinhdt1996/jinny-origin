package sg.prelens.jinny.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.ImageView
import android.graphics.Path

/**
 * Author : BIMBIM<br>.
 * Create Date : 3/21/18<br>.
 */
class RoundCornerImageView : ImageView {
    private var radius: Float = 5f
    private var path: Path? = null
    private var rect: RectF? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    private fun init() {
        path = android.graphics.Path()
    }

    override fun onDraw(canvas: Canvas?) {
        rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
        path?.addRoundRect(rect, radius, radius, Path.Direction.CCW)
        canvas?.clipPath(path)
        super.onDraw(canvas)
    }
}