package vn.edu.tlu.cse.ht1.lequocthinh.kdtm.model

data class Lesson(
    val id: String = "",
    val courseId: String = "",
    val title: String = "",
    val description: String = "",
    val youtubeVideoId: String = "", // YouTube video ID (e.g., "dQw4w9WgXcQ")
    val youtubeUrl: String = "", // Full YouTube URL
    val duration: String = "", // e.g., "10:30"
    val order: Int = 0,
    val isCompleted: Boolean = false,
    val thumbnailUrl: String = ""
) {
    /**
     * Extract YouTube video ID from URL
     */
    fun getVideoId(): String {
        if (youtubeVideoId.isNotEmpty()) return youtubeVideoId
        
        return when {
            youtubeUrl.contains("youtu.be/") -> {
                youtubeUrl.substringAfter("youtu.be/").substringBefore("?")
            }
            youtubeUrl.contains("youtube.com/watch?v=") -> {
                youtubeUrl.substringAfter("v=").substringBefore("&")
            }
            else -> ""
        }
    }
}

