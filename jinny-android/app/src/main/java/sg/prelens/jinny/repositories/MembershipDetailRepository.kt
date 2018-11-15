package sg.prelens.jinny.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import sg.prelens.jinny.api.ApiLink
import sg.prelens.jinny.models.Membership
import sg.prelens.jinny.models.RemoveMembershipResponse
import sg.prelens.jinny.utils.RetrofitLiveData

class MembershipDetailRepository(val apiService: ApiLink) {
    fun fetchMembershipDetail(id: Int, error: MutableLiveData<Throwable>): LiveData<Membership> = Transformations.map(RetrofitLiveData(apiService.getMembershipDetail(id), error)) { it.result }
    fun removeMembership(id: Int, error: MutableLiveData<Throwable>): LiveData<RemoveMembershipResponse> = Transformations.map(RetrofitLiveData(apiService.removeMembership(id), error)) { it }
}
