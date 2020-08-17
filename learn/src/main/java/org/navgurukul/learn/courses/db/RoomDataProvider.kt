package org.navgurukul.learn.courses.db

import org.navgurukul.learn.courses.db.models.Course
import org.navgurukul.learn.courses.db.models.Exercise

class RoomDataProvider(private val coursesDatabase: CoursesDatabase) : LocalDataProvider {
    private val exerciseDao = coursesDatabase.exerciseDao()
    private val courseDao = coursesDatabase.courseDao()

    override suspend fun fetchAvailableCourses(): List<Course> {
        TODO("Not yet implemented")
    }

    override suspend fun saveAvailableCourses(courses: List<Course>) {
        TODO("Not yet implemented")
    }

    override suspend fun getExerciseForCourse(courseId: String): List<Exercise> {
        TODO("Not yet implemented")
    }

    override suspend fun saveExercise(exercises: List<Exercise>) {
        TODO("Not yet implemented")
    }
}