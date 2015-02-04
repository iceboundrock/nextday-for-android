package li.ruoshi.nextday.models

import android.content.Context
import com.google.gson.Gson
import li.ruoshi.nextday.R
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.security.MessageDigest

/**
 * Created by ruoshili on 1/13/15.
 */
internal class AppKey(val name: String, val key: String) {
    companion object {
        fun create(context: Context): AppKey {
            val stream = context.resources.openRawResource(R.raw.app_key)
            try {
                val reader = InputStreamReader(stream)
                return Gson().fromJson<AppKey>(reader, AppKey::class.java)
            } finally {
                stream.close()
            }
        }
    }


    fun generateMd5Hash(url: String, date: String): String {
        val data = "$url&$name&$date&$key"

        val dig = MessageDigest.getInstance("MD5")
        val bytes = data.toByteArray(Charset.forName("utf-8"))

        dig.update(bytes)

        val result = dig.digest()

        val sb = StringBuilder();

        result.fold(sb, { s, b -> s.append(java.lang.String.format("%02x", b)) })

        return sb.toString()
    }
}

