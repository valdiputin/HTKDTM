package vn.edu.tlu.cse.ht1.lequocthinh.kdtm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.R
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.model.Course

class CourseAdapter(
    private val courses: List<Course>,
    private val onCourseClick: (Course) -> Unit
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.courseCard)
        val imageView: ImageView = itemView.findViewById(R.id.courseImage)
        val titleTextView: TextView = itemView.findViewById(R.id.courseTitle)
        val classCountTextView: TextView = itemView.findViewById(R.id.classCount)
        val priceTextView: TextView = itemView.findViewById(R.id.coursePrice)
        val ratingTextView: TextView = itemView.findViewById(R.id.courseRating)
        val button: TextView = itemView.findViewById(R.id.btnStart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courses[position]

        // Set image
        if (course.imageUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(course.imageUrl)
                .into(holder.imageView)
        } else {
            // Use default drawable if no URL
            holder.imageView.setImageResource(R.drawable.mastering_ui_ux)
        }

        holder.titleTextView.text = course.title
        holder.classCountTextView.text = "${course.classCount} Class, ${course.level}"
        holder.priceTextView.text = course.price
        
        // Set rating
        if (course.rating > 0) {
            holder.ratingTextView.text = "${course.rating} ⭐ (${course.reviewCount})"
        } else {
            holder.ratingTextView.text = ""
        }

        holder.cardView.setOnClickListener {
            onCourseClick(course)
        }

        holder.button.setOnClickListener {
            onCourseClick(course)
        }
    }

    override fun getItemCount() = courses.size
}

