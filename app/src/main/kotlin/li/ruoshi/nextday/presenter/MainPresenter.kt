package li.ruoshi.nextday.presenter

import android.os.Handler
import android.os.Looper
import android.util.Log
import li.ruoshi.nextday.models.DailyInfoRepository
import li.ruoshi.nextday.views.IMainView
import org.threeten.bp.LocalDate
import rx.android.schedulers.AndroidSchedulers

/**
 * Created by ruoshili on 1/5/16.
 */
class MainPresenter(val view: IMainView) {
    companion object {
        const val TAG = "MainPresenter"
    }

    val repository: DailyInfoRepository
    val handler = Handler(Looper.getMainLooper())
    var currentPos = -1

    init {
        repository = DailyInfoRepository(view.getContext())
    }

    fun pause() {

    }

    fun resume() {
        if (currentPos == -1) {
            repository.loadDaysAsync(LocalDate.now().minusDays(10), LocalDate.now())
                    .retry(3L)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        view.setDailyInfo(it)
                    }, {
                        Log.e(TAG, "load days failed.", it)
                    })
        }
    }
}