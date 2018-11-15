package sg.prelens.jinny.widget

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

/* *
 *  Created by JAY on 14/06/2018
 */
 
 class CustomViewPager: ViewPager{
     constructor(context: Context) : super(context)
     constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

     override fun onTouchEvent(ev: MotionEvent?): Boolean {
         try {
             return super.onTouchEvent(ev)
         } catch (ex: IllegalArgumentException) {
             ex.printStackTrace()
         }
         return false
     }

     override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
         try {
             return super.onInterceptTouchEvent(ev)
         } catch (ex: IllegalArgumentException) {
             ex.printStackTrace()
         }
         return false
     }
 }