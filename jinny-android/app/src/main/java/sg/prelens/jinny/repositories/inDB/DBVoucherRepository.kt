package sg.prelens.jinny.repositories.inDB

import sg.prelens.jinny.api.ApiLink
import sg.prelens.jinny.db.VoucherDB
import sg.prelens.jinny.db.entity.VoucherEntity
import sg.prelens.jinny.models.PromotionResponse
import sg.prelens.jinny.repositories.Listing
import sg.prelens.jinny.repositories.VoucherRepository
import java.util.concurrent.Executor

/**
 * Author      : BIMBIM<br>.
 * Create Date : 4/11/18<br>.
 */
class DBVoucherRepository(
        val db: VoucherDB,
        private val redditApi: ApiLink,
        private val ioExecutor: Executor,
        private val networkPageSize: Int = DEFAULT_NETWORK_PAGE_SIZE) : VoucherRepository {
    override fun vouchersRepository(voucher: String, pageSize: Int): Listing<VoucherEntity> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        private val DEFAULT_NETWORK_PAGE_SIZE = 10
    }

    /**
     * Inserts the response into the database while also assigning position indices to items.
     */
    private fun insertResultIntoDb(subredditName: String, body: PromotionResponse?) {
        body?.results.let { voucher ->
            db.runInTransaction {

                //db.voucher().insert(voucher)
            }
        }
    }
}