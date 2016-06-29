package li.ruoshi.nextday.views

import android.content.Context
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.View
import android.view.ViewManager
import li.ruoshi.nextday.R
import li.ruoshi.nextday.models.DailyInfo
import li.ruoshi.nextday.models.DayViewTextsVisibilityChangedEvent
import li.ruoshi.nextday.presenter.MainPresenter
import li.ruoshi.nextday.utils.RxBus
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.matchParent

/**
 * Created by ruoshili on 1/5/16.
 */
class MainActivity : BaseActivity(), IMainView {
    companion object {
        const val TAG = "MainActivity"
    }

    override fun getContext(): Context {
        return this
    }

    override fun setDailyInfo(list: List<DailyInfo>) {
        daysAdapter?.setDailyInfo(list)
        daysViewPager?.setCurrentItem(
                if (mPresenter.currentPos < 0) list.size - 1 else mPresenter.currentPos,
                false)
    }

    var daysViewPager: SwipeControllableViewPager? = null
    var daysAdapter: DaysAdapter? = null
    private val mPresenter = MainPresenter(this)

    inline fun ViewManager.swipeControllableViewPager() = swipeControllableViewPager {}
    inline fun ViewManager.swipeControllableViewPager(init: SwipeControllableViewPager.() -> Unit) = ankoView({ SwipeControllableViewPager(it) }, 0, init)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        frameLayout {
            daysViewPager = swipeControllableViewPager() {
                id = R.id.day_fragments_view_pager
            }.lparams(width = matchParent, height = matchParent)

        }

        daysAdapter = DaysAdapter(this)
        daysViewPager?.adapter = daysAdapter

        daysViewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                Log.d(TAG, "onPageScrolled, pos: $position, positionOffset: $positionOffset, positionOffsetPixels: $positionOffsetPixels, current pos: ${mPresenter.currentPos}")

                if (positionOffsetPixels == 0) {
                    val vh = daysAdapter!!.getViewHolderAt(position)
                    vh?.onShow()
                }
            }

            private var currentState: Int = ViewPager.SCROLL_STATE_IDLE

            override fun onPageScrollStateChanged(state: Int) {
                Log.d(TAG, "onPageScrollStateChanged, state: $state, current pos: ${mPresenter.currentPos}")

                if (currentState != state && state == ViewPager.SCROLL_STATE_DRAGGING) {
                    val vh = daysAdapter!!.getViewHolderAt(mPresenter.currentPos)
                    vh?.onHide(false)
                }
                currentState = state
            }

            override fun onPageSelected(position: Int) {
                Log.d(TAG, "onPageSelected, new position: $position, current pos: ${mPresenter.currentPos}")

                mPresenter.currentPos = position
            }

        })

        RxBus.default
                .register(DayViewTextsVisibilityChangedEvent::class.java, this)
                .subscribe({
                    daysViewPager?.setSwipeEnabled(it.visibility == View.VISIBLE)
                }, {
                    Log.e(TAG, "", it)
                })

    }

    override fun onPause() {
        super.onPause()
        mPresenter.pause()
    }

    override fun onResume() {
        super.onResume()
        mPresenter.resume()
    }
}
