package vn.edu.tlu.cse.ht1.lequocthinh.kdtm

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.adapter.LessonAdapter
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.model.Course
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.model.Lesson
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.service.FirebaseService

// 💡 Hằng số của bạn (Giữ nguyên ở đây)
const val GEMINI_API_KEY = "AIzaSyDWNQVAX2PwvFe7b0yY1Ce2QobrTJQRk2Y"
const val GEMINI_MODEL = "gemini-1.5-flash" // 1.5-flash là model ổn định

class CourseDetailActivity : AppCompatActivity() {

    private lateinit var courseImage: ImageView
    private lateinit var courseTitle: TextView
    private lateinit var courseInstructor: TextView
    private lateinit var courseDescription: TextView
    private lateinit var courseRating: TextView
    private lateinit var courseClassCount: TextView
    private lateinit var lessonsRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    // 💡 Biến cho Gemini
    private lateinit var generativeModel: GenerativeModel
    private var loadingDialog: AlertDialog? = null

    private var course: Course? = null
    private var courseId: String = ""
    private var lastClickedLessonId: String = "" // Track last clicked lesson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_detail)

        courseId = intent.getStringExtra("courseId") ?: ""

        setupViews()
        setupGemini() // Khởi tạo Gemini
        loadCourseDetails()
    }

    // 💡 HÀM ĐÃ SỬA LỖI
    private fun setupGemini() {
        // CẢNH BÁO: Không bao giờ để API Key trực tiếp trong code
        // Đây chỉ là tạm thời để chạy thử

        // Lỗi 1: Xóa 'companion object' khỏi đây.
        // Lỗi 2 & 3: Sửa lại cú pháp hàm và dùng hằng số
        generativeModel = GenerativeModel(
            modelName = "gemini-2.5-flash", // Dùng hằng số
            apiKey = GEMINI_API_KEY   // Dùng hằng số
        )
    }

    private fun setupViews() {
        courseImage = findViewById(R.id.courseImage)
        courseTitle = findViewById(R.id.courseTitle)
        courseInstructor = findViewById(R.id.courseInstructor)
        courseDescription = findViewById(R.id.courseDescription)
        courseRating = findViewById(R.id.courseRating)
        courseClassCount = findViewById(R.id.courseClassCount)
        lessonsRecyclerView = findViewById(R.id.lessonsRecyclerView)
        progressBar = findViewById(R.id.progressBar)

        lessonsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun loadCourseDetails() {
        if (courseId.isEmpty()) {
            finish()
            return
        }

        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.Main).launch {
            try {
                course = withContext(Dispatchers.IO) {
                    FirebaseService.getCourseById(courseId)
                }

                course?.let {
                    displayCourseInfo(it)
                    displayLessons(it.lessons)
                } ?: run {
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                finish()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun displayCourseInfo(course: Course) {
        if (course.imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(course.imageUrl)
                .into(courseImage)
        } else {
            courseImage.setImageResource(R.drawable.mastering_ui_ux)
        }

        courseTitle.text = course.title
        courseInstructor.text = "Giảng viên: ${course.instructor}"
        courseDescription.text = course.description

        if (course.rating > 0) {
            courseRating.text = "⭐ ${course.rating} (${course.reviewCount} reviews)"
        } else {
            courseRating.text = ""
        }

        courseClassCount.text = "${course.classCount} Classes"
    }

    private fun displayLessons(lessons: List<Lesson>) {
        if (lessons.isEmpty()) {
            findViewById<TextView>(R.id.lessonsTitle).visibility = View.GONE
            lessonsRecyclerView.visibility = View.GONE
            return
        }

        val currentUser = FirebaseService.getCurrentUser()

        CoroutineScope(Dispatchers.Main).launch {
            val completedLessons = if (currentUser != null) {
                withContext(Dispatchers.IO) {
                    val user = FirebaseService.getUserById(currentUser.uid)
                    user?.studyHistory?.map { it.lessonId } ?: emptyList()
                }
            } else {
                emptyList()
            }

            val lessonsWithStatus = lessons.map { lesson ->
                lesson.copy(isCompleted = completedLessons.contains(lesson.id))
            }

            // Gọi Adapter với 2 listener
            lessonsRecyclerView.adapter = LessonAdapter(
                lessons = lessonsWithStatus,
                onLessonClick = { lesson ->
                    // Logic xem video
                    lastClickedLessonId = lesson.id
                    openYouTubeVideo(lesson)
                },
                onSummaryClick = { lesson ->
                    // Logic gọi Gemini
                    handleSummaryClick(lesson)
                }
            )
        }
    }

    // 💡 --- CÁC HÀM CỦA GEMINI (Giữ nguyên) ---

    private fun handleSummaryClick(lesson: Lesson) {
        if (lesson.transcriptText.isBlank()) {
            showSummaryDialog("Không có nội dung", "Bài học này chưa có nội dung văn bản (transcript) để tóm tắt.")
            return
        }

        showLoadingDialog("Đang tạo tóm tắt...")

        val prompt = """
            Bạn là một trợ lý học tập. Hãy tóm tắt lại nội dung bài học sau đây 
            theo các ý chính gạch đầu dòng ngắn gọn:

            Nội dung bài học:
            "${lesson.transcriptText}"
        """.trimIndent()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = generativeModel.generateContent(prompt)
                withContext(Dispatchers.Main) {
                    hideLoadingDialog()
                    showSummaryDialog("Tóm tắt: ${lesson.title}", response.text ?: "Không thể tạo tóm tắt.")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    hideLoadingDialog()
                    showSummaryDialog("Lỗi", "Đã xảy ra lỗi khi tóm tắt: ${e.message}")
                }
            }
        }
    }

    private fun showSummaryDialog(title: String, summary: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(summary)
            .setPositiveButton("Đã hiểu") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showLoadingDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        // Đảm bảo bạn có file res/layout/dialog_loading.xml
        val dialogView = inflater.inflate(R.layout.dialog_loading, null)
        val textView = dialogView.findViewById<TextView>(R.id.loadingText)
        textView.text = message

        builder.setView(dialogView)
        builder.setCancelable(false)
        loadingDialog = builder.create()
        loadingDialog?.show()
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }

    // --- KẾT THÚC HÀM GEMINI ---


    private fun openYouTubeVideo(lesson: Lesson) {
        val videoId = lesson.getVideoId()

        if (videoId.isEmpty()) {
            if (lesson.youtubeUrl.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(lesson.youtubeUrl))
                startActivity(intent)
            }
            return
        }

        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId"))
            startActivity(intent)
        } catch (e: Exception) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoId"))
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Logic đánh dấu hoàn thành (Giữ nguyên)
        if (lastClickedLessonId.isNotEmpty() && courseId.isNotEmpty()) {
            val currentUser = FirebaseService.getCurrentUser()
            currentUser?.let { firebaseUser ->
                CoroutineScope(Dispatchers.Main).launch {
                    val user = withContext(Dispatchers.IO) {
                        FirebaseService.getUserById(firebaseUser.uid)
                    }
                    val isAlreadyCompleted = user?.studyHistory?.any {
                        it.lessonId == lastClickedLessonId
                    } ?: false

                    if (!isAlreadyCompleted) {
                        withContext(Dispatchers.IO) {
                            FirebaseService.markLessonCompleted(firebaseUser.uid, courseId, lastClickedLessonId)
                        }
                        course?.let {
                            displayLessons(it.lessons)
                        }
                    }
                    lastClickedLessonId = "" // Reset
                }
            }
        }
    }
}