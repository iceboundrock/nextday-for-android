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

/**
 * Created by ruoshili on 1/11/15.
 */
public class DailyInfoRepository(val context: Context, val onNewData: (from: String, to: String) -> Unit) {
    val TAG = javaClass<DailyInfoRepository>().getSimpleName();

    // constants
    val Date = "Date"
    val Authorization = "Authorization"
    val BaseUrl = "http://api.nextday.im"
    val DateRangeFormat = "yyyyMMdd"


    val httpClient = OkHttpClient()

    public fun getLastNDaysAsync(n: Int) : Unit {
        val appKey = AppKey(this.context)
        val now = DateTime()
        val currentTimeString = now.toString("yyyy-MM-dd HH:mm:ss")

        val from = now.minusDays(n).toString(DateRangeFormat)


        val apiPath = "/api/calendar?from=$from"

        Log.d(TAG, "API Path: $apiPath")
        val name = appKey.name
        val hash = appKey.generateMd5Hash(apiPath, currentTimeString)

        val request = Request
                .Builder()
                .url("$BaseUrl$apiPath")
                .addHeader(Date, currentTimeString)
                .addHeader(Authorization, "$name:$hash")
                .build()

        val response = httpClient.newCall(request).enqueue(object: Callback{
            override fun onFailure(request: Request?, e: IOException?) {

            }

            override fun onResponse(response: Response?) {
                onNewData(from, "")
            }
        })
    }
}
