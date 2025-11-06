// Tên file: vn/edu/tlu/cse/ht1/lequocthinh/kdtm/service/LeaderboardActivity.kt
package vn.edu.tlu.cse.ht1.lequocthinh.kdtm.service // Package đúng

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
// import androidx.appcompat.app.AppCompatActivity // Bỏ dòng này
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.BaseActivity // 👈 THÊM: Import BaseActivity
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.R


import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.adapter.LeaderboardAdapter
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.model.UserLeaderboard

// class LeaderboardActivity : AppCompatActivity() { // Bỏ dòng này
class LeaderboardActivity : BaseActivity() { // 👈 SỬA: Kế thừa từ BaseActivity

    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingBar: ProgressBar
    private lateinit var adapter: LeaderboardAdapter
    private val userList = mutableListOf<UserLeaderboard>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        // Tìm các view
        recyclerView = findViewById(R.id.recyclerViewLeaderboard)
        loadingBar = findViewById(R.id.leaderboardLoading)

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = LeaderboardAdapter(userList)
        recyclerView.adapter = adapter

        // Bắt đầu lấy dữ liệu
        fetchLeaderboardData()
    }

    private fun fetchLeaderboardData() {
        loadingBar.visibility = View.VISIBLE
        val db = Firebase.firestore

        db.collection("users")
            .orderBy("completedCoursesCount", Query.Direction.DESCENDING) // Sắp xếp theo điểm
            .limit(20) // Lấy 20 người cao nhất
            .get()
            .addOnSuccessListener { documents ->
                userList.clear() // Xóa danh sách cũ

                for (document in documents) {
                    // Chuyển dữ liệu từ Firestore sang model UserLeaderboard
                    val name = document.getString("name") ?: "Người dùng"
                    // Dòng 60 (gây crash):
                    val score = document.getLong("completedCoursesCount")?.toInt() ?: 0

                    userList.add(UserLeaderboard(name, score))
                }

                // Báo cho adapter biết dữ liệu đã thay đổi
                adapter.notifyDataSetChanged()
                loadingBar.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                Log.e("LeaderboardActivity", "Lỗi khi lấy dữ liệu: ", exception)
                loadingBar.visibility = View.GONE
                // TODO: Hiển thị thông báo lỗi cho người dùng
            }
    }
}