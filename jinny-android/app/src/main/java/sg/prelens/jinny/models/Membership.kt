package sg.prelens.jinny.models

data class Membership(
        val id:Int?,
        val merchant:Merchant?,
        val code:String?,
        val added_date:String?,
        val has_bookmark:Boolean?,
        val vouchers: List<Voucher>?
)