package sg.prelens.jinny.features.addbarcode

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import sg.prelens.jinny.models.AddBarcodeResponse
import sg.prelens.jinny.models.BaseResponse
import sg.prelens.jinny.models.MembershipDetailResponse
import sg.prelens.jinny.repositories.AddBarcodeRespository
import sg.prelens.jinny.utils.AbsentLiveData

/**
 * Author : BIMBIM<br>.
 * Create Date : 3/22/18<br>.
 */
class AddBarcodeViewModel(repository: AddBarcodeRespository) : ViewModel() {
    private val addBarcodeRequest = MutableLiveData<Pair<String, Int>>()
    val addBarcodeLiveData: LiveData<MembershipDetailResponse>
    val errorLiveData: LiveData<Throwable>
    fun setBarcode(code: String, merchantID: Int) {
        this.addBarcodeRequest.value = Pair(code, merchantID)
    }

    init {
        addBarcodeLiveData = Transformations.switchMap<Pair<String, Int>, MembershipDetailResponse>(addBarcodeRequest,
                {
                    if (it == null) {
                        AbsentLiveData.create()
                    } else
                        repository.addBarcode(it.first, it.second)
                })
        errorLiveData = repository.error
    }
}