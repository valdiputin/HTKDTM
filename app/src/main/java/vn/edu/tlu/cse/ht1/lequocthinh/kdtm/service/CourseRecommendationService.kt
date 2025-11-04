package vn.edu.tlu.cse.ht1.lequocthinh.kdtm.service

import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.model.Course
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.model.User

object CourseRecommendationService {
    
    /**
     * AI đề xuất khóa học dựa trên:
     * - Khóa học đã hoàn thành
     * - Sở thích người dùng
     * - Xu hướng học tập
     */
    fun getRecommendedCourses(
        allCourses: List<Course>,
        user: User?
    ): List<Course> {
        if (user == null || allCourses.isEmpty()) {
            // Nếu chưa đăng nhập hoặc không có user, trả về courses được đánh dấu recommended
            return allCourses.filter { it.isRecommended }.take(10)
        }
        
        val recommendedCourses = mutableListOf<Pair<Course, Double>>()
        
        for (course in allCourses) {
            // Bỏ qua các khóa học đã hoàn thành
            if (user.completedCourses.contains(course.id)) {
                continue
            }
            
            var score = 0.0
            
            // 1. Điểm dựa trên sở thích (interests)
            user.interests.forEach { interest ->
                if (course.category.contains(interest, ignoreCase = true) ||
                    course.title.contains(interest, ignoreCase = true) ||
                    course.description.contains(interest, ignoreCase = true)) {
                    score += 3.0
                }
            }
            
            // 2. Điểm dựa trên khóa học đã hoàn thành (recommend courses cùng category hoặc level)
            val completedCourses = allCourses.filter { user.completedCourses.contains(it.id) }
            completedCourses.forEach { completed ->
                if (course.category == completed.category) {
                    score += 2.0
                }
                if (course.level == completed.level) {
                    score += 1.5
                }
                // Nếu cùng instructor
                if (course.instructor == completed.instructor) {
                    score += 1.0
                }
            }
            
            // 3. Điểm dựa trên xu hướng học tập (popularity)
            if (course.isRecommended) {
                score += 2.0
            }
            // Khóa học có rating cao
            if (course.rating >= 4.5) {
                score += 1.5
            }
            // Khóa học có nhiều học viên
            if (course.studentCount > 1000) {
                score += 1.0
            }
            
            // 4. Khóa học miễn phí được ưu tiên
            if (course.price == "Free" || course.price.contains("Free", ignoreCase = true)) {
                score += 0.5
            }
            
            if (score > 0) {
                recommendedCourses.add(Pair(course, score))
            }
        }
        
        // Sắp xếp theo điểm số giảm dần và lấy top 10
        return recommendedCourses
            .sortedByDescending { it.second }
            .take(10)
            .map { it.first }
    }
    
    /**
     * Lấy khóa học phổ biến (Popular)
     */
    fun getPopularCourses(allCourses: List<Course>): List<Course> {
        return allCourses
            .sortedByDescending { it.studentCount }
            .take(10)
    }
    
    /**
     * Lấy khóa học miễn phí (Freemium)
     */
    fun getFreeCourses(allCourses: List<Course>): List<Course> {
        return allCourses
            .filter { it.price == "Free" || it.price.contains("Free", ignoreCase = true) }
            .sortedByDescending { it.rating }
            .take(10)
    }
}

