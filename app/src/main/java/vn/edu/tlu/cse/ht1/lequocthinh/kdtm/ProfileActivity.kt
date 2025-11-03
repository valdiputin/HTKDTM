package vn.edu.tlu.cse.ht1.lequocthinh.kdtm

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.service.FirebaseService
import java.text.DecimalFormat

class ProfileActivity : AppCompatActivity() {
    
    private lateinit var profileImage: CircleImageView
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var progressBar: ProgressBar
    
    // Dashboard views
    private lateinit var tvScore: TextView
    private lateinit var tvCompletionPercent: TextView
    private lateinit var tvStudyTime: TextView
    private lateinit var tvAchievements: TextView
    private lateinit var progressBarCompletion: ProgressBar
    private lateinit var tvCompletedCourses: TextView
    private lateinit var tvEnrolledCourses: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        setupViews()
        loadUserData()
    }
    
    private fun setupViews() {
        profileImage = findViewById(R.id.profile_image)
        tvName = findViewById(R.id.tvName)
        tvEmail = findViewById(R.id.tvEmail)
        tvPhone = findViewById(R.id.tvPhone)
        progressBar = findViewById(R.id.progressBar)
        
        // Dashboard views
        tvScore = findViewById(R.id.tvScore)
        tvCompletionPercent = findViewById(R.id.tvCompletionPercent)
        tvStudyTime = findViewById(R.id.tvStudyTime)
        tvAchievements = findViewById(R.id.tvAchievements)
        progressBarCompletion = findViewById(R.id.progressBarCompletion)
        tvCompletedCourses = findViewById(R.id.tvCompletedCourses)
        tvEnrolledCourses = findViewById(R.id.tvEnrolledCourses)
        
        // Logout button
        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            logout()
        }
    }
    
    private fun logout() {
        FirebaseService.logout()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    
    private fun loadUserData() {
        val currentUser = FirebaseService.getCurrentUser()
        
        if (currentUser == null) {
            // Redirect to login
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
        
        progressBar.visibility = View.VISIBLE
        
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val user = withContext(Dispatchers.IO) {
                    FirebaseService.getUserById(currentUser.uid)
                }
                
                user?.let {
                    displayUserInfo(it)
                    calculateAndDisplayProgress(it)
                }
                
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }
    
    private fun displayUserInfo(user: vn.edu.tlu.cse.ht1.lequocthinh.kdtm.model.User) {
        tvName.text = user.name.ifEmpty { "Người dùng" }
        tvEmail.text = user.email.ifEmpty { "Chưa cập nhật" }
        // Phone number would need to be added to User model if needed
    }
    
    private fun calculateAndDisplayProgress(user: vn.edu.tlu.cse.ht1.lequocthinh.kdtm.model.User) {
        // Load all courses to calculate completion
        CoroutineScope(Dispatchers.Main).launch {
            val allCourses = withContext(Dispatchers.IO) {
                FirebaseService.getAllCourses()
            }
            
            // Calculate completion percentage
            val enrolledCount = user.enrolledCourses.size
            val completedCount = user.completedCourses.size
            val completionPercent = if (enrolledCount > 0) {
                (completedCount.toFloat() / enrolledCount.toFloat() * 100).toInt()
            } else {
                0
            }
            
            // Calculate average score
            val scores = user.studyHistory.map { it.score }.filter { it > 0 }
            val averageScore = if (scores.isNotEmpty()) {
                scores.average()
            } else {
                0.0
            }
            
            // Calculate total study time (in hours)
            val totalMinutes = user.studyHistory.sumOf { it.timeSpent }
            val totalHours = totalMinutes / 60.0
            
            // Count achievements (completed courses, high scores, etc.)
            val achievementsCount = completedCount + 
                user.studyHistory.count { it.score >= 8.0 }
            
            // Update UI
            val df = DecimalFormat("#.#")
            
            tvScore.text = "Điểm trung bình: ${df.format(averageScore)}/10"
            tvCompletionPercent.text = "$completionPercent% hoàn thành"
            
            // Set progress bar
            progressBarCompletion.max = 100
            progressBarCompletion.progress = completionPercent
            
            tvStudyTime.text = "Thời gian học: ${df.format(totalHours)} giờ"
            tvAchievements.text = "$achievementsCount thành tích"
            tvCompletedCourses.text = "Đã hoàn thành: $completedCount khóa học"
            tvEnrolledCourses.text = "Đã đăng ký: $enrolledCount khóa học"
        }
    }
}
