package vn.edu.tlu.cse.ht1.lequocthinh.kdtm.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "student", // "student" or "lecturer"
    val completedCourses: List<String> = emptyList(), // Course IDs
    val enrolledCourses: List<String> = emptyList(),
    val interests: List<String> = emptyList(),
    val studyHistory: List<StudyProgress> = emptyList()
)

data class StudyProgress(
    val courseId: String = "",
    val lessonId: String = "",
    val completedAt: Long = 0,
    val score: Double = 0.0,
    val timeSpent: Long = 0 // in minutes
)

