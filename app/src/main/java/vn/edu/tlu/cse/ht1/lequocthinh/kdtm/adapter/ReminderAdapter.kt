package vn.edu.tlu.cse.ht1.lequocthinh.kdtm.service

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.R
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.model.Reminder

class ReminderAdapter(
    // Tạo một callback để báo cho Activity biết khi nào nút xóa được nhấn
    private val onDeleteClicked: (Reminder) -> Unit
) : ListAdapter<Reminder, ReminderAdapter.ViewHolder>(ReminderDiffCallback()) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val timeText: TextView = view.findViewById(R.id.itemTimeText)
        val deleteButton: Button = view.findViewById(R.id.btnItemDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reminder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reminder = getItem(position)

        val timeString = "⏰ ${"%02d".format(reminder.hour)}:${"%02d".format(reminder.minute)}"
        holder.timeText.text = timeString

        // Đặt listener cho nút xóa
        holder.deleteButton.setOnClickListener {
            onDeleteClicked(reminder) // Gọi callback, truyền reminder cần xóa
        }
    }
}

class ReminderDiffCallback : DiffUtil.ItemCallback<Reminder>() {
    override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
        return oldItem == newItem
    }
}