package vn.edu.tlu.cse.ht1.lequocthinh.kdtm

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.R

class ClassingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classing)

        val cardIds = listOf(
            R.id.course_card_1,
            R.id.course_card_2,
            R.id.course_card_3,
            R.id.course_card_4,
            R.id.course_card_5,
            R.id.course_card_6,
            R.id.course_card_7,
            R.id.course_card_8
        )

        cardIds.forEach { cardId ->
            findViewById<CardView>(cardId)?.setOnClickListener {
                val intent = Intent(this, CourseDetailActivity::class.java)
                startActivity(intent)
            }
        }
    }
}