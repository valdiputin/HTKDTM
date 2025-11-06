package vn.edu.tlu.cse.ht1.lequocthinh.kdtm

import android.content.Intent // <-- Đã import Intent
import android.os.Bundle
// import android.view.View // Không cần thiết nếu dùng lambda
import com.google.android.material.floatingactionbutton.FloatingActionButton
// XÓA DÒNG NÀY:
// THÊM DÒNG NÀY:
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.service.LeaderboardActivity

class ClassdanghocActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classdanghoc)
        setupBottomNavigation(R.id.nav_class)

        // ===== PHẦN CODE SỬA LỖI =====

        // 1. Tìm nút (kiểu ? để báo là có thể null)
        val btnXepHang: FloatingActionButton? = findViewById(R.id.btn_xep_hang)

        // 2. Dùng safe call (?.) và chuyển sang lambda (dấu { }) cho gọn
        btnXepHang?.setOnClickListener {
            // 3. Hành động sẽ xảy ra khi bấm nút:
            // Tạo một Intent để chuyển sang LeaderboardActivity
            val intent = Intent(this, LeaderboardActivity::class.java)
            startActivity(intent)
        }

        // ===== KẾT THÚC PHẦN CODE SỬA LỖI =====
    }
}