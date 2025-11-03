package vn.edu.tlu.cse.ht1.lequocthinh.kdtm.model

data class Course(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val instructor: String = "",
    val price: String = "Free",
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val category: String = "",
    val level: String = "",
    val classCount: Int = 0,
    val lessons: List<Lesson> = emptyList(),
    val isRecommended: Boolean = false,
    val studentCount: Int = 0
)

