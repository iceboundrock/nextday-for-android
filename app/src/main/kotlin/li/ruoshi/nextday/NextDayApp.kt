package li.ruoshi.nextday;

import android.app.Application;
import com.jakewharton.threetenabp.AndroidThreeTen

/**
 * Created by ruoshili on 8/1/15.
 */
class NextDayApp : Application() {
    companion object {
        private lateinit var graph: ApplicationComponent
        @JvmStatic fun getObjGraph(): ApplicationComponent = graph
    }
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        graph = DaggerApplicationComponent.builder().appModule(AppModule()).build()
    }
}
