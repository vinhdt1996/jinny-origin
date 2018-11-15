package sg.prelens.jinny.utils

import android.arch.lifecycle.LiveData
import java.util.concurrent.Executor

class ExecutorLiveData<T>(private val executor: Executor, var run: (()-> Unit?) -> T) : LiveData<T>(){

    override fun onActive() {
        super.onActive()
        executor.execute {
            postValue(run.invoke {

            })
        }
    }
}