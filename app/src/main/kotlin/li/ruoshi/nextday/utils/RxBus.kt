package li.ruoshi.nextday.utils

import android.util.Log
import android.view.View
import com.jakewharton.rxbinding.view.RxView
import com.trello.rxlifecycle.ActivityEvent
import com.trello.rxlifecycle.FragmentEvent
import com.trello.rxlifecycle.RxLifecycle
import com.trello.rxlifecycle.components.RxActivity
import com.trello.rxlifecycle.components.support.RxFragmentActivity
import rx.Observable
import rx.subjects.PublishSubject
import rx.subjects.SerializedSubject
import java.util.concurrent.TimeUnit

/**
 * Created by ruoshili on 1/26/16.
 * 基于Rx的事件总线
 * 内置了一个默认实例便于全局使用，也可通过create创建新实例在自定义范围内使用
 *
 */
class RxBus private constructor() {
    private val mSubject: SerializedSubject<Any, Any>

    init {
        mSubject = SerializedSubject(PublishSubject.create<Any>())
    }

    /**
     * 向总线填入一个事件对象

     * @param event
     */
    fun post(event: Any) {
        mSubject.onNext(event)
    }

    /**
     * 向总线填入一个事件对象，延迟发送
     * @param event
     * @param milliSecs 延迟毫秒时间
     */
    fun postDelay(event: Any, milliSecs: Long) {
        Observable.timer(milliSecs, TimeUnit.MILLISECONDS).subscribe({
            mSubject.onNext(event)
        }, {
            Log.e(TAG, "Post Delay failed.", it)
        })
    }

    /**
     * 过滤事件类型，获得一个可订阅的事件源
     * 注意：订阅该事件源后必须自行调用 unsubscribe 进行释放，避免内存泄露

     * @param cls 要过滤的事件对象类型
     * *
     * @return 事件源Observable
     */
    fun <T> register(cls: Class<T>): Observable<T> {
        return mSubject.onBackpressureBuffer(100) { Log.e(TAG, "Back pressure Buffer Overflow.") }.filter { o -> cls.isInstance(o) }.cast(cls)
    }

    /**
     * 过滤事件类型，获得一个可订阅的事件源
     * 使用该函数可以不用调用者管理 Subscription 的退订，在 activity onDestroy 时将自动销毁。

     * @param cls             要过滤的事件载体类型
     * *
     * @param lifecycleObject 绑定订阅的生命周期到一个支持生命周期的对象上,这个对象的类型可以是:
     * *                        RxActivity/RxFragmentActivity/RxFragment/RxDialogFragment/View
     * *
     * @return 事件源Observable
     */
    fun <T> register(cls: Class<T>, lifecycleObject: Any): Observable<T> {
        Log.v(TAG, "Register for class: " + cls.name + ", lifecycleObject type: " + lifecycleObject.javaClass.name)

        return when (lifecycleObject) {
            is RxActivity -> registerOnActivity(cls, lifecycleObject)
            is RxFragmentActivity -> registerOnActivity(cls, lifecycleObject)
            is com.trello.rxlifecycle.components.support.RxFragment -> registerOnFragment(cls, lifecycleObject)
            is com.trello.rxlifecycle.components.RxDialogFragment -> registerOnDialogFragment(cls, lifecycleObject)
            is com.trello.rxlifecycle.components.support.RxDialogFragment -> registerOnDialogFragment(cls, lifecycleObject)
            is View -> registerOnView(cls, lifecycleObject)
            else -> {
                Log.w(TAG, "Type of lifecycleObject is: ["
                        + lifecycleObject.javaClass.name
                        + "], which is not supported. You should un-subscribe from the returned Observable object yourself.")

                throw IllegalArgumentException("lifecycleObject is not supported.")
            }
        }
    }

    /**
     * 过滤事件类型，获得一个可订阅的事件源
     * 使用该函数可以不用调用者管理 Subscription 的退订，在 activity onDestroy 时将自动销毁。

     * @param cls      要过滤的事件载体类型
     * *
     * @param activity 绑定订阅的生命周期到一个 activity 上
     * *
     * @return 事件源Observable
     */
    fun <T> registerOnActivity(cls: Class<T>, activity: RxActivity): Observable<T> {
        val ev = RxLifecycle.bindUntilActivityEvent<T>(activity.lifecycle(), ActivityEvent.DESTROY)
        return register(cls).compose(ev)
    }

