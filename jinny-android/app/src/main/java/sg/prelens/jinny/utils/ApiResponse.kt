package sg.prelens.jinny.utils

import retrofit2.Response
import sg.prelens.jinny.exceptions.NetworkErrorException
import sg.prelens.jinny.exts.parseMessage
import sg.prelens.jinny.models.BaseResponse
import java.io.IOException


/**
 * Created by tommy on 3/19/18.
 */
/**
 * Common class used by API responses.
 * @param <T>
</T> */
class ApiResponse<T> {
    var code: Int
    var body: T?
    var errorMessage: String?
    var error:Throwable?
    var retry: (() -> Unit)?
    var refresh: (() -> Unit)?

    private constructor(code: Int, body: T?, errorMessage: String?, error: Throwable?, retry: (() -> Unit)?, refresh: (() -> Unit)?) {
        this.code = code
        this.body = body
        this.errorMessage = error?.parseMessage()
        this.error = error
        this.retry = retry
        this.refresh = refresh
    }

    constructor(error: Throwable, retry: (() -> Unit)?) {
        code = 500
        body = null
        errorMessage = error.parseMessage()
        this.error = error
        this.retry = retry
        this.refresh = retry
    }

    constructor(response: Response<T>, retry: (() -> Unit)?) {
        code = response.code()
        val isSuccess = ((response is BaseResponse) && response.isSuccess()) || response.isSuccessful
        if (isSuccess) {
            body = response.body()
            errorMessage = null
            error = null
            this.retry = null
        } else {
            var message: String? = null
            if (response.errorBody() != null) {
                try {
                    message = response.errorBody()?.string()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }

            }
            if (message == null || message.trim { it <= ' ' }.isEmpty()) {
                message = response.message()
            }
            errorMessage = message
            error = NetworkErrorException.newWith(response)
            body = null
            this.retry = retry
        }
        this.refresh = retry
    }

    fun  <R> map(mapFunc:(T?)->(R)):ApiResponse<R>{
        return ApiResponse(code, mapFunc(body), errorMessage, error, retry, refresh)
    }


    fun isSuccessful(): Boolean {
        return code in 200..299
    }

}
