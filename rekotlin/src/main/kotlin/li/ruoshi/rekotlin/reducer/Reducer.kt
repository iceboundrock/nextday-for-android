package li.ruoshi.rekotlin.reducer

import li.ruoshi.rekotlin.action.IAction
import li.ruoshi.rekotlin.state.State

/**
 * Created by ruoshili on 3/16/16.
 */
interface Reducer<T : State> {
    fun handleAction(action: IAction, state: T?): T
}