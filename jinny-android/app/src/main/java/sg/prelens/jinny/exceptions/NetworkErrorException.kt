package sg.prelens.jinny.exceptions

import sg.prelens.jinny.models.BaseResponse

/**
 * Created by tommy on 3/13/18.
 */

class NetworkErrorException(message: String?) : Exception(message) {
    companion object {
        val DEFAULT_ERROR = NetworkErrorException("Server error")
        fun newWith(response: BaseResponse?):NetworkErrorException =
            response?.message?.let {
                NetworkErrorException(it)
            } ?: DEFAULT_ERROR
        fun newWith(response: Any?):NetworkErrorException = if (response is BaseResponse) newWith(response) else DEFAULT_ERROR

    }
}