package vn.edu.tlu.cse.ht1.lequocthinh.kdtm

import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.BuildConfig

object GeminiHelper {

    suspend fun summarizeYouTubeContent(youtubeUrl: String): String {
        val apiKey: String = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            return "Lỗi: Không thể lấy GEMINI_API_KEY."
        }
        if (apiKey.isBlank() || apiKey == "YOUR_GEMINI_API_KEY_HERE") {
            return "Lỗi: GEMINI_API_KEY không hợp lệ."
        }

        if (youtubeUrl.isBlank()) {
            return "Lỗi: URL YouTube không hợp lệ."
        }

        val model = GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = apiKey
        )

        val prompt = """
            Hãy truy cập và phân tích nội dung của video YouTube tại URL sau: $youtubeUrl
            
            Vui lòng cung cấp:
            1. Tóm tắt nội dung chính của video
            2. Các điểm chính được đề cập
            3. Kết luận quan trọng (nếu có)
            
            Trả lời bằng tiếng Việt.
        """.trimIndent()

        return try {
            withContext(Dispatchers.IO) {
                println("Đang gửi yêu cầu tóm tắt cho Gemini...")
                val response = model.generateContent(prompt)
                response.text ?: "Không nhận được phản hồi văn bản từ mô hình."
            }
        } catch (e: Exception) {
            println("Lỗi khi gọi Gemini API: ${e.message}")
            "Lỗi khi gọi Gemini API: ${e.message}"
        }
    }
}


