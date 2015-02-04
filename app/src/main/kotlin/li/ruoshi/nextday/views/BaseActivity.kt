package li.ruoshi.nextday.views

import android.os.Bundle
import com.crashlytics.android.Crashlytics
import com.trello.rxlifecycle.components.RxActivity
import io.fabric.sdk.android.Fabric

/**
 * Created by ruoshili on 1/8/16.
 */
open class BaseActivity : RxActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
    }
}