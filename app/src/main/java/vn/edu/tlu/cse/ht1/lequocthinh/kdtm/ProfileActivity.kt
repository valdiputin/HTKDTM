package vn.edu.tlu.cse.ht1.lequocthinh.kdtm

import android.content.Intent // <-- THÊM 1
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.MainActivity // <-- THÊM 2

// Kế thừa từ BaseActivity()
class ProfileActivity : BaseActivity() {

    private lateinit var profileImage: CircleImageView
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var btnLogout: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Khởi tạo views
        tvName = findViewById(R.id.tvName)
        tvEmail = findViewById(R.id.tvEmail)
        tvPhone = findViewById(R.id.tvPhone)
        btnLogout = findViewById(R.id.btnLogout)
        progressBar = findViewById(R.id.profileLoading)
        profileImage = findViewById(R.id.profile_image)

        // Khởi tạo Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Tải thông tin người dùng
        loadUserProfile()

        // -----------------------------------------------------------------
        // SỬA 3: HOÀN THIỆN LOGIC ĐĂNG XUẤT
        // -----------------------------------------------------------------
        btnLogout.setOnClickListener {
            // 1. Đăng xuất khỏi Firebase Auth
            auth.signOut()

            // 2. Tạo Intent để quay về MainActivity (màn hình chính/login)
            val intent = Intent(this, MainActivity::class.java)

            // 3. Xóa hết các Activity cũ (như Home, Profile...) khỏi bộ nhớ
            // Để người dùng không thể nhấn "Back" quay lại sau khi đăng xuất
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            // 4. Khởi chạy
            startActivity(intent)

            // 5. Đóng Activity hiện tại (ProfileActivity)
            finish()
        }

        // Gọi hàm từ lớp cha và truyền ID
        setupBottomNavigation(R.id.nav_profile)
    }

    private fun loadUserProfile() {
        progressBar.visibility = View.VISIBLE
        val user = auth.currentUser

        if (user != null) {
            tvEmail.text = user.email ?: "N/A"

            // Lấy thêm thông tin từ Realtime Database (nếu có)
            val userRef = database.getReference("Users").child(user.uid)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.child("name").getValue(String::class.java)
                    val phone = snapshot.child("phone").getValue(String::class.java)
                    // val imageUrl = snapshot.child("profileImage").getValue(String::class.java)

                    tvName.text = name ?: "Người dùng"

                    if (phone != null && phone.isNotEmpty()) {
                        tvPhone.text = phone
                        tvPhone.visibility = View.VISIBLE
                    } else {
                        tvPhone.visibility = View.GONE
                    }

                    // (Code Glide/Picasso)

                    progressBar.visibility = View.GONE
                }

                override fun onCancelled(error: DatabaseError) {
                    progressBar.visibility = View.GONE
                    // Xử lý lỗi
                }
            })
        } else {
            progressBar.visibility = View.GONE
            // Người dùng chưa đăng nhập
        }
    }
}