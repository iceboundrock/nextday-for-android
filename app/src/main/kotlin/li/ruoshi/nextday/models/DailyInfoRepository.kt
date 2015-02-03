package li.ruoshi.nextday.models

import java.security.MessageDigest
import android.content.Context
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import org.joda.time.DateTime
import com.squareup.okhttp.Callback
import android.util.Log
import java.io.IOException
import com.squareup.okhttp.Response
import org.joda.time.format.DateTimeFormat
import java.util.Locale
import com.squareup.okhttp.Headers
import java.util.zip.GZIPInputStream
import okio.GzipSource
import com.squareup.okhttp.internal.http.RealResponseBody
import okio.Okio
import java.io.Reader
import com.google.gson.Gson
import com.google.gson.JsonParser
import android.util.SparseArray
import java.util.ArrayList

/**
 * Created by ruoshili on 1/11/15.
 */
public class DailyInfoRepository(val context: Context) {
    val TAG = javaClass<DailyInfoRepository>().getSimpleName();

    // constants
    val DateHeaderName = "Date"
    val AuthorizationHeaderName = "Authorization"
    val ContentEncodingHeaderName = "Content-Encoding"
    val ContentLengthHeaderName = "Content-Length"
    val UserAgentHeaderName = "User-Agent"
    val UserAgent = "github.com/iceboundrock/nextday-for-android"
    val EmptyString = ""

    val BaseUrl = "http://api.nextday.im"
    val DateRangeFormat = "yyyyMMdd"
    val GZIP = "gzip"


    val httpClient = OkHttpClient()

    val cache = SparseArray<DailyInfo>()


    public fun getTodayAsync(onNewData: (dailyInfo: DailyInfo) -> Unit) {
        val today = DateTime().minusDays(1)
        val todayKey = today.getYear() * 100 * 100 + today.getMonthOfYear() * 100 + today.getDayOfMonth()
        getDaysAsync(todayKey.toString(), "", {
            () ->


            onNewData(cache.get(todayKey))
        })
    }

    private fun isOk(resp: Response): Boolean {
        val rc = resp.code()

        return rc >= 200 && rc < 300
    }

    private fun getDaysAsync(fromDate: String,
                             toDate: String,

                             onNewData: () -> Unit,
                             retryTimes: Int = 0,
                             without: List<String> = ArrayList(0)
    ) {
        val appKey = AppKey(this.context)
        val now = DateTime()
        val fmt = DateTimeFormat.forPattern("E MMM dd yyyy HH:mm:ss 'GMT'Z").withLocale(Locale.US)
        val currentTimeString = now.toString(fmt)

        var apiPath = StringBuilder("/api/calendar")


        if (fromDate.length() == DateRangeFormat.length()) {
            apiPath.append ("?from=").append(fromDate)
        }

        if (toDate.length() == DateRangeFormat.length()) {
            apiPath.append("&to=").append(toDate)
        }

        if (without.count() > 0) {
            apiPath.append("&")
            without.fold(apiPath, {
                (sb, s) ->
                    sb.append(s).append(",")
                }
            )
            apiPath.deleteCharAt(apiPath.length() - 1)
        }

        val url = "$BaseUrl$apiPath"


        val name = appKey.name
        val hash = appKey.generateMd5Hash(apiPath.toString(), currentTimeString)

        Log.d(TAG, "API Path: $apiPath , Date: $currentTimeString , Url: $url , Name: $name , hash: $hash")


        val request = Request
                .Builder()
                .url(url)
                .addHeader(UserAgentHeaderName, UserAgent)
                .addHeader(DateHeaderName, currentTimeString)
                .addHeader(AuthorizationHeaderName, "$name:$hash")
                .addHeader(ContentEncodingHeaderName, "gzip, deflate")
                .get()
                .build()

        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(request: Request?, e: IOException?) {
                if (retryTimes < 3) {
                    getDaysAsync(fromDate, toDate, onNewData, retryTimes + 1)
                }
            }

            override fun onResponse(response: Response?) {
                if ((response == null || !(isOk(response))) && retryTimes < 3) {
                    getDaysAsync(fromDate, toDate, onNewData, retryTimes + 1)
                    return
                }

                val unzipped = unzipRespBody(response!!)
                val body = unzipped.body()
                Log.d(TAG, "body type: " + body!!.javaClass.getName())

                parseData(body.charStream())

                onNewData()
                body.close()
            }
        })
    }

    private fun unzipRespBody(response: Response): Response {
        val ce = response.header(ContentEncodingHeaderName, EmptyString)

        if (!GZIP.equalsIgnoreCase(ce)) {
            return response;
        }

        if (response.body() == null) {
            return response;
        }

        val responseBody = GzipSource(response.body().source());
        val strippedHeaders = response.headers().newBuilder()
                .removeAll(ContentEncodingHeaderName)
                .removeAll(ContentLengthHeaderName)
                .build();
        return response.newBuilder()
                .headers(strippedHeaders)
                .body(RealResponseBody(strippedHeaders, Okio.buffer(responseBody)))
                .build();
    }

    private fun parseData(reader: Reader) {
        val parser = JsonParser()

        val gson = Gson()

        val data = parser.parse(reader).getAsJsonObject()
        val results = data.get("result").getAsJsonObject().entrySet()

        results.forEach { e ->
            if (!"hasMore".equals(e.getKey())) {
                val day = Integer.parseInt (e.getKey())
                val dailyInfo = gson.fromJson(e.getValue(), javaClass<DailyInfo>())
                cache.append(day, dailyInfo)
            }
        }


    }

    public fun getLastNDaysAsync(n: Int, onNewData: () -> Unit): Unit {
        val now = DateTime()
        val from = now.minusDays(n).toString(DateRangeFormat)
        val to = now.toString(DateRangeFormat)

        getDaysAsync(from, to, onNewData)
    }
}
