package org.navgurukul.learn.courses.network.retrofit

import org.koin.java.KoinJavaComponent.inject
import org.navgurukul.learn.courses.db.models.Course
import org.navgurukul.learn.courses.repository.CoursesDataCallback
import org.navgurukul.learn.courses.repository.DataRepository

class NetworkDataProvider: DataRepository<Course> {
    private val retrofit: RetrofitClient by inject()

    override fun fetchData(callback: CoursesDataCallback<Course>) {

    }

    override fun saveCourses(courses: List<Course>) {
        // NO OP: Not saving anything into remote directory
    }
}