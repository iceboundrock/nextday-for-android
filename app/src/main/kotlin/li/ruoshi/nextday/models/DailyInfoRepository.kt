package li.ruoshi.nextday.models

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonParser
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import retrofit2.*
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Url
import rx.Observable
import java.io.InputStreamReader
import java.lang.reflect.Type
import java.util.*

/**
 * Created by ruoshili on 1/11/15.
 */
class DailyInfoRepository(val context: Context) {
    // constants
    companion object {
        const val TAG = "DailyInfoRepository";
        const val BaseUrl = "http://api.nextday.im/"
        const val DateRangeFormat = "yyyyMMdd"
    }

    private val cache = ArrayList<DailyInfo>(64)
    private val logging: HttpLoggingInterceptor
    private val httpClient: OkHttpClient
    private val nextDayService: NextDayService

    // add your other interceptors …
    // add logging as last interceptor
    init {
        logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        httpClient = OkHttpClient
                .Builder()
                .addInterceptor(HttpGzipInterceptor())
                .addInterceptor(logging)
                .build()

        nextDayService = Retrofit
                .Builder()
                .addConverterFactory(DailyInfoConverterFactory())
                .baseUrl(BaseUrl).client(httpClient)
                .build()
                .create(NextDayService::class.java)
    }


    private class DailyInfoConverterFactory() : Converter.Factory() {
        override fun responseBodyConverter(type: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, *>? {
            if (type == null) {
                return null
            }

            return Converter<okhttp3.ResponseBody, List<DailyInfo>> {

                val reader = InputStreamReader(it.byteStream())
                val parser = JsonParser()

                val gson = Gson()
                val data = parser.parse(reader).asJsonObject
                val results = data.get("result").asJsonObject.entrySet()

                val ret = ArrayList<DailyInfo>()

                results.forEach {
                    val key = it.key
                    val value = it.value

                    if (!"hasMore".equals(key)) {
                        val dailyInfo = gson.fromJson(value, DailyInfo::class.java)
                        ret.add (dailyInfo)
                    }
                }

                reader.close()
                ret
            }
        }
    }

    private interface NextDayService {
        @GET
        @Headers("Accept-Encoding: gzip, deflate")
        fun getDays(@Url url: String,
                    @Header("Authorization") auth: String,
                    @Header("Date") date: String): Call<List<DailyInfo>>;
    }

    fun loadDaysAsync(fromDate: LocalDate, toDate: LocalDate?): Observable<List<DailyInfo>> {
        val from = (fromDate.year * 100 * 100 + (fromDate.month.value * 100) + fromDate.dayOfMonth).toString()
        val to = if (toDate == null) "" else (toDate.year * 100 * 100 + (toDate.month.value * 100) + toDate.dayOfMonth).toString()
        return getDaysAsync(from, to, listOf("thumbnail", "video")).retry(3)
    }

    private fun getDaysAsync(fromDate: String,
                             toDate: String,
                             without: List<String> = emptyList()): Observable<List<DailyInfo>> {
        val appKey = AppKey.create(this.context)
        val now = ZonedDateTime.now()
        val fmt = DateTimeFormatter.ofPattern("E MMM dd yyyy HH:mm:ss 'GMT'Z").withLocale(Locale.US)
        val currentTimeString = now.format(fmt)

        var apiPath = StringBuilder("/api/calendar")

        if (fromDate.length == DateRangeFormat.length) {
            apiPath.append ("?from=").append(fromDate)
        }

        if (toDate.length == DateRangeFormat.length) {
            apiPath.append("&to=").append(toDate)
        }

        if (!without.isEmpty()) {
            apiPath.append("&without=")
            without.fold(apiPath, { sb, s -> sb.append(s).append(",") })
            apiPath.deleteCharAt(apiPath.length - 1)
        }


        val name = appKey.name
        val hash = appKey.generateMd5Hash(apiPath.toString(), currentTimeString)

        Log.d(TAG, "API Path: $apiPath , Date: $currentTimeString , Name: $name , App key: ${appKey.key} , hash: $hash")

        val call = nextDayService.getDays(apiPath.toString(), "$name:$hash", currentTimeString)

        return Observable.create<Response<List<DailyInfo>>> {
            it.onStart()
            try {
                val c = call.clone()
                c.enqueue(object : Callback<List<DailyInfo>> {

                    override fun onFailure(call: Call<List<DailyInfo>>?, t: Throwable?) {
                        it.onError(t)
                    }

                    override fun onResponse(call: Call<List<DailyInfo>>?, response: Response<List<DailyInfo>>?) {
                        val respCode = response!!.code()
                        if (respCode >= 300 || respCode < 200) {
                            Log.w(TAG, "get days failed, resp code: $respCode , message: ${response.message()}")
                            it.onError(IllegalArgumentException("get days failed, resp code: $respCode , message: ${response.message()}"))
                            return
                        }

                        it.onNext(response)
                        it.onCompleted()
                    }
                })
            } catch(e: Throwable) {
                it.onError(e)
            }
        }.map { it -> it.body() }


    }
}
