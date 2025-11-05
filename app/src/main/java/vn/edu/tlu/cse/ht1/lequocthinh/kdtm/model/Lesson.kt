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
    val thumbnailUrl: String = "",

    // 💡 ĐÃ THÊM: Thêm trường này để lưu nội dung tóm tắt
    val transcriptText: String = "",

    // Trường này nên là 'var' để có thể thay đổi trong Adapter
    var isCompleted: Boolean = false
) {
    /**
     * Trích xuất ID video YouTube từ URL (Hàm của bạn đã viết tốt)
     */
    fun getVideoId(): String {
        // Ưu tiên ID đã được cung cấp sẵn
        if (youtubeVideoId.isNotEmpty()) return youtubeVideoId

        // Nếu không, thử trích xuất từ URL
        return when {
            youtubeUrl.contains("youtu.be/") -> {
                // Dạng link rút gọn: https://youtu.be/VIDEO_ID
                youtubeUrl.substringAfter("youtu.be/").substringBefore("?")
            }
            youtubeUrl.contains("youtube.com/watch?v=") -> {
                // Dạng link đầy đủ: https://www.youtube.com/watch?v=VIDEO_ID
                youtubeUrl.substringAfter("v=").substringBefore("&")
            }
            else -> "" // Không tìm thấy
        }
    }
}