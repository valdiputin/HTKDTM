package vn.edu.tlu.cse.ht1.lequocthinh.kdtm

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.R
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.model.QuizQuestion

// Đảm bảo bạn đang kế thừa BaseActivity. Nếu không có, hãy đổi thành AppCompatActivity.
class QuizActivity : BaseActivity() {

    // Danh sách câu hỏi và chỉ mục hiện tại
    private var questionsList: ArrayList<QuizQuestion> = arrayListOf()
    private var currentQuestionIndex = 0
    private var correctAnswersCount = 0

    // Views
    private lateinit var tvQuestionNumber: TextView
    private lateinit var tvQuestion: TextView
    private lateinit var btnOption1: Button
    private lateinit var btnOption2: Button
    private lateinit var btnOption3: Button
    private lateinit var btnOption4: Button
    private lateinit var btnNext: Button
    private lateinit var tvResult: TextView

    // Danh sách các nút đáp án để dễ dàng quản lý
    private lateinit var optionButtons: List<Button>

    // Biến lưu đáp án đã chọn (index của nút)
    private var selectedAnswerIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Đảm bảo bạn đã tạo file layout activity_quiz.xml
        setContentView(R.layout.activity_quiz)

        // 1. Ánh xạ Views
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber)
        tvQuestion = findViewById(R.id.tvQuestion)
        btnOption1 = findViewById(R.id.btnOption1)
        btnOption2 = findViewById(R.id.btnOption2)
        btnOption3 = findViewById(R.id.btnOption3)
        btnOption4 = findViewById(R.id.btnOption4)
        btnNext = findViewById(R.id.btnNext)
        tvResult = findViewById(R.id.tvResult)

        // Tạo danh sách các nút để thao tác
        optionButtons = listOf(btnOption1, btnOption2, btnOption3, btnOption4)

        // 2. Nhận dữ liệu câu hỏi
        questionsList = intent.getParcelableArrayListExtra("QUESTIONS_LIST") ?: arrayListOf()

        if (questionsList.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy câu hỏi nào.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // 3. Thiết lập Listeners
        optionButtons.forEachIndexed { index, button ->
                button.setOnClickListener { onAnswerSelected(index) }
        }

        btnNext.setOnClickListener { onNextClicked() }

        // 4. Bắt đầu Quiz
        displayQuestion()
    }

    /**
     * Hiển thị câu hỏi hiện tại lên giao diện
     */
    private fun displayQuestion() {
        if (currentQuestionIndex >= questionsList.size) {
            showQuizResult()
            return
        }

        val question = questionsList[currentQuestionIndex]

        // Reset trạng thái
        resetButtonsState()
        selectedAnswerIndex = -1
        tvResult.visibility = View.GONE
        btnNext.isEnabled = false // Tắt nút Next cho đến khi chọn đáp án

        // Cập nhật số thứ tự câu hỏi và nội dung câu hỏi
        tvQuestionNumber.text = "Câu hỏi ${currentQuestionIndex + 1}/${questionsList.size}"
        tvQuestion.text = question.question

        // Cập nhật nội dung cho các nút đáp án
        for (i in question.options.indices) {
            if (i < optionButtons.size) {
                optionButtons[i].apply {
                    text = question.options[i]
                    visibility = View.VISIBLE
                    isEnabled = true
                }
            }
        }
    }

    /**
     * Xử lý khi người dùng chọn một đáp án
     */
    private fun onAnswerSelected(selectedIndex: Int) {
        if (selectedAnswerIndex != -1) return // Nếu đã chọn rồi thì không làm gì nữa

                val currentQuestion = questionsList[currentQuestionIndex]
        val correctAnswerIndex = currentQuestion.correctAnswerIndex

        selectedAnswerIndex = selectedIndex
        btnNext.isEnabled = true // Bật nút Next

        // 1. Tô màu đáp án
        if (selectedIndex == correctAnswerIndex) {
            // Đúng
            correctAnswersCount++
            setButtonColor(optionButtons[selectedIndex], R.color.green_correct)
            tvResult.text = "Chính xác! 🎉"
        } else {
            // Sai
            setButtonColor(optionButtons[selectedIndex], R.color.red_wrong)
            setButtonColor(optionButtons[correctAnswerIndex], R.color.green_correct) // Hiện đáp án đúng
            tvResult.text = "Sai rồi. Đáp án đúng là: ${currentQuestion.options[correctAnswerIndex]}."
        }

        // 2. Vô hiệu hóa các nút sau khi chọn
        optionButtons.forEach { it.isEnabled = false }
        tvResult.visibility = View.VISIBLE
    }

    /**
     * Xử lý khi người dùng nhấn nút "Tiếp theo"
     */
    private fun onNextClicked() {
        if (selectedAnswerIndex == -1) {
            Toast.makeText(this, "Vui lòng chọn đáp án trước!", Toast.LENGTH_SHORT).show()
            return
        }

        currentQuestionIndex++
        displayQuestion()
    }

    /**
     * Đặt lại màu và trạng thái ban đầu cho các nút
     */
    private fun resetButtonsState() {
        optionButtons.forEach { button ->
                // Sử dụng màu nền mặc định của button hoặc màu trắng
                button.setBackgroundResource(R.drawable.rounded_button_default)
            button.setTextColor(Color.BLACK) // Màu chữ đen mặc định
            button.visibility = View.GONE // Ẩn để hiển thị lại đúng số lượng
        }
    }

    /**
     * Thiết lập màu nền cho nút đáp án
     */
    private fun setButtonColor(button: Button, colorResId: Int) {
        // Đặt màu nền bằng Drawable đã định nghĩa
        button.setBackgroundResource(R.drawable.rounded_button_selected)
        button.background.setTint(ContextCompat.getColor(this, colorResId))
        button.setTextColor(Color.WHITE)
    }

    /**
     * Hiển thị kết quả cuối cùng của bài Quiz
     */
    private fun showQuizResult() {
        val totalQuestions = questionsList.size
        val percentage = (correctAnswersCount.toFloat() / totalQuestions.toFloat()) * 100
        val message = "Bạn đã hoàn thành bài Quiz!\nĐúng: $correctAnswersCount/$totalQuestions (${String.format("%.1f", percentage)}%)"

        AlertDialog.Builder(this)
                .setTitle("Kết quả Quiz")
                .setMessage(message)
                .setPositiveButton("Làm lại Quiz") { dialog, _ ->
                // Tải lại Quiz
                currentQuestionIndex = 0
            correctAnswersCount = 0
            displayQuestion()
            dialog.dismiss()
        }
            .setNegativeButton("Quay lại màn hình chính") { dialog, _ ->
                // Quay lại HomeActivity hoặc AITutorActivity
                val intent = Intent(this, HomeActivity::class.java) // Thay bằng Activity mong muốn
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
            .setCancelable(false)
                .show()
    }
}