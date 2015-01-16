package li.ruoshi.nextday.views

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import li.ruoshi.nextday.R
import android.widget.TextView
import li.ruoshi.nextday.models.AppKey
import android.util.Log
import com.google.gson.Gson
import java.io.Reader
import java.io.InputStreamReader
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request

public class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    override fun onResume() {
        super.onResume()
        val textBox = findViewById(R.id.text_box) as TextView



    }


}