    /**
     * 过滤事件类型，获得一个可订阅的事件源
     * 使用该函数可以不用调用者管理 Subscription 的退订，在 activity onDestroy 时将自动销毁。

     * @param cls      要过滤的事件载体类型
     * *
     * @param activity 绑定订阅的生命周期到一个 activity 上
     * *
     * @return 事件源Observable
     */
    fun <T> registerOnActivity(cls: Class<T>, activity: RxFragmentActivity): Observable<T> {
        return register(cls).compose(RxLifecycle.bindUntilActivityEvent<T>(activity.lifecycle(), ActivityEvent.DESTROY))
    }

    /**
     * 过滤事件类型，获得一个可订阅的事件源
     * 使用该函数可以不用调用者管理 Subscription 的退订，在 fragment onDestroy 时将自动销毁。

     * @param cls      要过滤的事件载体类型
     * *
     * @param fragment 绑定订阅的生命周期到一个 fragment 上
     * *
     * @return 事件源Observable
     */
    fun <T> registerOnFragment(cls: Class<T>,
                               fragment: com.trello.rxlifecycle.components.support.RxFragment): Observable<T> {
        return register(cls).compose(RxLifecycle.bindUntilFragmentEvent<T>(fragment.lifecycle(), FragmentEvent.DESTROY))
    }


    /**
     * 过滤事件类型，获得一个可订阅的事件源
     * 使用该函数可以不用调用者管理 Subscription 的退订，在 fragment onDestroy 时将自动销毁。

     * @param cls      要过滤的事件载体类型
     * *
     * @param fragment 绑定订阅的生命周期到一个 fragment 上
     * *
     * @return 事件源Observable
     */
    fun <T> registerOnFragment(cls: Class<T>, fragment: com.trello.rxlifecycle.components.RxFragment): Observable<T> {
        return register(cls).compose(RxLifecycle.bindUntilFragmentEvent<T>(fragment.lifecycle(), FragmentEvent.DESTROY))
    }

    /**
     * 过滤事件类型，获得一个可订阅的事件源
     * 使用该函数可以不用调用者管理 Subscription 的退订，在 fragment onDestroy 时将自动销毁。

     * @param cls         要过滤的事件载体类型
     * *
     * @param dlgFragment 绑定订阅的生命周期到一个 DialogFragment 上
     * *
     * @return 事件源Observable
     */
    fun <T> registerOnDialogFragment(cls: Class<T>,
                                     dlgFragment: com.trello.rxlifecycle.components.RxDialogFragment): Observable<T> {
        return register(cls).compose(RxLifecycle.bindUntilFragmentEvent<T>(dlgFragment.lifecycle(), FragmentEvent.DESTROY))
    }

    fun <T> registerOnDialogFragment(cls: Class<T>,
                                     dlgFragment: com.trello.rxlifecycle.components.support.RxDialogFragment): Observable<T> {
        return register(cls).compose(RxLifecycle.bindUntilFragmentEvent<T>(dlgFragment.lifecycle(), FragmentEvent.DESTROY))
    }

    /**
     * 过滤事件类型，获得一个可订阅的事件源
     * 不同于 register，该函数可以不用调用者管理 Subscription 的退订，在view detached时将自动销毁。
     * 注意：该函数必须在UI现场调用

     * @param cls  要过滤的事件载体类型
     * *
     * @param view 绑定订阅的生命周期到一个 view 上
     * *
     * @return 事件源Observable
     */
    fun <T> registerOnView(cls: Class<T>, view: View): Observable<T> {
        return register(cls).takeUntil(RxView.detaches(view))
    }

    companion object {
        private val TAG = "RxBus"
        /**
         * 获得默认总线实例

         * @return
         */
        val default = RxBus()

        /**
         * 创建一个新总线实例

         * @return
         */
        fun create(): RxBus {
            return RxBus()
        }
    }
}
