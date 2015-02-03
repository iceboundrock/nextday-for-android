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
import li.ruoshi.nextday.models.DailyInfoRepository
import android.os.Handler
import org.joda.time.DateTime
import android.widget.ImageView
import com.squareup.picasso.Picasso
import android.graphics.Color
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.PersistableBundle
import android.graphics.Typeface
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.DateTimeFormat
import java.util.Locale
import android.text.TextUtils
import android.util.Base64
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.view.animation.AnimationUtils
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener

public class MainActivity : Activity() {
    val TAG = "MainActivity"
    val handler = Handler()

    var mediaPlayer: MediaPlayer? = null
    var dayOfMonthText: TextView? = null
    var monthAndDayOfWeekText: TextView? = null
    var locationText: TextView? = null
    var artistText: TextView? = null
    var songNameText: TextView? = null
    var textText: TextView? = null

    val dateKeyFormat = DateTimeFormat.forPattern("yyyyMMdd")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        dayOfMonthText          = findViewById(R.id.day_of_month_text) as TextView
        monthAndDayOfWeekText   = findViewById(R.id.month_and_day_of_week_text) as TextView
        locationText            = findViewById(R.id.location_text) as TextView
        artistText              = findViewById(R.id.artist_text) as TextView
        songNameText            = findViewById(R.id.song_name) as TextView
        textText                = findViewById(R.id.text_text) as TextView

        val tf = Typeface.createFromAsset(getAssets(), "fonts/Roboto_Thin.ttf")
        dayOfMonthText!!.setTypeface(tf)
        monthAndDayOfWeekText!!.setTypeface(tf)
        locationText!!.setTypeface(tf)
        artistText!!.setTypeface(tf)
        songNameText!!.setTypeface(tf)
        textText!!.setTypeface(tf)
    }

    fun scaleBitmap(srcBmp: Bitmap, width:Int, height:Int): Bitmap{
        val srcWidth = srcBmp.getWidth()
        val srcHeight = srcBmp.getHeight()
        var dstBmp: Bitmap;
        if ((height.toFloat() / width.toFloat()) < (srcHeight.toFloat() / srcWidth.toFloat())) {
            // image view的高宽比小于图片高宽比，也就是说图片比较瘦长，
            // 那么按照image view的宽度为基准，把图片宽度缩放为image view的宽度，然后等比例缩放高度
            // 缩放完成的结果应该是scaledBmp高于image view高度，再按image view高度裁剪
            val newHeight = (width.toFloat() / srcWidth.toFloat() * srcHeight.toFloat()).toInt()
            val scaledBmp = Bitmap.createScaledBitmap(srcBmp,
                    width.toInt(),
                    newHeight,
                    false)

            dstBmp = Bitmap.createBitmap(scaledBmp,
                    0,
                    ((newHeight - height) / 2).toInt(),
                    width,
                    height)

            scaledBmp.recycle()
        } else {
            // image view的高宽比大于图片高宽比，也就是说图片比较矮胖，
            // 那么按照image view的高度为基准，把图片高度缩放为image view的高度，然后等比例缩放宽度
            val newWidth = (height.toFloat() / srcHeight.toFloat() * srcWidth.toFloat()).toInt()
            val scaledBmp = Bitmap.createScaledBitmap(srcBmp,
                    newWidth,
                    height,
                    false)

            dstBmp = Bitmap.createBitmap(scaledBmp,
                    ((newWidth - width) / 2).toInt(),
                    0,
                    width,
                    height)

            scaledBmp.recycle()
        }

        return dstBmp
    }

    override fun onResume() {

        super.onResume()
        val imageView = findViewById(R.id.main_image_view) as ImageView

        val r = DailyInfoRepository(this)

        r.getTodayAsync {
            (dailyInfo) ->
            val height = imageView.getHeight()
            val width = imageView.getWidth()

            val srcBmp = Picasso.with(this).load(dailyInfo.images.big568h3x).get()
            val dstBmp = scaleBitmap(srcBmp, width, height)
            srcBmp.recycle()

            handler.post({
                () ->
                imageView.setImageBitmap(dstBmp)
                val date = dateKeyFormat.parseDateTime(dailyInfo.dateKey)

                this.dayOfMonthText!!.setText(java.lang.String.format ("%02d", date.getDayOfMonth()))

                val enUS = Locale("en", "US")
                val mon = date.monthOfYear().getAsShortText(enUS)

                val dayOfWeek = date.dayOfWeek().getAsText(enUS)

                this.monthAndDayOfWeekText!!.setText("$mon. $dayOfWeek".toUpperCase())

                locationText!!.setText(dailyInfo.geo.reverse)

                if(!TextUtils.isEmpty(dailyInfo.text.short)){
                    textText!!.setText(dailyInfo.text.short!!)
                } else {
                    val c1 = dailyInfo.text.comment1
                    val c2 = dailyInfo.text.comment2
                    textText!!.setText("$c1\n$c2")
                }

                textText!!.setBackgroundColor(Color.parseColor (dailyInfo.colors.background))


                if (dailyInfo.music != null) {
                    artistText!!.setText(dailyInfo.music.artist)
                    songNameText!!.setText(dailyInfo.music.title)

                    mediaPlayer = MediaPlayer()
                    mediaPlayer!!.setDataSource(dailyInfo.music.url)
                    mediaPlayer!!.prepareAsync()
                    mediaPlayer!!.setOnPreparedListener({ mp -> mp.start() })
                    mediaPlayer!!.start()
                }
            })


        }

    }


    override fun onPause() {
        super.onPause()
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }
}