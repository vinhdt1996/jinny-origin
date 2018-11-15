package sg.prelens.jinny.models

import java.io.Serializable

data class Voucher(
        val id: String?,
        val users_voucher_id: Int?,
        val description: String?,
        val expires_at: String?,
        val expires_at_in_words: String?,
        val merchant_name: String?,
        val images: List<Logo>?,
        val qrcode: Logo?,
        val is_readed: Boolean?,
        val is_bookmarked: Boolean?,
        val merchant: Merchant?,
        val image: Logo?,
        val archived: Boolean?,
        val is_cashbacked: Boolean?,
        val cashback_percent: String?,
        val is_redeemed: Boolean?,
        val is_expired: Boolean?,
        val can_cashback: Boolean?,
        val terms: String?,
        val cashback_type: String?,
        val cashback_amount: String?,
        val need_promocode: Boolean?
) : Serializable
