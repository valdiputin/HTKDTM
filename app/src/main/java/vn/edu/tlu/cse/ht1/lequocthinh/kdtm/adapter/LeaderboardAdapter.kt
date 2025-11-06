package vn.edu.tlu.cse.ht1.lequocthinh.kdtm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.R
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.model.UserLeaderboard

class LeaderboardAdapter(private val userList: List<UserLeaderboard>) :
    RecyclerView.Adapter<LeaderboardAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRank: TextView = itemView.findViewById(R.id.tvRank)
        val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        val tvScore: TextView = itemView.findViewById(R.id.tvScore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_leaderboard, parent, false)
        return UserViewHolder(itemView)
    }

    override fun getItemCount() = userList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]

        holder.tvRank.text = (position + 1).toString()
        holder.tvUserName.text = currentUser.name
        holder.tvScore.text = "Điểm: ${currentUser.score}"

        // Đổi màu cho Top 3 (Tùy chọn)
        when (position) {
            0 -> holder.tvRank.setTextColor(android.graphics.Color.parseColor("#FFD700")) // Vàng
            1 -> holder.tvRank.setTextColor(android.graphics.Color.parseColor("#C0C0C0")) // Bạc
            2 -> holder.tvRank.setTextColor(android.graphics.Color.parseColor("#CD7F32")) // Đồng
            else -> holder.tvRank.setTextColor(android.graphics.Color.parseColor("#333333"))
        }
    }
}