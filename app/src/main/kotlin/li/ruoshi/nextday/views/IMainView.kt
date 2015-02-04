package li.ruoshi.nextday.views

import android.content.Context
import li.ruoshi.nextday.models.DailyInfo

/**
 * Created by ruoshili on 1/5/16.
 */
interface IMainView {
    fun setDailyInfo(list: List<DailyInfo>)
    fun getContext(): Context
}