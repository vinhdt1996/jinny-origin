package sg.prelens.jinny.features.membership

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import sg.prelens.jinny.constant.DEFAULT_PAGE_SIZE
import sg.prelens.jinny.models.Membership
import sg.prelens.jinny.repositories.MembershipRepository
import sg.prelens.jinny.repositories.inmemory.bypage.ListDataSourceFactory
import java.util.concurrent.Executor

class MembershipViewModel(var repository: MembershipRepository, val executor: Executor) : ViewModel() {
    private var repoResult = repository.fetchMemberships()
    val error: LiveData<String?> = Transformations.map(repoResult) {
        it.errorMessage
    }

    private val config = PagedList
            .Config
            .Builder()
            .setPageSize(50)
            .setInitialLoadSizeHint(50)
            .build()

    val starred = Transformations.switchMap(repoResult) {
        query(it.body?.started_memberships)
    }!!

    val other = Transformations.switchMap(repoResult) {
        query(it.body?.other_memberships)
    }!!

    fun filterOtherList(query: String = "") =
            Transformations.switchMap(repoResult) {
        Transformations.switchMap(repository.filterMembership(it?.body?.other_memberships?: arrayListOf(), query)){
            query(it)
        }
    }!!

    fun filterStarredList(query: String = "") = Transformations.switchMap(repoResult) {
        Transformations.switchMap(repository.filterMembership(it?.body?.started_memberships?: arrayListOf(), query)){
            query(it)
        }
    }!!

    fun filterOtherList(query: Int) = Transformations.switchMap(repoResult) {
        when (query) {
            0 ->
                Transformations.switchMap(repository.sortMembershipByDescending(it?.body?.other_memberships?: arrayListOf())){
                    query(it)
                }

            else -> {
                Transformations.switchMap(repository.sortMembershipByName(it?.body?.other_memberships?: arrayListOf())){
                    query(it)
                }
            }
        }
    }!!

    private fun query(originalList: List<Membership>?): LiveData<PagedList<Membership>> {
        val dataSource = ListDataSourceFactory(originalList ?: emptyList())
        return LivePagedListBuilder(dataSource, config)
                .setBackgroundThreadExecutor(executor)
                .build()
    }

    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }

    fun retry() {
        repoResult.value?.retry?.invoke()
    }
}