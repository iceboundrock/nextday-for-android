package li.ruoshi.nextday.models

import java.util.Date

/**
 * Created by ruoshili on 1/11/15.
 */
public class DailyInfo(val author: Author,
                       val dateKey: String,
                       val colors: Colors,
                       val images: Images,
                       val music: Music,
                       val text: Text,
                       val video: Video,
                       val event: String,
                       val geo: Geo,
                       val thumbnail: Thumbnail,
                       val modifiedAt: Date) {

}
