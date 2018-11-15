package sg.prelens.jinny.api

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*

/**
 * Author : BIMBIM<br>.
 * Create Date : 3/12/18<br>.
 */
class ProgressResponseBody : ResponseBody {
    private val responseBody: ResponseBody
    private lateinit var bufferedSource: BufferedSource
    private var mProgress: Float = 0.0f

    constructor(response: ResponseBody) {
        this.responseBody = response
    }

    override fun contentType(): MediaType? {
        return responseBody.contentType()
    }

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()))
        }
        return bufferedSource
    }

    private fun source(source: Source): Source {
        return object : ForwardingSource(source) {
            var totalBytesRead = 0L
            override fun read(sink: Buffer?, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                //update(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                mProgress = if (bytesRead == -1L) 1F else bytesRead.toFloat() / responseBody.contentLength()
                return bytesRead
            }
        }
    }

    override fun contentLength(): Long {
        return responseBody.contentLength()
    }
}