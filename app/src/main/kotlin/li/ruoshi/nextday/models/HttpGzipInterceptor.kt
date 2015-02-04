package li.ruoshi.nextday.models

import android.text.TextUtils
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.http.RealResponseBody
import okio.GzipSource
import okio.Okio

/**
 * Created by ruoshili on 1/10/16.
 */
class HttpGzipInterceptor : Interceptor {
    companion object {
        const val ContentEncoding = "Content-Encoding"
        const val AcceptEncoding = "Accept-Encoding"
    }

    override fun intercept(chain: Interceptor.Chain?): Response? {
        if (chain == null) {
            return null
        }
        val request = chain.request()

        var newReq: Request = request;

        if (TextUtils.isEmpty(request.header(AcceptEncoding))) {
            newReq = request
                    .newBuilder()
                    .addHeader(AcceptEncoding, "gzip, deflate")
                    .build()
        }

        val resp = chain.proceed(newReq)
        return unzipRespBody(resp)
    }


    private fun unzipRespBody(response: Response): Response {
        val ce = response.header(ContentEncoding, "")

        if (!"gzip".equals(ce, ignoreCase = true)) {
            return response;
        }

        val body = response.body() ?: return response

        val responseBody = GzipSource(body.source());

        val strippedHeaders = response.headers().newBuilder()
                .removeAll(ContentEncoding)
                .removeAll("Content-Length")
                .build();
        return response.newBuilder()
                .headers(strippedHeaders)
                .body(RealResponseBody(strippedHeaders, Okio.buffer(responseBody)))
                .build();
    }

}