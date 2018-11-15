package sg.prelens.jinny.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import sg.prelens.jinny.api.ApiLink
import sg.prelens.jinny.models.RegionList
import sg.prelens.jinny.utils.RetrofitLiveData

/**
 * Author : BIMBIM<br>.
 * Create Date : 3/27/18<br>.
 */
class RegionRespository(val apiService: ApiLink) {
    fun getRegion(error: MutableLiveData<Throwable>): LiveData<RegionList> = RetrofitLiveData(apiService.getRegions(), error)
}