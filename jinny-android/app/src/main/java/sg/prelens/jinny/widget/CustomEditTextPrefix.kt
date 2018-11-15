package sg.prelens.jinny.widget

import android.content.Context
import android.graphics.Canvas
import android.support.v7.widget.AppCompatEditText
import android.util.AttributeSet

class CustomEditTextPrefix : AppCompatEditText {

    private var mOriginalLeftPadding: Float = (-1).toFloat()

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        calculatePrefix()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val prefix = getTag()
        canvas?.drawText(prefix.toString(), mOriginalLeftPadding,
                getLineBounds(0, null).toFloat(), getPaint());
    }

    private fun calculatePrefix() {
        if (mOriginalLeftPadding == (-1).toFloat()) {
            val prefix = tag as String
            val widths = FloatArray(prefix.length)
            paint.getTextWidths(prefix, widths)
            var textWidth = 0f
            for (w in widths) {
                textWidth += w
            }
            mOriginalLeftPadding = compoundPaddingLeft.toFloat()
            setPadding((textWidth + mOriginalLeftPadding).toInt(),
                    paddingRight, paddingTop,
                    paddingBottom)
        }
    }
}