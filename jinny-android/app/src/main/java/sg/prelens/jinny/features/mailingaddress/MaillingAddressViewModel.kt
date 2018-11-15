package sg.prelens.jinny.features.mailingaddress

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import sg.prelens.jinny.models.DefaultResonse
import sg.prelens.jinny.models.MaillingAddress
import sg.prelens.jinny.models.MaillingAddressResponse
import sg.prelens.jinny.repositories.MaillingAddressRespository
import sg.prelens.jinny.utils.AbsentLiveData

class MaillingAddressViewModel(respository: MaillingAddressRespository) : ViewModel() {
    private val postMaillingAddressRespositoty = MutableLiveData<MaillingAddress?>()
    private val putMaillingAddressRespositoty = MutableLiveData<MaillingAddress?>()
    val postMaillingAddressResult: LiveData<DefaultResonse>
    val getMaillingAddressResult: LiveData<MaillingAddressResponse>
    val putMaillingAddressResult: LiveData<DefaultResonse>
    val errorLiveData: MutableLiveData<Throwable> = MutableLiveData()

    fun postMaillingAddress(maillingAddress: MaillingAddress?) {
        postMaillingAddressRespositoty.value = maillingAddress
    }

    fun putMaillingAddress(maillingAddress: MaillingAddress?) {
        putMaillingAddressRespositoty.value = maillingAddress
    }

    init {
        postMaillingAddressResult = Transformations.switchMap(postMaillingAddressRespositoty, {
            if (it == null) {
                AbsentLiveData.create()
            } else {
                respository.postMaillingAddress(it, errorLiveData)
            }
        })
        putMaillingAddressResult = Transformations.switchMap(putMaillingAddressRespositoty, {
            if (it == null) {
                AbsentLiveData.create()
            } else {
                respository.putMaillingAddress(it, errorLiveData)
            }
        })
        getMaillingAddressResult = respository.getMaillingAddressInfo(errorLiveData)
    }
}