package vn.edu.tlu.cse.ht1.lequocthinh.kdtm

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.service.FirebaseService

class RegisterActivity : AppCompatActivity() {
    
    private lateinit var edtName: TextInputEditText
    private lateinit var edtEmail: TextInputEditText
    private lateinit var edtPassword: TextInputEditText
    private lateinit var btnRegister: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        
        setupViews()
    }
    
    private fun setupViews() {
        edtName = findViewById(R.id.edtName)
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        btnRegister = findViewById(R.id.btnRegister)
        
        findViewById<Button>(R.id.tvLoginNav).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        
        btnRegister.setOnClickListener {
            register()
        }
    }
    
    private fun register() {
        val name = edtName.text?.toString()?.trim() ?: ""
        val email = edtEmail.text?.toString()?.trim() ?: ""
        val password = edtPassword.text?.toString() ?: ""
        
        if (name.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập họ tên", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (password.isEmpty() || password.length < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show()
            return
        }
        
        btnRegister.isEnabled = false
        
        CoroutineScope(Dispatchers.Main).launch {
            val result = FirebaseService.register(email, password, name)
            
            result.onSuccess {
                Toast.makeText(this@RegisterActivity, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                navigateToHome()
            }.onFailure { exception ->
                Toast.makeText(this@RegisterActivity, "Đăng ký thất bại: ${exception.message}", Toast.LENGTH_LONG).show()
                btnRegister.isEnabled = true
            }
        }
    }
    
    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}

