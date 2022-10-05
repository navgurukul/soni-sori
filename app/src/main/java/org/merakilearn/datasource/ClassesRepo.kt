package org.merakilearn.datasource

import kotlinx.coroutines.flow.*
import org.merakilearn.datasource.network.SaralApi
import org.merakilearn.datasource.network.model.Batches
import org.merakilearn.datasource.network.model.Classes
import timber.log.Timber

class ClassesRepo(
    val api: SaralApi
) {

    private val _classesFlow = MutableSharedFlow<List<Classes>?>(replay = 1)

    val classesFlow = _classesFlow.asSharedFlow()

    var lastUpdatedClasses: List<Classes>? = null

    suspend fun updateClasses() {
        try {
            val response = api.getMyClassesAsync()
            lastUpdatedClasses = response
            _classesFlow.emit(response)
        } catch (ex: Exception) {
            Timber.tag(TAG).e(ex, "fetchUpcomingClassData: ")
            _classesFlow.emit(arrayListOf())
        }
    }

    suspend fun fetchClassData(classId: Int): Classes? {
        return try {
            api.fetchClassDataAsync(classId)
        } catch (ex: Exception) {
            Timber.tag(TAG).e(ex, "fetchUpcomingClassData: ")
            null
        }
    }

    suspend fun getEnrolledBatches():List<Batches>?{
        val res = api.getEnrolledBatches()
        if(res.isSuccessful)
            return res.body()
        return null
    }
    suspend fun enrollToClass(classId: Int, enrolled: Boolean): Boolean {
        return try {
            if (enrolled) {
                api.logOutToClassAsync(classId)
                updateEnrollStatus(classId, false)
            } else {
                api.enrollToClassAsync(classId, mutableMapOf())
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