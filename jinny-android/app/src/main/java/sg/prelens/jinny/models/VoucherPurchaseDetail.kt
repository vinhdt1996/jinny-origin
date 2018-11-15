package sg.prelens.jinny.models

import java.io.Serializable

data class VoucherPurchaseDetail(
        val id: String?,
        val description: String?,
        val price: String?,
        val cashback_type: String?,
        val expires_at_in_words: String?,
        val terms: String?,
        val image: Logo?
) : Serializable