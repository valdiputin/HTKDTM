package vn.edu.tlu.cse.ht1.lequocthinh.kdtm

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast // <-- THÊM IMPORT
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText // <-- THÊM IMPORT
import com.google.firebase.auth.FirebaseAuth // <-- THÊM IMPORT
import com.google.firebase.auth.ktx.auth // <-- THÊM IMPORT
import com.google.firebase.ktx.Firebase // <-- THÊM IMPORT

class MainActivity : AppCompatActivity() {

    // 1. Khai báo biến Firebase Auth
    private lateinit var auth: FirebaseAuth

    // 2. Khai báo biến cho EditTexts
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main) // (Tệp layout XML Đăng nhập của bạn)
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

        // --- Nút Đăng Ký (Code của bạn đã đúng) ---
        val btnRegister = findViewById<Button>(R.id.tvSignInHeader)
        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // --- Nút Đăng Nhập (CẬP NHẬT LOGIC) ---
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        btnLogin.setOnClickListener {
            // Thay vì chuyển trang ngay, hãy gọi hàm performLogin
            performLogin()
        }
    } // Kết thúc hàm onCreate

    /**
     * Hàm này đọc email/pass và gọi Firebase để xác thực
     */
    private fun performLogin() {
        // 5. Lấy text từ EditTexts
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        // 6. Kiểm tra xem có trống không
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show()
            return // Dừng hàm nếu trống
        }

        // 7. Gọi hàm đăng nhập của Firebase
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Đăng nhập THÀNH CÔNG
                    Toast.makeText(baseContext, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()

                    // Bây giờ mới chuyển sang HomeActivity
                    val intent = Intent(this, HomeActivity::class.java)
                    // Xóa các Activity cũ
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish() // Đóng MainActivity

                } else {
                    // Đăng nhập THẤT BẠI
                    Toast.makeText(
                        baseContext,
                        "Đăng nhập thất bại: ${task.exception?.message}", // Hiển thị lỗi
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}