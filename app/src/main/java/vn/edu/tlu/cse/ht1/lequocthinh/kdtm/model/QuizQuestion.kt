package vn.edu.tlu.cse.ht1.lequocthinh.kdtm.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuizQuestion(
        val question: String,
        val options: List<String>,
        val correctAnswerIndex: Int
) : Parcelable