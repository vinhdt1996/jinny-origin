package sg.prelens.jinny.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import sg.prelens.jinny.db.dao.VoucherDao
import sg.prelens.jinny.db.entity.VoucherEntity

/**
 * Author      : BIMBIM<br>.
 * Create Date : 4/11/18<br>.
 */
@Database(
        entities = arrayOf(VoucherEntity::class),
        version = 1,
        exportSchema = false
)
abstract class VoucherDB : RoomDatabase() {
    companion object {
        fun create(context: Context, useInMemory: Boolean): VoucherDB {
            val databaseBuilder = if (useInMemory) {
                Room.inMemoryDatabaseBuilder(context, VoucherDB::class.java)
            } else {
                Room.databaseBuilder(context, VoucherDB::class.java, "Voucher.db")
            }
            return databaseBuilder
                    .fallbackToDestructiveMigration()
                    .build()
        }
    }

    abstract fun voucher(): VoucherDao
}