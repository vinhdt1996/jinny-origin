package sg.prelens.jinny.features.editprofile

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import sg.prelens.jinny.models.LogoutResponse
import sg.prelens.jinny.models.RegionList
import sg.prelens.jinny.repositories.RegionRespository

/**
 * Author : BIMBIM<br>.
 * Create Date : 3/27/18<br>.
 */
class RegionViewModel(repository: RegionRespository) : ViewModel() {
    val regionLiveData: LiveData<RegionList>
    val errorLiveData: MutableLiveData<Throwable> = MutableLiveData()

    init {
        regionLiveData = repository.getRegion(errorLiveData)
    }
}