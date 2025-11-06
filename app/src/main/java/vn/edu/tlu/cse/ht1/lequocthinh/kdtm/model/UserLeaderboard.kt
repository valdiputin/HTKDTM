// vn/edu/tlu/cse/ht1/lequocthinh/kdtm/model/UserLeaderboard.kt
package vn.edu.tlu.cse.ht1.lequocthinh.kdtm.model

/**
 * Data class để biểu diễn dữ liệu của một người dùng trên bảng xếp hạng.
 */
data class UserLeaderboard(
    val name: String = "",
    val score: Int = 0
)