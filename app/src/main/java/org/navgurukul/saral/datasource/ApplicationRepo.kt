package org.navgurukul.saral.datasource

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import org.navgurukul.learn.courses.db.CoursesDatabase
import org.navgurukul.learn.courses.db.models.Course
import org.navgurukul.learn.courses.network.CoursesResponseContainer
import org.navgurukul.learn.courses.network.NetworkBoundResource
import org.navgurukul.learn.ui.common.Util
import org.navgurukul.saral.datasource.network.SaralApi
import org.navgurukul.saral.datasource.network.model.ClassesContainer
import org.navgurukul.saral.datasource.network.model.LoginRequest
import org.navgurukul.saral.datasource.network.model.MyClassContainer
import org.navgurukul.saral.util.AppUtils

class ApplicationRepo(
    private val applicationApi: SaralApi,
    private val application: Application,
    private val courseDb: CoursesDatabase
) {

    suspend fun initLoginServer(authToken: String?): Boolean {
        return try {
            val req = applicationApi.initLoginAsync(LoginRequest(authToken))
            val response = req.await()
            AppUtils.saveUserLoginResponse(response, application)
            true
        } catch (ex: Exception) {
            ex.printStackTrace()
            false
        }
    }

    fun fetchWhereYouLeftData(): LiveData<List<Course>?> {
        val courseDao = courseDb.courseDao()
        return object : NetworkBoundResource<List<Course>, CoursesResponseContainer>() {
            override suspend fun saveCallResult(data: CoursesResponseContainer) {
                courseDao.insertCourses(data.availableCourses)
            }

            override fun shouldFetch(data: List<Course>?): Boolean {
                return Util.isOnline(application) && (data == null || data.isEmpty())
            }

            override suspend fun makeApiCallAsync(): Deferred<CoursesResponseContainer> {
                return applicationApi.getCoursesAsync()
            }

            override suspend fun loadFromDb(): List<Course>? {
                val data = courseDao.getAllCoursesDirect()
                data?.forEachIndexed { index, course ->
                    course.number = (index + 1).toString()
                }
                return data
            }
        }.asLiveData()
    }

    suspend fun fetchOtherCourseData(): List<ClassesContainer.Classes>? {
        return try {
            val req = applicationApi.getRecommendedClassAsync(AppUtils.getAuthToken(application))
            val response = req.await()
            response.forEachIndexed { index, course ->
                course.number = (index + 1)
            }
            response
        } catch (ex: Exception) {
            Log.e(TAG, "fetchOtherCourseData: ", ex)
            mutableListOf()
        }
    }

    suspend fun fetchUpcomingClassData(): MutableList<ClassesContainer.Classes?>? {
        return try {
            val req = applicationApi.getUpComingClassesAsync(AppUtils.getAuthToken(application))
            val response = req.await()
            response.classes as MutableList<ClassesContainer.Classes?>?
        } catch (ex: Exception) {
            Log.e(TAG, "fetchUpcomingClassData: ", ex)
            mutableListOf()
        }

    }

    suspend fun fetchMyClassData(): MutableList<MyClassContainer.MyClass?>? {
        return try {
            val req = applicationApi.getMyClassesAsync(AppUtils.getAuthToken(application))
            val response = req.await()
            response.myClass as MutableList<MyClassContainer.MyClass?>?
        } catch (ex: Exception) {
            Log.e(TAG, "fetchUpcomingClassData: ", ex)
            mutableListOf()
        }

    }

    suspend fun enrollToClass(classId: Int, enrolled: Boolean): Boolean {
        return try {
            val req: Deferred<ResponseBody>
            if (enrolled) {
                req = applicationApi.logOutToClassAsync(AppUtils.getAuthToken(application), classId)
            } else {
                req = applicationApi.enrollToClassAsync(
                    AppUtils.getAuthToken(application), classId,
                    mutableMapOf()
                )
            }
            val response = req.await()
            true
        } catch (ex: Exception) {
            Log.e(TAG, "enrollToClass: ", ex)
            false
        }
    }

    companion object {
        private const val TAG = "ApplicationRepo"
    }
}