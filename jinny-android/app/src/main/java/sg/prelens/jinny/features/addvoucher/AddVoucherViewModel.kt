package sg.prelens.jinny.features.addvoucher

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import sg.prelens.jinny.models.VoucherResponse
import sg.prelens.jinny.repositories.AddQrCodeRepository
import sg.prelens.jinny.utils.AbsentLiveData

class AddVoucherViewModel(repository: AddQrCodeRepository) : ViewModel() {
    private val addQrCodeRequest = MutableLiveData<String>()
    val addQrCodeLiveData: LiveData<VoucherResponse>
    val errorLiveData: LiveData<Throwable>
    fun setQrCode(code: String) {
        this.addQrCodeRequest.value = code
    }

    init {
        addQrCodeLiveData = Transformations.switchMap<String, VoucherResponse>(addQrCodeRequest,
                {
                    if (it == null) {
                        AbsentLiveData.create()
                    } else
                        repository.addQrCode(it)
                })
        errorLiveData = repository.error
    }
}