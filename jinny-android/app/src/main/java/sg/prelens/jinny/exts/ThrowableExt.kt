package sg.prelens.jinny.exts

import okhttp3.internal.http2.ConnectionShutdownException
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException
import java.nio.channels.ConnectionPendingException
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeoutException

fun Throwable.parseMessage(): String {
    return when (this) {
        is UnknownHostException -> "Please check your internet connection or try again later"
        is TimeoutException, is InterruptedException,
        is SocketTimeoutException -> "Request timed out!\nPlease check your internet connection\nand try again later"
        is SocketException -> "Request timed out!\nPlease check your internet connection\nand try again later"
        is SSLException -> "Request timed out!\nPlease check your internet connection\nand try again later"
        is ConnectionPendingException, is ConnectionShutdownException, is ExecutionException, is IOException -> "Please check your internet connection or try again later"
        else -> this.localizedMessage
    }
}