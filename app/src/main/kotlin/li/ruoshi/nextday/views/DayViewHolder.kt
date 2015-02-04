package li.ruoshi.nextday.views

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.text.TextUtils
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import li.ruoshi.nextday.R
import li.ruoshi.nextday.models.DailyInfo
import li.ruoshi.nextday.models.DayViewTextsVisibilityChangedEvent
import li.ruoshi.nextday.models.Music
import li.ruoshi.nextday.models.SongPlayer
import li.ruoshi.nextday.utils.RxBus
import org.threeten.bp.LocalDate
import org.threeten.bp.format.TextStyle
import java.util.*

class DayViewHolder(val context: Context, val view: View) : IDayView {
    private var dailyInfo: DailyInfo? = null

    override fun setData(dailyInfo: DailyInfo) {
        this.dailyInfo = dailyInfo
        updateView()
    }

    fun getData(): DailyInfo? {
        return dailyInfo
    }

    private var dayOfMonthText: TextView? = null
    private var monthAndDayOfWeekText: TextView? = null
    private var locationText: TextView? = null
    private var artistText: TextView? = null
    private var songNameText: TextView? = null
    private var textText: TextView? = null
    private var imageOfDay: ImageView? = null
    private var authorText: TextView? = null
    private var textContainer: View? = null
    private var progressBar: ProgressBar? = null
    private var musicContainer: View? = null

    fun onShow() {

        val anim = TranslateAnimation(-1f * view.width, 0f, 0f, 0f)
        anim.duration = 100
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation?) {
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationStart(animation: Animation?) {
                textContainer!!.visibility = View.VISIBLE
                RxBus.default.post(DayViewTextsVisibilityChangedEvent.Visible)
            }
        })
        textContainer!!.startAnimation(anim)
    }

    private fun updateView() {
        if (dailyInfo == null) {
            return
        }

        if (dailyInfo!!.author != null && !TextUtils.isEmpty(dailyInfo!!.author!!.name)) {
            authorText?.text = "@${dailyInfo!!.author!!.name}"
        }

        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val screenSize = Point()
        val defaultDisplay = wm.defaultDisplay
        defaultDisplay.getSize(screenSize)

        val wtoh = screenSize.x.toFloat() / screenSize.y.toFloat()


        val url = if (Math.abs(wtoh - (320f / 480f)) < Math.abs(wtoh - (1080f / 1920f))) {
            when (screenSize.y / 480) {
                0 -> dailyInfo!!.images.small
                1 -> dailyInfo!!.images.big
                else -> dailyInfo!!.images.big2x
            }
        } else {
            when (screenSize.y / 568) {
                0 or 1 -> dailyInfo!!.images.small568h2x
                2 -> dailyInfo!!.images.big568h2x
                else -> dailyInfo!!.images.big568h3x
            }
        }

        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.bg_placeholder)
                .centerCrop()
                .into(imageOfDay)

        val date = LocalDate.of(
                dailyInfo!!.dateKey / 10000,
                (dailyInfo!!.dateKey % 10000) / 100,
                dailyInfo!!.dateKey % 100)

        this.dayOfMonthText!!.text = java.lang.String.format ("%02d", date.dayOfMonth)

        val enUS = Locale("en", "US")
        val mon = date.month.getDisplayName(TextStyle.SHORT, enUS)

        val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, enUS)

        this.monthAndDayOfWeekText!!.text = "$mon. $dayOfWeek".toUpperCase()

        locationText!!.text = dailyInfo!!.geo.reverse

        if (!dailyInfo!!.text.short.isNullOrEmpty()) {
            textText!!.text = dailyInfo!!.text.short!!
        } else {
            val c1 = dailyInfo!!.text.comment1
            val c2 = dailyInfo!!.text.comment2
            textText!!.text = "$c1\n$c2"
        }

        textText!!.setBackgroundColor(Color.parseColor (dailyInfo!!.colors.background))

        if (SongPlayer.Instance.isPlaying()) {
            updateMusicText(dailyInfo!!.music!!)
            musicContainer?.setOnClickListener({
                SongPlayer.Instance.stop()
            })
        } else {
            if (dailyInfo!!.music != null) {
                updateMusicText(dailyInfo!!.music!!)
                musicContainer?.setOnClickListener({
                    SongPlayer.Instance.play(dailyInfo!!.music!!)
                })
            } else {
                musicContainer?.visibility = View.INVISIBLE
                musicContainer?.setOnClickListener(null)
            }
        }
    }

    private fun updateMusicText(music: Music) {
        artistText!!.text = music.artist
        songNameText!!.text = music.title

    }

    init {
        dayOfMonthText = view.findViewById(R.id.dayOfMonthText) as TextView
        monthAndDayOfWeekText = view.findViewById(R.id.monthAndDayOfWeekText) as TextView
        locationText = view.findViewById(R.id.locationText) as TextView
        artistText = view.findViewById(R.id.artistText) as TextView
        songNameText = view.findViewById(R.id.songNameText) as TextView
        textText = view.findViewById(R.id.textText) as TextView
        imageOfDay = view.findViewById(R.id.image_of_day) as ImageView
        authorText = view.findViewById(R.id.authorText) as TextView
        textContainer = view.findViewById(R.id.textContainer)
        progressBar = view.findViewById(R.id.progress_bar) as ProgressBar
        musicContainer = view.findViewById(R.id.music_container)

        RxBus.default.register(SongPlayer::class.java, context).subscribe({
            if (it.isPlaying()) {
                updateMusicText(it.getMusic())
                if (it.getMusic().url.equals(dailyInfo!!.music!!.url)) {

                } else {

                }
            } else if (dailyInfo != null && dailyInfo!!.music != null) {
                updateMusicText(dailyInfo!!.music!!)

            }
        })

        val gd = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent?): Boolean {
                return true
            }

            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                if (textContainer!!.visibility == View.VISIBLE) {
                    onHide()
                } else {
                    RxBus.default.post(DayViewTextsVisibilityChangedEvent.Visible)

                    val anim = AlphaAnimation(0f, 1f)
                    anim.duration = 200
                    anim.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationEnd(animation: Animation?) {
                        }

                        override fun onAnimationRepeat(animation: Animation?) {
                        }

                        override fun onAnimationStart(animation: Animation?) {
                            textContainer!!.visibility = android.view.View.VISIBLE
                        }
                    })
                    textContainer!!.startAnimation(anim)
                }
                return true
            }

        })

        imageOfDay?.setOnTouchListener({ v, motionEvent -> gd.onTouchEvent(motionEvent) })
    }


    companion object {
        const val TAG = "DayViewHolder"
    }

    fun onHide(shouldNotify: Boolean = true) {
        if (shouldNotify) {
            RxBus.default.post(DayViewTextsVisibilityChangedEvent.Gone)
        }

        val anim = AlphaAnimation(1f, 0f)
        anim.duration = 200
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation?) {
                textContainer!!.visibility = android.view.View.GONE
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })
        textContainer!!.startAnimation(anim)
    }
}