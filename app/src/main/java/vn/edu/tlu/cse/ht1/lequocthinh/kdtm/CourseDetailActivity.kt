package vn.edu.tlu.cse.ht1.lequocthinh.kdtm

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.adapter.LessonAdapter
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.model.Course
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.model.Lesson
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.service.FirebaseService

class CourseDetailActivity : AppCompatActivity() {

    private lateinit var courseImage: ImageView
    private lateinit var courseTitle: TextView
    private lateinit var courseInstructor: TextView
    private lateinit var courseDescription: TextView
    private lateinit var courseRating: TextView
    private lateinit var courseClassCount: TextView
    private lateinit var lessonsRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    private var course: Course? = null
    private var courseId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_detail)

        courseId = intent.getStringExtra("courseId") ?: ""
        
        setupViews()
        loadCourseDetails()
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
                    // Course not found, finish activity
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
        // Load course image
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

        // Check completed lessons for current user
        val currentUser = FirebaseService.getCurrentUser()
        val completedLessons = if (currentUser != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val user = FirebaseService.getUserById(currentUser.uid)
                user?.studyHistory?.map { it.lessonId } ?: emptyList()
            }
        } else {
            emptyList()
        }

        // Mark lessons as completed
        val lessonsWithStatus = lessons.map { lesson ->
            lesson.copy(isCompleted = completedLessons.contains(lesson.id))
        }

        lessonsRecyclerView.adapter = LessonAdapter(lessonsWithStatus) { lesson ->
            openYouTubeVideo(lesson)
            // Mark lesson as completed
            currentUser?.let { user ->
                CoroutineScope(Dispatchers.IO).launch {
                    FirebaseService.markLessonCompleted(user.uid, courseId, lesson.id)
                }
            }
        }
    }

    private fun openYouTubeVideo(lesson: Lesson) {
        val videoId = lesson.getVideoId()
        
        if (videoId.isEmpty()) {
            // If no video ID, try to open URL directly
            if (lesson.youtubeUrl.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(lesson.youtubeUrl))
                startActivity(intent)
            }
            return
        }

        // Try to open YouTube app first, then fallback to browser
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId"))
            startActivity(intent)
        } catch (e: Exception) {
            // Fallback to web browser
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoId"))
            startActivity(intent)
        }
    }
}
