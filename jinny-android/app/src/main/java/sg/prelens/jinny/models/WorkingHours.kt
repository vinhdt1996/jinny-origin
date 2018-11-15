package sg.prelens.jinny.models

import java.io.Serializable

data class WorkingHours(
        val day:String?,
        val start_time:String?,
        val close_time:String?
):Serializable