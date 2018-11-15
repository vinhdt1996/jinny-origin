package sg.prelens.jinny.repositories

import android.arch.lifecycle.MutableLiveData
import sg.prelens.jinny.models.Merchant

/**
 * Created by tommy on 3/16/18.
 */
interface MerchantRepository {
    fun error(): MutableLiveData<Throwable>
}