package li.ruoshi.nextday.models

import android.media.MediaPlayer
import li.ruoshi.nextday.utils.RxBus

/**
 * Created by ruoshili on 1/10/16.
 */
class SongPlayer {
    companion object {
        val Instance = SongPlayer()
    }

    private enum class PlayerState {
        Init,
        Preparing,
        Playing
    }

    private val mediaPlayer = MediaPlayer()
    private var state: PlayerState = PlayerState.Init
    private var music: Music? = null

    fun getMusic(): Music = music!!


    fun isPlaying(): Boolean = mediaPlayer.isPlaying

    fun play(music: Music) {
        if (state != PlayerState.Init) {
            stop()
        }

        mediaPlayer.setOnPreparedListener {
            it.start()
            state = PlayerState.Playing
            RxBus.default.post(this)
        }

        mediaPlayer.setDataSource(music.url)
        mediaPlayer.prepareAsync()



        state = PlayerState.Preparing

    }

    fun stop() {
        mediaPlayer.stop()
        mediaPlayer.reset()
        state = PlayerState.Init
    }
}