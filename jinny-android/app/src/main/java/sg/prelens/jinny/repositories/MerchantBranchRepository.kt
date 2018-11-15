package sg.prelens.jinny.repositories

import sg.prelens.jinny.models.MerchantBranch

interface MerchantBranchRepository {
    fun fetchMerchantBranch(id: Int, pageSize: Int): Listing<MerchantBranch>
}