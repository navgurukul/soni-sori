package org.navgurukul.learn.courses.repository

import org.navgurukul.learn.courses.db.models.Course
import org.navgurukul.learn.courses.db.models.Exercise

interface CoursesDataCallback<T> {
    fun onSuccess(data: List<T>)
    fun onError(exception: Exception)
}

interface DataRepository<T> {
    fun fetchData(callback: CoursesDataCallback<T>)
    fun saveCourses(courses: List<T>)
}

interface ExerciseRepository :
    DataRepository<Exercise>
interface CourseRepository :
    DataRepository<Course>

