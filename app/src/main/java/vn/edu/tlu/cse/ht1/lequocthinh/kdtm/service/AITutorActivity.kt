package vn.edu.tlu.cse.ht1.lequocthinh.kdtm

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.model.QuizQuestion

// Kế thừa BaseActivity nếu bạn có, hoặc dùng AppCompatActivity nếu không có BaseActivity
class AITutorActivity : BaseActivity() {

    // Ánh xạ các ID cũ trong XML
    private lateinit var tvLessonContext: TextView
    private lateinit var tvAIResponse: TextView
    private lateinit var btnCreateMCQ: Button
    private lateinit var btnCreateOpenEnded: Button

    private lateinit var generativeModel: GenerativeModel

    // Giả định nội dung bài học mẫu
    private val SAMPLE_LESSON_CONTENT = "Nguyên tắc thiết kế UI/UX cơ bản là sự dễ sử dụng và tính thẩm mỹ. Giao diện người dùng (UI) tập trung vào bố cục và màu sắc, trong khi trải nghiệm người dùng (UX) tập trung vào hành trình của người dùng và cảm xúc họ có được. Một thiết kế tốt là sự cân bằng giữa hai yếu tố này."

    // Hằng số cho API
    companion object {
        // Khóa API thật của bạn đã được dán
        const val GEMINI_API_KEY = "AIzaSyDWNQVAX2PwvFe7b0yY1Ce2QobrTJQRk2Y"

        // SỬA LỖI API: Dùng model mới được hỗ trợ
        const val GEMINI_MODEL = "gemini-2.5-flash"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_tutor)

        // Khởi tạo model AI
        generativeModel = GenerativeModel(GEMINI_MODEL, GEMINI_API_KEY)

        // Ánh xạ Views
        tvLessonContext = findViewById(R.id.tv_lesson_context)
        tvAIResponse = findViewById(R.id.tv_ai_response)
        btnCreateMCQ = findViewById(R.id.btn_create_mcq)
        btnCreateOpenEnded = findViewById(R.id.btn_create_open_ended)

        // Hiển thị nội dung bài học mẫu
        tvLessonContext.text = SAMPLE_LESSON_CONTENT

        // Xử lý sự kiện click
        btnCreateMCQ.setOnClickListener {
            // Yêu cầu AI tạo 5 câu hỏi trắc nghiệm
            requestAIPrompt("Tạo 5 câu hỏi trắc nghiệm từ nội dung này.", SAMPLE_LESSON_CONTENT, true)
        }

        btnCreateOpenEnded.setOnClickListener {
            // Yêu cầu AI tạo 1 câu hỏi mở
            requestAIPrompt("Tạo 1 câu hỏi mở mang tính thảo luận từ nội dung này.", SAMPLE_LESSON_CONTENT, false)
        }
    }

    /**
     * Hàm gọi API AI để tạo câu hỏi
     */
    private fun requestAIPrompt(instruction: String, content: String, isQuiz: Boolean) {
        tvAIResponse.text = "Đang yêu cầu Gia sư AI... Vui lòng chờ."

        // Vô hiệu hóa nút để tránh spam
        btnCreateMCQ.isEnabled = false
        btnCreateOpenEnded.isEnabled = false

        // Xây dựng Prompt
        val quizJsonStructure = if (isQuiz) """
            Yêu cầu BẮT BUỘC: Trả lời bằng một chuỗi JSON hợp lệ.
            Sử dụng cấu trúc: [{"question": "...", "options": ["..."], "correctAnswerIndex": 0 }].
            Chỉ trả về chuỗi JSON, không có văn bản nào khác.
        """.trimIndent() else ""

        val prompt = "$instruction\n\n---\n$content\n---\n$quizJsonStructure"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = generativeModel.generateContent(prompt)
                val responseText = response.text ?: ""

                withContext(Dispatchers.Main) {
                    btnCreateMCQ.isEnabled = true
                    btnCreateOpenEnded.isEnabled = true

                    if (isQuiz) {
                        // Xử lý tạo Quiz và mở màn hình QuizActivity
                        handleQuizGeneration(responseText)
                    } else {
                        // Xử lý câu hỏi mở (hiển thị trực tiếp)
                        tvAIResponse.text = "\n[Câu hỏi mở từ AI Tutor]:\n\n$responseText"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    btnCreateMCQ.isEnabled = true
                    btnCreateOpenEnded.isEnabled = true
                    tvAIResponse.text = "Lỗi API: Không thể kết nối. Chi tiết: ${e.message}"
                    Toast.makeText(this@AITutorActivity, "Lỗi kết nối AI.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun handleQuizGeneration(jsonResponse: String) {
        val questionsList = parseQuizJson(jsonResponse)

        if (questionsList.isNotEmpty()) {
            // Mở QuizActivity
            val intent = Intent(this, QuizActivity::class.java)
            intent.putParcelableArrayListExtra("QUESTIONS_LIST", ArrayList(questionsList))
            startActivity(intent)
            tvAIResponse.text = "Đã tạo ${questionsList.size} câu hỏi và chuyển sang màn hình Quiz."
        } else {
            // Hiển thị lỗi nếu JSON không hợp lệ
            tvAIResponse.text = "Lỗi: AI trả về JSON không hợp lệ. Vui lòng kiểm tra console để biết lỗi JSON."
            Toast.makeText(this, "Không tạo được Quiz. Thử lại.", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Hàm phân tích JSON
     */
    private fun parseQuizJson(jsonString: String): List<QuizQuestion> {
        val questions = mutableListOf<QuizQuestion>()
        try {
            val cleanedJson = jsonString.trim().removePrefix("```json").removeSuffix("```").trim()

            val jsonArray = JSONArray(cleanedJson)
            for (i in 0 until jsonArray.length()) {
                val jsonObj = jsonArray.getJSONObject(i)

                val question = jsonObj.getString("question")
                val correctIndex = jsonObj.getInt("correctAnswerIndex")

                val optionsArray = jsonObj.getJSONArray("options")
                val options = mutableListOf<String>()
                for (j in 0 until optionsArray.length()) {
                    options.add(optionsArray.getString(j))
                }

                questions.add(QuizQuestion(question, options, correctIndex))
            }
        } catch (e: Exception) {
            // Nếu có lỗi parse JSON, nó sẽ được ghi log
            e.printStackTrace()
        }
        return questions
    }
}