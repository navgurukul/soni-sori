package org.merakilearn.scratchjr

/**
 * Exception thrown when there was a problem connecting to or accessing the database.
 *
 * @author markroth8
 */
class DatabaseException

    : Exception {
    constructor(message: String?) : super(message)

    constructor(message: String?, t: Throwable?) : super(message, t)

    constructor(t: Throwable?) : super(t)

    companion object {
        private const val serialVersionUID = 2762109849013951310L
    }
}