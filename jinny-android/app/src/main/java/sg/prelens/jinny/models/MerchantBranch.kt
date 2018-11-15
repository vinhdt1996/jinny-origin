package sg.prelens.jinny.models

import java.io.Serializable

data class MerchantBranch(
        val id:Int?,
        val address:String?,
        val name:String?,
        val merchant_id:Int?,
        val opening_hours: List<WorkingHours?>,
        val created_at:String?
):Serializable