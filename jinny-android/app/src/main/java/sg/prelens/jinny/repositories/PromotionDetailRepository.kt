package sg.prelens.jinny.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import sg.prelens.jinny.api.ApiLink
import sg.prelens.jinny.models.AddBookmarkResonse
import sg.prelens.jinny.models.DefaultResonse
import sg.prelens.jinny.models.RemoveVoucherResponse
import sg.prelens.jinny.models.Voucher
import sg.prelens.jinny.utils.RetrofitLiveData

class PromotionDetailRepository(val apiService: ApiLink) {
    fun fetchPromotionDetail(id: String, users_voucher_id: Int, error: MutableLiveData<Throwable>):
            LiveData<Voucher> = Transformations.map(
            RetrofitLiveData(apiService.fetchPromotionDetail(id, users_voucher_id), error)) { it.result }

    fun bookmarkVoucher(id: String, users_voucher_id: Int, error: MutableLiveData<Throwable>):
            LiveData<AddBookmarkResonse> = Transformations.map(
            RetrofitLiveData(apiService.bookmarkVoucher(id, users_voucher_id), error)) { it }

    fun archiveVoucher(id: String, users_voucher_id: Int, error: MutableLiveData<Throwable>):
            LiveData<DefaultResonse> = Transformations.map(
            RetrofitLiveData(apiService.archiveVoucher(id, users_voucher_id), error)) { it }

    fun removeVoucher(id: String, users_voucher_id: Int, error: MutableLiveData<Throwable>):
            LiveData<RemoveVoucherResponse> = Transformations.map(
            RetrofitLiveData(apiService.removeVoucher(id, users_voucher_id), error)) { it }
}
