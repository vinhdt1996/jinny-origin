package sg.prelens.jinny.widget

import android.content.Context
import android.support.v4.view.NestedScrollingChild
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Created by tommy on 3/14/18.
 */
class NonSwipeableViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs), NestedScrollingChild {
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean = false
    override fun onTouchEvent(ev: MotionEvent?): Boolean = true
    override fun isNestedScrollingEnabled(): Boolean = false
    override fun requestChildFocus(child: View?, focused: View?) {
    }

    override fun addOnPageChangeListener(listener: OnPageChangeListener) {
        super.addOnPageChangeListener(listener)
    }
}