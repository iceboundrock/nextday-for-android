package li.ruoshi.nextday.models

import android.view.View

/**
 * Created by ruoshili on 1/26/16.
 */
class DayViewTextsVisibilityChangedEvent(val visibility: Int) {

    companion object {
        val Visible = DayViewTextsVisibilityChangedEvent(View.VISIBLE)
        val Invisible = DayViewTextsVisibilityChangedEvent(View.INVISIBLE)
        val Gone = DayViewTextsVisibilityChangedEvent(View.GONE)
    }

}