package li.ruoshi.nextday.models

data class Video(val autoPlay: Boolean,
                 val autuRepeat: Boolean,
                 val sharing: String,
                 val url: String,
                 val width: Int,
                 val height: Int,
                 val length: Float,
                 val orientation: String)