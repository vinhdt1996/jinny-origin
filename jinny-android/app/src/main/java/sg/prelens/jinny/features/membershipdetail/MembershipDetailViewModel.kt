package sg.prelens.jinny.features.membershipdetail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import sg.prelens.jinny.constant.DEFAULT_PAGE_SIZE
import sg.prelens.jinny.models.Membership
import sg.prelens.jinny.models.RemoveMembershipResponse
import sg.prelens.jinny.models.Voucher
import sg.prelens.jinny.repositories.MembershipDetailRepository
import sg.prelens.jinny.repositories.inmemory.bypage.ListDataSourceFactory
import sg.prelens.jinny.utils.AbsentLiveData
import java.util.concurrent.Executor

class MembershipDetailViewModel(val repository: MembershipDetailRepository, val executor: Executor) : ViewModel() {
    private val membershipRequest = MutableLiveData<Int>()
    private val removeMembershipRequest = MutableLiveData<Int>()
    val membershipLiveData: LiveData<Membership>
    val removeMembershipLiveData: LiveData<RemoveMembershipResponse>

    private val errorLiveData: MutableLiveData<Throwable> = MutableLiveData()

    private val config = PagedList
            .Config
            .Builder()
            .setPageSize(DEFAULT_PAGE_SIZE)
            .setInitialLoadSizeHint(DEFAULT_PAGE_SIZE)
            .build()

    val relatedPromotions: LiveData<PagedList<Voucher>>

    fun setId(id: Int) {
        this.membershipRequest.value = id
    }

    fun removeMembership(id: Int) {
        this.removeMembershipRequest.value = id
    }

    init {
        membershipLiveData = Transformations.switchMap<Int, Membership>(membershipRequest, {
            if (it == null) {
                AbsentLiveData.create()
            } else {
                repository.fetchMembershipDetail(it, errorLiveData)
            }
        })

        relatedPromotions = Transformations.switchMap<Membership, PagedList<Voucher>>(membershipLiveData, {
            query(it.vouchers)
        })

        removeMembershipLiveData = Transformations.switchMap<Int, RemoveMembershipResponse>(removeMembershipRequest, {
            if (it == null) {
                AbsentLiveData.create()
            } else {
                repository.removeMembership(it, errorLiveData)
            }
        })
    }

    private fun query(originalList: List<Voucher>?): LiveData<PagedList<Voucher>> {
        val dataSource = ListDataSourceFactory(originalList ?: emptyList())
        return LivePagedListBuilder(dataSource, config)
                .setBackgroundThreadExecutor(executor)
                .build()
    }
}