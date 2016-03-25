package li.ruoshi.nextday

import dagger.Module
import dagger.Provides
import li.ruoshi.nextday.models.SongPlayer
import javax.inject.Singleton

/**
 * Created by ruoshili on 2/22/16.
 */

@Module
class AppModule {
    @Provides
    @Singleton
    fun provideSongPlayer(): SongPlayer {
        return SongPlayer()
    }
}