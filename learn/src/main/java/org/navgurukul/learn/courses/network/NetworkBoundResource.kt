package org.navgurukul.learn.courses.network

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

@Deprecated("Use networkBoundResourceFlow instead")
abstract class NetworkBoundResource<ResultType, RequestType> {
    private val result = MutableLiveData<ResultType?>()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val dbSource = loadFromDb()
            if (shouldFetch(dbSource)) {
                fetchFromNetwork(dbSource)
            } else {
                result.postValue(dbSource)
            }
        }
    }

    private suspend fun fetchFromNetwork(dbSource: ResultType?) {
        dbSource?.let(result::postValue)
        try {
            val data = makeApiCallAsync()
            saveCallResult(data)
            result.postValue(loadFromDb())
        } catch (e: Exception) {
            e.printStackTrace()
            result.postValue(null)
        }
    }

    protected abstract suspend fun loadFromDb(): ResultType?

    protected abstract fun shouldFetch(data: ResultType?): Boolean

    protected abstract suspend fun makeApiCallAsync(): RequestType

    protected abstract suspend fun saveCallResult(data: RequestType)

    fun asLiveData() = result

}

fun <ResultType, RequestType> networkBoundResourceFlow(
    loadFromDb: suspend () -> ResultType?,
    shouldFetch: (ResultType?) -> Boolean,
    makeApiCallAsync: suspend () -> RequestType,
    saveCallResult: (RequestType) -> Unit
): Flow<ResultType?> = flow {
    val dbSource = withContext(Dispatchers.IO) { loadFromDb() }
    if (dbSource != null && (dbSource !is Collection<*> || (dbSource as Collection<*>).isNotEmpty())) {
        emit(dbSource)
    }
    if (shouldFetch(dbSource)) {
        val data = makeApiCallAsync()
        withContext(Dispatchers.IO) { saveCallResult(data) }
        emit(loadFromDb())
    }
}.catch { e ->
    e.printStackTrace()
    emit(null)
}