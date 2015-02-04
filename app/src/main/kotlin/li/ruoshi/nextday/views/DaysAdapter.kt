package li.ruoshi.nextday.views

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import li.ruoshi.nextday.R
import li.ruoshi.nextday.models.DailyInfo
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.ArrayBlockingQueue

/**
 * Created by ruoshili on 1/10/16.
 */
class DaysAdapter(val context: Context) : PagerAdapter() {
    companion object {
        const val TAG = "DaysAdapter"
    }

    private val mDestroyedViewHolders: Queue<WeakReference<DayViewHolder>> = ArrayBlockingQueue(8)

    private val mViewHoldersCache: MutableList<DayViewHolder> = ArrayList(8)

    override fun isViewFromObject(view: View?, obj: Any?): Boolean {
        return (obj is DayViewHolder) && view == obj.view
    }


    override fun destroyItem(container: ViewGroup?, position: Int, obj: Any?) {
        Log.d(TAG, "destroyItem, pos: $position, obj: ${obj?.javaClass}")
        if (obj is DayViewHolder) {
            container?.removeView(obj.view)
            mDestroyedViewHolders.offer(WeakReference(obj))
        }
        Log.d(TAG, "destroyItem, end, mDestroyedViewHolders size: ${mDestroyedViewHolders.size}")
    }

    private var createViewTimes = 0
    private fun createViewHolder(container: ViewGroup?): DayViewHolder {
        Log.d(TAG, "createViewHolder, times: ${++createViewTimes}")
        val inflater = LayoutInflater.from(context);
        val layout = inflater.inflate(R.layout.day_view, container, false);
        return DayViewHolder(context, layout)
    }

    override fun instantiateItem(container: ViewGroup?, position: Int): Any? {
        Log.d(TAG, "instantiateItem, pos: $position")

        if (position < 3) {
            // TODO: 加载
        }

        var viewHolder: DayViewHolder? = null
        while (!mDestroyedViewHolders.isEmpty()) {
            val r = mDestroyedViewHolders.poll()
            if (r.get() == null) {
                Log.d(TAG, "weak ref has no value")
                continue
            } else {
                viewHolder = r.get()
                break
            }
        }

        if (viewHolder == null) {
            Log.d(TAG, "mDestroyedViewHolders is empty, create new view holder")
            viewHolder = createViewHolder(container)
        }

        viewHolder.setData(list!![position])

        container?.addView(viewHolder.view)

        updateCache(viewHolder)

        return viewHolder
    }

    private fun updateCache(viewHolder: DayViewHolder) {
        if (mViewHoldersCache.size > 0) {
            for (i in 0..(mViewHoldersCache.size - 1)) {
                if (mViewHoldersCache[i] == viewHolder) {
                    return
                }
            }
        }
        mViewHoldersCache.add(viewHolder)
        Log.d(TAG, "added a new view holder to cache, current cache size: ${mViewHoldersCache.size}")
    }

    fun getViewHolderAt(pos: Int): DayViewHolder? {
        if (pos < 0 || pos >= count) {
            return null
        }

        val data = list!![pos]

        return mViewHoldersCache.filter {
            it.getData() == data
        }.firstOrNull()
    }

    override fun getCount(): Int {
        if (list == null) {
            return 0
        }

        return list!!.count()
    }

    var list: List<DailyInfo>? = null

    fun setDailyInfo(list: List<DailyInfo>) {
        this.list = list
        notifyDataSetChanged()
    }
}