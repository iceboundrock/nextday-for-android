package li.ruoshi.rekotlin

import li.ruoshi.rekotlin.action.IAction
import li.ruoshi.rekotlin.state.State
import rx.Observable

/**
 * Created by ruoshili on 3/16/16.
 */
interface IStore<T : State> {
    fun getDispatchFunction(): (IAction) -> Any

    fun getStateObservable(): Observable<T>

    fun dispatch(action: IAction): Any


}