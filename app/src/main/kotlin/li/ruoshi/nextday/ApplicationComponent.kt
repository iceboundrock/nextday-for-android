package li.ruoshi.nextday

import dagger.Component
import li.ruoshi.nextday.views.DayViewHolder
import javax.inject.Singleton

/**
 * Created by ruoshili on 2/22/16.
 */

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface ApplicationComponent {
    fun inject(vh: DayViewHolder)
}