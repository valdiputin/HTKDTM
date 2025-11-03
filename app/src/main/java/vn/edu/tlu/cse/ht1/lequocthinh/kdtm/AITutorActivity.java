package vn.edu.tlu.cse.ht1.lequocthinh.kdtm;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AITutorActivity extends AppCompatActivity {

    private TextView tvLessonContext;
    private TextView tvAIResponse;
    private Button btnCreateMCQ;
    private Button btnCreateOpenEnded;

    // Giả định nội dung bài học được truyền vào hoặc lấy từ database
    private static final String SAMPLE_LESSON_CONTENT = "Nguyên tắc thiết kế UI/UX cơ bản là sự dễ sử dụng và tính thẩm mỹ. Giao diện người dùng (UI) tập trung vào bố cục và màu sắc, trong khi trải nghiệm người dùng (UX) tập trung vào hành trình của người dùng và cảm xúc họ có được. Một thiết kế tốt là sự cân bằng giữa hai yếu tố này.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_tutor);

        tvLessonContext = findViewById(R.id.tv_lesson_context);
        tvAIResponse = findViewById(R.id.tv_ai_response);
        btnCreateMCQ = findViewById(R.id.btn_create_mcq);
        btnCreateOpenEnded = findViewById(R.id.btn_create_open_ended);

        // Hiển thị nội dung bài học mẫu
        tvLessonContext.setText(SAMPLE_LESSON_CONTENT);

        // Xử lý sự kiện click
        btnCreateMCQ.setOnClickListener(v -> {
            // Yêu cầu AI tạo câu hỏi trắc nghiệm
            requestAIPrompt("Tạo 3 câu hỏi trắc nghiệm từ nội dung sau:", SAMPLE_LESSON_CONTENT);
        });

        btnCreateOpenEnded.setOnClickListener(v -> {
            // Yêu cầu AI tạo câu hỏi mở
            requestAIPrompt("Tạo 1 câu hỏi mở để thảo luận từ nội dung sau:", SAMPLE_LESSON_CONTENT);
        });
    }

    /**
     * Hàm giả lập gọi API AI (Bạn cần thay thế bằng code gọi API Gemini/GPT thực tế)
     * @param instruction Hướng dẫn cho AI (ví dụ: "Tạo câu hỏi trắc nghiệm")
     * @param content Nội dung bài học
     */
    private void requestAIPrompt(String instruction, String content) {
        tvAIResponse.setText("Đang yêu cầu Gia sư AI... Vui lòng chờ.");

        // --- THAY THẾ BẰNG CODE GỌI API AI THỰC TẾ Ở ĐÂY ---
        // Ví dụ: Gemini.generateContent(instruction + content)
        // Khi có kết quả: tvAIResponse.setText(result)

        // GIẢ LẬP KẾT QUẢ TRẢ VỀ
        String mockResponse = "\n\n[Kết quả từ AI Tutor:]\n\n";
        if (instruction.contains("trắc nghiệm")) {
            mockResponse += "Câu 1: Yếu tố nào sau đây tập trung vào bố cục và màu sắc?\nA. UX\nB. UI\nC. Cân bằng\nD. Cảm xúc\n(Đáp án: B)";
        } else {
            mockResponse += "Câu hỏi: Theo bạn, làm thế nào một công ty có thể cân bằng tốt nhất giữa UI và UX khi thiết kế một ứng dụng mới?";
        }

        tvAIResponse.setText(mockResponse);
    }
}