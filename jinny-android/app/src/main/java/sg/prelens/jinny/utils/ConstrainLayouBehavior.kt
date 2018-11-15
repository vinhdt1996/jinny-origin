package sg.prelens.jinny.utils

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout

class ConstrainLayouBehavior : CoordinatorLayout.Behavior<ConstraintLayout> {

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor() : super()

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: ConstraintLayout, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: ConstraintLayout, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if(dy < 0) {
            showView(child)
        }
        else if(dy > 0) {
            hideView(child)
        }
    }

    override fun layoutDependsOn(parent: CoordinatorLayout?, child: ConstraintLayout?, dependency: View?): Boolean {
        return dependency is FrameLayout
    }

    private fun hideView(view: ConstraintLayout) {
        view.animate().translationY(view.height.toFloat())
    }

    private fun showView(view: ConstraintLayout) {
        view.animate().translationY(0.0f)
    }
}