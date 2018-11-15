package sg.prelens.jinny.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "Voucher")
data class VoucherEntity(@PrimaryKey(autoGenerate = true)
                         @SerializedName("id")
                         @ColumnInfo(name = "id")
                         val id: String?,
                         @SerializedName("description")
                         @ColumnInfo(name = "description")
                         val description: String?,
                         @SerializedName("expires_at")
                         @ColumnInfo(name = "expires_at")
                         val expires_at: String?,
                         @SerializedName("expires_at_in_words")
                         @ColumnInfo(name = "expires_at_in_words")
                         val expires_at_in_words: String?,
                         @SerializedName("merchant_name")
                         @ColumnInfo(name = "merchant_name")
                         val merchant_name: String?,
                         @SerializedName("is_readed")
                         @ColumnInfo(name = "is_readed")
                         val is_readed: Boolean?,
                         @SerializedName("is_bookmarked")
                         @ColumnInfo(name = "is_bookmarked")
                         val is_bookmarked: Boolean?,
                         @SerializedName("is_archived")
                         @ColumnInfo(name = "is_archived")
                         val archived: Boolean)
