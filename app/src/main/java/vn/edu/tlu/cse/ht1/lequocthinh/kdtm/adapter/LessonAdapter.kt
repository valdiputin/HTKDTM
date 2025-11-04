package vn.edu.tlu.cse.ht1.lequocthinh.kdtm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.R
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.model.Lesson

class LessonAdapter(
    private val lessons: List<Lesson>,
    private val onLessonClick: (Lesson) -> Unit
) : RecyclerView.Adapter<LessonAdapter.LessonViewHolder>() {

    class LessonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.lessonCard)
        val titleTextView: TextView = itemView.findViewById(R.id.lessonTitle)
        val durationTextView: TextView = itemView.findViewById(R.id.lessonDuration)
        val completedIcon: ImageView = itemView.findViewById(R.id.completedIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lesson, parent, false)
        return LessonViewHolder(view)
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        val lesson = lessons[position]

        holder.titleTextView.text = lesson.title
        holder.durationTextView.text = lesson.duration.ifEmpty { "N/A" }

        // Show completed icon if lesson is completed
        holder.completedIcon.visibility = if (lesson.isCompleted) View.VISIBLE else View.GONE

        holder.cardView.setOnClickListener {
            onLessonClick(lesson)
        }
    }

    override fun getItemCount() = lessons.size
}

