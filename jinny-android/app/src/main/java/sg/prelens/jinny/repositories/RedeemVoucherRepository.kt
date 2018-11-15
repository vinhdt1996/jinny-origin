package sg.prelens.jinny.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import sg.prelens.jinny.api.ApiLink
import sg.prelens.jinny.models.PromotionDetailResponse
import sg.prelens.jinny.utils.RetrofitLiveData

class RedeemVoucherRepository(val apiService: ApiLink) {
    fun redeemVoucher(id: String, users_voucher_id: Int, error: MutableLiveData<Throwable>):
            LiveData<PromotionDetailResponse> = Transformations.map(
            RetrofitLiveData(apiService.redeemVoucher(id, users_voucher_id), error)) { it }
}
