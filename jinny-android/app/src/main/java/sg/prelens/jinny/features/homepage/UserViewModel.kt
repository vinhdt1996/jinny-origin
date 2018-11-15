package sg.prelens.jinny.features.homepage

import android.arch.lifecycle.ViewModel
import sg.prelens.jinny.constant.DEFAULT_PAGE_SIZE
import sg.prelens.jinny.repositories.UserRepository

/**
 * Created by vinova on 1/17/18.
 */
class UserViewModel(private val repository: UserRepository) : ViewModel() {
    private val repoResult = repository.fetchUsers(DEFAULT_PAGE_SIZE)
    val users = repoResult.pagedList
    val networkState = repoResult.networkState
    val refreshState = repoResult.refreshState

    fun refresh() {
        repoResult.refresh.invoke()
    }

    fun retry() {
        repoResult.retry.invoke()
    }

}