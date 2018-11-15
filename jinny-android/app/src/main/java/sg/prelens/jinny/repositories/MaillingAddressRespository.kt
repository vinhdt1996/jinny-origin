package sg.prelens.jinny.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import sg.prelens.jinny.api.ApiLink
import sg.prelens.jinny.models.DefaultResonse
import sg.prelens.jinny.models.MaillingAddress
import sg.prelens.jinny.models.MaillingAddressResponse
import sg.prelens.jinny.utils.RetrofitLiveData

class MaillingAddressRespository(val apiService: ApiLink) {
    fun postMaillingAddress(maillingAddress: MaillingAddress?, error: MutableLiveData<Throwable>): LiveData<DefaultResonse> =
            RetrofitLiveData(apiService.postMaillingAddress(maillingAddress?.voucher_id
                    ?: "", maillingAddress?.name ?: "",
                    maillingAddress?.street ?: "", maillingAddress?.floor
                    ?: "", maillingAddress?.postal_code ?: "", maillingAddress?.remembered
                    ?: false), error)

    fun putMaillingAddress(maillingAddress: MaillingAddress?, error: MutableLiveData<Throwable>): LiveData<DefaultResonse> =
            RetrofitLiveData(apiService.putMaillingAddress(maillingAddress?.voucher_id
                    ?: "", maillingAddress?.name ?: "",
                    maillingAddress?.street ?: "", maillingAddress?.floor
                    ?: "", maillingAddress?.postal_code ?: "", maillingAddress?.remembered
                    ?: false), error)

    fun getMaillingAddressInfo(error: MutableLiveData<Throwable>): LiveData<MaillingAddressResponse> =
            RetrofitLiveData(apiService.getMaillingAddressInfo(), error)
}