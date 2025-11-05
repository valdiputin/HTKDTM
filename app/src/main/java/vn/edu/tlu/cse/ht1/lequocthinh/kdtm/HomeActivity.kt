package vn.edu.tlu.cse.ht1.lequocthinh.kdtm

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.adapter.CourseAdapter
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.model.Course
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.service.CourseRecommendationService
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.service.FirebaseService

class HomeActivity : BaseActivity() {

    private lateinit var recommendedRecyclerView: RecyclerView
    private lateinit var popularRecyclerView: RecyclerView
    private lateinit var freeRecyclerView: RecyclerView
    private lateinit var recommendedTitle: TextView
    private lateinit var popularTitle: TextView
    private lateinit var freeTitle: TextView
    private lateinit var progressBar: ProgressBar

    private var allCourses: List<Course> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupViews()
        setupRecyclerViews()
        loadCourses()
        setupBottomNavigation(R.id.nav_home)

        // ✅ THÊM PHẦN NÀY — TẠO KÊNH THÔNG BÁO NHẮC HỌC
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "study_channel",
                "Nhắc học tập",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Kênh thông báo nhắc học tập"
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        // ✅ Android 13 trở lên cần xin quyền thông báo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }

        // ✅ Gọi lại logic click categories
        setupCategoryClickListeners()
    }

    private fun setupViews() {
        recommendedTitle = findViewById(R.id.recommendedTitle)
        popularTitle = findViewById(R.id.popularTitle)
        freeTitle = findViewById(R.id.freeTitle)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupRecyclerViews() {
        // Recommended courses RecyclerView
        recommendedRecyclerView = findViewById(R.id.recommendedRecyclerView)
        recommendedRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Popular courses RecyclerView
        popularRecyclerView = findViewById(R.id.popularRecyclerView)
        popularRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Free courses RecyclerView
        freeRecyclerView = findViewById(R.id.freeRecyclerView)
        freeRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setupCategoryClickListeners() {
        // Nút 1: Các Lớp Học
        val classesCategory: LinearLayout? = findViewById(R.id.category_classes)
        classesCategory?.setOnClickListener {
            val intent = Intent(this, ClassingActivity::class.java)
            startActivity(intent)
        }

        // Nút 2: Ngôn ngữ
        val languageCategory: LinearLayout? = findViewById(R.id.category_language)
        languageCategory?.setOnClickListener {
            val intent = Intent(this, LanguageActivity::class.java)
            startActivity(intent)
        }

        // Nút 3: Mở rộng (AI Tutor)
        val expandCategory: LinearLayout? = findViewById(R.id.category_expand)
        expandCategory?.setOnClickListener {
            val intent = Intent(this, AITutorActivity::class.java)
            startActivity(intent)
        }

        // ✅ Nút 4: Nhắc học tập
        val reminderCategory: LinearLayout? = findViewById(R.id.category_certificates)
        reminderCategory?.setOnClickListener {
            val intent = Intent(this, vn.edu.tlu.cse.ht1.lequocthinh.kdtm.service.ReminderActivity::class.java)
            startActivity(intent)

        }
    }

    private fun loadCourses() {
        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.Main).launch {
            try {
                allCourses = withContext(Dispatchers.IO) {
                    FirebaseService.getAllCourses()
                }

                val currentUser = FirebaseService.getCurrentUser()
                val user = if (currentUser != null) {
                    withContext(Dispatchers.IO) {
                        FirebaseService.getUserById(currentUser.uid)
                    }
                } else null

                val recommendedCourses =
                    CourseRecommendationService.getRecommendedCourses(allCourses, user)
                val popularCourses =
                    CourseRecommendationService.getPopularCourses(allCourses)
                val freeCourses =
                    CourseRecommendationService.getFreeCourses(allCourses)

                updateRecyclerViews(recommendedCourses, popularCourses, freeCourses)

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun updateRecyclerViews(
        recommended: List<Course>,
        popular: List<Course>,
        free: List<Course>
    ) {
        if (recommended.isNotEmpty()) {
            recommendedTitle.visibility = View.VISIBLE
            recommendedRecyclerView.visibility = View.VISIBLE
            recommendedRecyclerView.adapter = CourseAdapter(recommended) { course ->
                openCourseDetail(course)
            }
        } else {
            recommendedTitle.visibility = View.GONE
            recommendedRecyclerView.visibility = View.GONE
        }

        if (popular.isNotEmpty()) {
            popularTitle.visibility = View.VISIBLE
            popularRecyclerView.visibility = View.VISIBLE
            popularRecyclerView.adapter = CourseAdapter(popular) { course ->
                openCourseDetail(course)
            }
        } else {
            popularTitle.visibility = View.GONE
            popularRecyclerView.visibility = View.GONE
        }

        if (free.isNotEmpty()) {
            freeTitle.visibility = View.VISIBLE
            freeRecyclerView.visibility = View.VISIBLE
            freeRecyclerView.adapter = CourseAdapter(free) { course ->
                openCourseDetail(course)
            }
        } else {
            freeTitle.visibility = View.GONE
            freeRecyclerView.visibility = View.GONE
        }
    }

    private fun openCourseDetail(course: Course) {
        val intent = Intent(this, CourseDetailActivity::class.java)
        intent.putExtra("courseId", course.id)
        startActivity(intent)
    }
}
