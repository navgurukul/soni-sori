package org.navgurukul.learn.courses.network

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*

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