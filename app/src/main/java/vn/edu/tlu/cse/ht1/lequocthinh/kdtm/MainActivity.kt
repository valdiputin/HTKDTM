package vn.edu.tlu.cse.ht1.lequocthinh.kdtm

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.service.FirebaseService
import java.util.Locale

class MainActivity : AppCompatActivity() {

    // Email login
    private lateinit var edtEmail: TextInputEditText
    private lateinit var edtPassword: TextInputEditText
    private lateinit var btnLogin: Button

    // Google login
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        // Gọi loadLocale() TRƯỚC KHI setContentView
        loadLocale()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViews()

        // Firebase Auth instance
        auth = FirebaseAuth.getInstance()

        // Cấu hình đăng nhập Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1014852112031-3r80km5mimsvb13tqcbus9b206vdu9r5.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Nút đăng nhập Google
        findViewById<Button>(R.id.btnGoogleSignIn).setOnClickListener {
            signInWithGoogle()
        }

        // Nếu đã đăng nhập rồi → chuyển thẳng vào Home
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

        btnLogin.setOnClickListener { login() }
    }

    // ------------------ Đăng nhập Email/Password ------------------
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
                Toast.makeText(this@MainActivity, "Đăng nhập thành công!", Toast.LENGTH_SHORT)
                    .show()
                navigateToHome()
            }.onFailure { exception ->
                Toast.makeText(
                    this@MainActivity,
                    "Đăng nhập thất bại: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
                btnLogin.isEnabled = true
            }
        }
    }

    // ------------------ Đăng nhập Google ------------------
    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful) {
                val account = task.result
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener { task2 ->
                    if (task2.isSuccessful) {
                        Toast.makeText(this, "Đăng nhập Google thành công!", Toast.LENGTH_SHORT).show()
                        navigateToHome()
                    } else {
                        Toast.makeText(this, "Lỗi xác thực Firebase: ${task2.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this, "Đăng nhập Google thất bại!", Toast.LENGTH_SHORT).show()
            }
        }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    // ------------------ Điều hướng sang màn hình chính ------------------
    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    // ------------------ Xử lý đa ngôn ngữ ------------------
    private fun loadLocale() {
        val prefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val language = prefs.getString("Language", "en") ?: "en"
        setLocale(this, language)
    }

    private fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources: Resources = context.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}
