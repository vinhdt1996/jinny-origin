package sg.prelens.jinny.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import sg.prelens.jinny.api.ApiLink
import sg.prelens.jinny.models.AddBookmarkResonse
import sg.prelens.jinny.models.DefaultResonse
import sg.prelens.jinny.utils.RetrofitLiveData
import java.util.concurrent.Executor

class PromotionOperationRepository(private val api: ApiLink,
                                   private val networkExecutor: Executor) {
    fun bookmarkVoucher(id: String, users_voucher_id: Int, error: MutableLiveData<Throwable>):
            LiveData<AddBookmarkResonse> = Transformations.map(
            RetrofitLiveData(api.bookmarkVoucher(id, users_voucher_id), error)) { it }

    fun archiveVoucher(id: String, users_voucher_id: Int, error: MutableLiveData<Throwable>):
            LiveData<DefaultResonse> = Transformations.map(
            RetrofitLiveData(api.archiveVoucher(id, users_voucher_id), error)) { it }
}