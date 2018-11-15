package sg.prelens.jinny.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import sg.prelens.jinny.db.entity.VoucherEntity

/**
 * Author      : BIMBIM<br>.
 * Create Date : 4/9/18<br>.
 */
@Dao
interface VoucherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(voucher: List<VoucherEntity>)
}
