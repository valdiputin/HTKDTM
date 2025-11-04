package vn.edu.tlu.cse.ht1.lequocthinh.kdtm

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

// -----------------------------------------------------------------
// SỬA 1: KẾ THỪA TỪ BaseActivity()
// -----------------------------------------------------------------
class ProfileActivity : BaseActivity() {

    private lateinit var profileImage: CircleImageView
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var btnLogout: Button
    private lateinit var progressBar: ProgressBar // Đảm bảo ID là "profileLoading" trong XML

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
        progressBar = findViewById(R.id.profileLoading) // Sửa ID này nếu bạn đặt tên khác
        profileImage = findViewById(R.id.profile_image)

        // Khởi tạo Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Tải thông tin người dùng
        loadUserProfile()

        // Xử lý đăng xuất
        btnLogout.setOnClickListener {
            auth.signOut()
            // Chuyển về màn hình Login (ví dụ)
            // val intent = Intent(this, LoginActivity::class.java)
            // intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // startActivity(intent)
            // finish()
        }

        // -----------------------------------------------------------------
        // SỬA 2: GỌI HÀM TỪ LỚP CHA VÀ TRUYỀN ID
        // -----------------------------------------------------------------
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

                    // Code để tải ảnh profile dùng Glide/Picasso (nếu có)
                    // if (imageUrl != null && imageUrl.isNotEmpty()) {
                    //    Glide.with(this@ProfileActivity).load(imageUrl).into(profileImage)
                    // }

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