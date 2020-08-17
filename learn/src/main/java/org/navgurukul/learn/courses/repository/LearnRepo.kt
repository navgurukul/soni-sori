package org.navgurukul.learn.courses.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.navgurukul.learn.courses.db.CourseDao
import org.navgurukul.learn.courses.db.ExerciseDao
import org.navgurukul.learn.courses.db.models.Course
import org.navgurukul.learn.courses.network.CoursesResponseContainer
import org.navgurukul.learn.courses.network.SaralCoursesApi
import org.navgurukul.learn.courses.network.NetworkBoundResource

class LearnRepo(
    private val courseApi: SaralCoursesApi,
    private val courseDao: CourseDao,
    private val exerciseDao: ExerciseDao
) {

    fun getCoursesData(): LiveData<List<Course>?> {
        return object : NetworkBoundResource<List<Course>, CoursesResponseContainer>() {
            override suspend fun saveCallResult(data: CoursesResponseContainer) {
                courseDao.insertCourses(data.availableCourses)
            }

            override fun shouldFetch(data: List<Course>?): Boolean {
                //if network avail && shared pref
                return data == null && data?.isEmpty()!!
            }

            override suspend fun makeApiCallAsync(): Deferred<CoursesResponseContainer> {
                return courseApi.getCoursesAsync()
            }

            override suspend fun loadFromDb(): List<Course>? {
                return courseDao.getAllCoursesDirect()
            }
        }.asLiveData()
    }
}
