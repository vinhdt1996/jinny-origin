package sg.prelens.jinny.repositories

import sg.prelens.jinny.db.entity.VoucherEntity

/**
 * Author      : BIMBIM<br>.
 * Create Date : 4/11/18<br>.
 */
interface VoucherRepository {
    fun vouchersRepository(voucher: String, pageSize: Int): Listing<VoucherEntity>
    enum class Type {
        IN_MEMORY_BY_ITEM,
        IN_MEMORY_BY_PAGE,
        DB
    }
}