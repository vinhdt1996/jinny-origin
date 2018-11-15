package sg.prelens.jinny.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import sg.prelens.jinny.api.ApiLink
import sg.prelens.jinny.exts.newIntent
import sg.prelens.jinny.models.Membership
import sg.prelens.jinny.models.MembershipList
import sg.prelens.jinny.utils.ApiResponse
import sg.prelens.jinny.utils.ExecutorLiveData
import java.util.concurrent.Executor

interface MembershipRepository {
    fun fetchMemberships():
            LiveData<ApiResponse<MembershipList?>>

    fun sortMembershipByName(list: List<Membership>): LiveData<List<Membership>>

    fun sortMembershipByDescending(list: List<Membership>): LiveData<List<Membership>>

    fun filterMembership(list: List<Membership>, query: String):LiveData<List<Membership>>
}

class MembershipRepositoryImpl(private val apiService: ApiLink,val executor: Executor) : MembershipRepository {
    override fun sortMembershipByName(list: List<Membership>): LiveData<List<Membership>> =
        ExecutorLiveData(executor) {
            list.sortedBy { it.merchant?.name }
        }

    override fun sortMembershipByDescending(list: List<Membership>): LiveData<List<Membership>> =
        ExecutorLiveData(executor) {
            list.sortedByDescending { it.merchant?.created_at_with_float }
        }

    override fun filterMembership(list: List<Membership>, query: String): LiveData<List<Membership>>  =
        ExecutorLiveData(executor) {
            list.filter {it.merchant?.name?.toLowerCase()?.contains(query) == true }
        }

    override fun fetchMemberships():
            LiveData<ApiResponse<MembershipList?>> = Transformations.map(apiService.getMemberships()) { it.map { it?.result } }
}