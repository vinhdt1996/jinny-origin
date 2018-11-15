package sg.prelens.jinny.repositories

import sg.prelens.jinny.models.User

/**
 * Common interface shared by the different repository implementations.
 * Note: this only exists for sample purposes - typically an app would implement a repo once, either
 * network+db, or network-only
 */
interface UserRepository {
    fun fetchUsers(pageSize: Int): Listing<User>

//    enum class Type {
//        IN_MEMORY_BY_ITEM
//        IN_MEMORY_BY_PAGE,
//        DB
//    }
}