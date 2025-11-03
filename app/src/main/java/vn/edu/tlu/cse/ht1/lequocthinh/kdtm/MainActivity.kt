package vn.edu.tlu.cse.ht1.lequocthinh.kdtm

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.service.FirebaseService

class MainActivity : AppCompatActivity() {
    
    private lateinit var edtEmail: TextInputEditText
    private lateinit var edtPassword: TextInputEditText
    private lateinit var btnLogin: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        setupViews()
        
        // Check if user is already logged in
        if (FirebaseService.getCurrentUser() != null) {
            navigateToHome()
        }
    }
    
    private fun setupViews() {
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        btnLogin = findViewById(R.id.btnLogin)
        
        val btnRegister = findViewById<Button>(R.id.tvSignInHeader)
        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        
        btnLogin.setOnClickListener {
            login()
        }
    } // Kết thúc hàm onCreate
    
    private fun login() {
        val email = edtEmail.text?.toString()?.trim() ?: ""
        val password = edtPassword.text?.toString() ?: ""
        
        if (email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show()
            return
        }
        
        btnLogin.isEnabled = false
        
        CoroutineScope(Dispatchers.Main).launch {
            val result = FirebaseService.login(email, password)
            
            result.onSuccess {
                Toast.makeText(this@MainActivity, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                navigateToHome()
            }.onFailure { exception ->
                Toast.makeText(this@MainActivity, "Đăng nhập thất bại: ${exception.message}", Toast.LENGTH_LONG).show()
                btnLogin.isEnabled = true
            }
        }
    }
    
    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
