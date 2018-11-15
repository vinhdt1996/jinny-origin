package sg.prelens.jinny.models

/**
 * Created by tommy on 3/13/18.
 */
abstract class BaseResponse(val status: Boolean? = null, val message: String? = null, val code: Int? = null) {
    fun isSuccess() = status == true
}