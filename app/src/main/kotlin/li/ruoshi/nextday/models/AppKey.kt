package li.ruoshi.nextday.models

import java.security.MessageDigest
import com.google.gson.Gson
import li.ruoshi.nextday.R
import java.io.InputStreamReader
import android.content.Context
import android.util.Log
import java.net.URLEncoder

/**
 * Created by ruoshili on 1/13/15.
 */

public fun AppKey(context: Context) : AppKey {
    val stream = context.getResources().openRawResource(R.raw.app_key)
    try {
        val reader = InputStreamReader(stream)
        return Gson().fromJson<AppKey>(reader, javaClass<AppKey>())
    }finally {
        stream.close()
    }
}

public class AppKey(val name:String, val key:String){
    public fun generateMd5Hash(url:String, date:String) : String{
        val data = java.lang.String.format ("%s&%s&%s&%s", url, name, date , key)
        Log.d("AppKey", data)
        val dig = MessageDigest.getInstance("MD5")

        val bytes = data.toByteArray("utf-8")

        dig.update(bytes)

        val result = dig.digest()

        val sb = StringBuilder();

        for (b in result){
            sb.append(java.lang.String.format("%02x", b));
        }

        return sb.toString()
    }
}

