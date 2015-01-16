package li.ruoshi.nextday.models

public data class Video(val autuPlay: Boolean,
                        val autuRepeat: Boolean,
                        val sharing: String,
                        val url: String,
                        val width: Int,
                        val height: Int,
                        val length: Float,
                        val orientation: String)