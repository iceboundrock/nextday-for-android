package li.ruoshi.nextday.models


/**
 * Created by ruoshili on 1/11/15.
 */


class DailyInfo(val author: Author?,
                val dateKey: Int,
                val colors: Colors,
                val images: Images,
                val music: Music?,
                val text: Text,
                val video: Video?,
                val event: String,
                val geo: Geo,
                val thumbnail: Thumbnail?,
                val modifiedAt: String) {

}
