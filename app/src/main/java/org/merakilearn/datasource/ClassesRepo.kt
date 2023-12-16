package org.merakilearn.datasource

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.merakilearn.datasource.network.SaralApi
import org.merakilearn.datasource.network.model.Batches
import org.merakilearn.datasource.network.model.Classes
import org.navgurukul.learn.courses.network.SaralCoursesApi
import org.navgurukul.learn.courses.network.wrapper.BaseRepo
import org.navgurukul.learn.courses.network.wrapper.Resource
import timber.log.Timber

class ClassesRepo(
    val saralApi: SaralApi,
    val coursesApi:SaralCoursesApi
) : BaseRepo() {

    private val _classesFlow = MutableSharedFlow<List<Classes>?>(replay = 1)

    val classesFlow = _classesFlow.asSharedFlow()

    var lastUpdatedClasses: List<Classes>? = null

//    suspend fun updateClasses() {    //function is not in use
//        try {
//            val response = api.getMyClassesAsync()
//            lastUpdatedClasses = response
//            _classesFlow.emit(response)
//        } catch (ex: Exception) {
//            Timber.tag(TAG).e(ex, "fetchUpcomingClassData: ")
//            _classesFlow.emit(arrayListOf())
//        }
//    }

    suspend fun fetchClassData(classId: Int): Classes? {
        return try {
            saralApi.fetchClassDataAsync(classId)
        } catch (ex: Exception) {
            Timber.tag(TAG).e(ex, "fetchUpcomingClassData: ")
            null
        }
    }

    suspend fun getEnrolledBatches(): Resource<List<Batches>>? {
        return try {
            safeApiCall { saralApi.getEnrolledBatches() }
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

    suspend fun enrollToClass(classId: Int, enrolled: Boolean): Boolean {
        return try {
            if (enrolled) {
                coursesApi.logOutToClassAsync(classId,true)
                updateEnrollStatus(classId, false)
            } else {
                coursesApi.enrollToClassAsync(classId, mutableMapOf(),false)
                updateEnrollStatus(classId, true)
            }
        } catch (ex: Exception) {
            Timber.tag(TAG).e(ex, "enrollToClass: ")
            false
        }
    }

    private suspend fun updateEnrollStatus(classId: Int, enrolled: Boolean): Boolean {
        val classes = lastUpdatedClasses ?: return true
        classes.forEachIndexed loop@ { index, classItem ->
            if (classId == classItem.id) {
                val updatedClass = classItem.copy(enrolled = enrolled)
                lastUpdatedClasses = mutableListOf(*classes.toTypedArray()).apply {
                    this[index] = updatedClass
                }
                _classesFlow.emit(lastUpdatedClasses)

                return@loop
            }
        }
        return true
    }

    companion object {
        private const val TAG = "ClassesRepo"
    }
}