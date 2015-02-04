package li.ruoshi.nextday.views

import li.ruoshi.nextday.models.DailyInfo

/**
 * Created by ruoshili on 1/9/16.
 */
interface IDayView {
    fun setData(dailyInfo: DailyInfo)
}