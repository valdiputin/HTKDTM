package vn.edu.tlu.cse.ht1.lequocthinh.kdtm

// --- THÊM CÁC IMPORT CẦN THIẾT CHO NGÔN NGỮ ---
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import java.util.Locale
// ---------------------------------------------

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    // 1. Khai báo biến Firebase Auth
    private lateinit var auth: FirebaseAuth

    // 2. Khai báo biến cho EditTexts
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {

        // VÔ CÙNG QUAN TRỌNG: GỌI loadLocale() TRƯỚC HẾT MỌI THỨ
        loadLocale()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 3. Khởi tạo Firebase Auth
        auth = Firebase.auth

        // 4. Ánh xạ EditTexts
        emailEditText = findViewById(R.id.edtEmail)
        passwordEditText = findViewById(R.id.edtPassword)

        // --- Nút Đăng Ký ---
        val btnRegister = findViewById<Button>(R.id.tvSignInHeader)
        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // --- Nút Đăng Nhập ---
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        btnLogin.setOnClickListener {
            performLogin()
        }
    } // Kết thúc hàm onCreate

    /**
     * Hàm này đọc email/pass và gọi Firebase để xác thực
     */
    private fun performLogin() {
        // ... (Giữ nguyên logic performLogin của bạn) ...
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(
                        baseContext,
                        "Đăng nhập thất bại: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    // --- CÁC HÀM XỬ LÝ NGÔN NGỮ (COPY TỪ BaseActivity.java) ---

    /**
     * Tải ngôn ngữ đã lưu từ SharedPreferences và áp dụng.
     */
    fun loadLocale() {
        // Đảm bảo không có lỗi chính tả trong tên SharedPreferences
        val prefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val language = prefs.getString("Language", "en") ?: "en"
        setLocale(this, language)
    }

    /**
     * Cài đặt ngôn ngữ (locale) cho Context.
     */
    fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        // Cập nhật Configuration để áp dụng ngôn ngữ
        val resources: Resources = context.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}