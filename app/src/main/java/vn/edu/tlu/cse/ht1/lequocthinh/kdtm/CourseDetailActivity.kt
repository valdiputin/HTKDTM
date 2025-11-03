package vn.edu.tlu.cse.ht1.lequocthinh.kdtm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.R

class CourseDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_detail)

        val lessonsContainer = findViewById<LinearLayout>(R.id.lessons_container)

        val completedLessons = 5
        val inProgressLesson = 1
        val upcomingLessons = 4

        for (i in 1..completedLessons) {
            val lessonView = LayoutInflater.from(this).inflate(R.layout.item_lesson_completed, lessonsContainer, false)
            val lessonTitle = lessonView.findViewById<TextView>(R.id.lesson_title)
            lessonTitle.text = "Bài giảng $i"
            setupLessonClickListener(lessonView)
            lessonsContainer.addView(lessonView)
        }

        for (i in 1..inProgressLesson) {
            val lessonView = LayoutInflater.from(this).inflate(R.layout.item_lesson_inprogress, lessonsContainer, false)
            val lessonTitle = lessonView.findViewById<TextView>(R.id.lesson_title)
            lessonTitle.text = "Bài giảng ${i + completedLessons}"
            setupLessonClickListener(lessonView)
            lessonsContainer.addView(lessonView)
        }

        for (i in 1..upcomingLessons) {
            val lessonView = LayoutInflater.from(this).inflate(R.layout.item_lesson_upcoming, lessonsContainer, false)
            val lessonTitle = lessonView.findViewById<TextView>(R.id.lesson_title)
            lessonTitle.text = "Bài giảng ${i + completedLessons + inProgressLesson}"
            setupLessonClickListener(lessonView)
            lessonsContainer.addView(lessonView)
        }
    }

    private fun setupLessonClickListener(lessonView: View) {
        val detailsButton = lessonView.findViewById<Button>(R.id.btn_details)
        val detailsLayout = lessonView.findViewById<LinearLayout>(R.id.lesson_details)

        detailsButton.setOnClickListener {
            if (detailsLayout.visibility == View.VISIBLE) {
                detailsLayout.visibility = View.GONE
            } else {
                detailsLayout.visibility = View.VISIBLE
            }
        }
    }
}
