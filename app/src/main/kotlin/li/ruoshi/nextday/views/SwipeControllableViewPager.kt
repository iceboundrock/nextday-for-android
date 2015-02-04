package li.ruoshi.nextday.views

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent


class SwipeControllableViewPager @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : ViewPager(context, attrs) {
    private var swipeEnabled: Boolean = false

    init {
        this.swipeEnabled = true
    }

    fun setSwipeEnabled(enabled: Boolean) {
        this.swipeEnabled = enabled
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        try {
            return this.swipeEnabled && super.onInterceptTouchEvent(event)
        } catch (t: Throwable) {
            return false
        }

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return this.swipeEnabled && super.onTouchEvent(event)
    }
}