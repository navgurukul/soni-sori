package org.merakilearn.scratchjr
import android.R
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.text.TextUtils
import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


/**
 * Manages the database connection for Scratch Jr
 *
 * @author markroth8
 */
class DatabaseManager(private val _applicationContext: Context) {
    var isOpen: Boolean = false
        private set
    private var _databaseHelper: DatabaseHelper? = null
    private var _database: SQLiteDatabase? = null

    /**
     * Open the database, creating it if it does not yet exist.
     *
     * @throws SQLException
     * if the database could be neither opened or created
     */
    @Throws(SQLException::class)
    fun open() {
        _databaseHelper = DatabaseHelper(_applicationContext, DB_NAME, null, DB_VERSION)
        _database = _databaseHelper!!.writableDatabase

        // Migrations
        try {
            _database.execSQL(_applicationContext.getString(R.string.sql_add_gift))
        } catch (e: SQLException) {
            // isgift field already exists
        }

        isOpen = true
    }

    fun close() {
        _databaseHelper!!.close()
        isOpen = false
    }

    @Throws(DatabaseException::class)
    fun clearTables() {
        exec("DELETE FROM PROJECTS")
        exec("DELETE FROM USERSHAPES")
        exec("DELETE FROM USERBKGS")
    }

    private class DatabaseHelper
        (private val _context: Context, name: String?, factory: CursorFactory?, version: Int) :
        SQLiteOpenHelper(_context, name, factory, version) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(_context.getString(R.string.sql_create_projects))
            Log.i(LOG_TAG, "Created table projects")

            db.execSQL(_context.getString(R.string.sql_create_usershapes))
            Log.i(LOG_TAG, "Created table usershapes")

            db.execSQL(_context.getString(R.string.sql_create_userbkgs))
            Log.i(LOG_TAG, "Created table userbkgs")

            db.execSQL(_context.getString(R.string.sql_add_gift))
            Log.i(LOG_TAG, "Created project gift field")
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            Log.w(
                LOG_TAG, "Upgrading database from version " + oldVersion + " to " + newVersion +
                        ", which currently does nothing."
            )
        }
    }

    /**
     * Execute a statement on the database and return "success" if successful or the error message if not.
     *
     * @param stmt
     * The statement to execute.
     * @return "success" if the statement was executed successfully, or the error message if not.
     * @throws DatabaseException
     * If there was an error in accessing the database.
     */
    @Throws(DatabaseException::class)
    fun exec(stmt: String): String? {
        Log.d(LOG_TAG, "exec '$stmt'")
        var result: String
        try {
            _database!!.execSQL(stmt)
            result = "success"
        } catch (e: SQLException) {
            Log.e(
                LOG_TAG,
                "Error while executing statement '$stmt'", e
            )
            result = e.message
        }
        return result
    }

    /**
     * Perform a query on the database and return the results as a JSON-encoded array.
     *
     * @param statement
     * The SQL statement to query
     * @param values
     * Ordered list of arguments to substitute
     * @return A JSONArray that contains the results of the query. Each element of the array is a JSON object with the keys as
     * column names and the values as column values (Strings).
     * @throws JSONException
     * If there was an error encoding or decoding JSON
     * @throws DatabaseException
     * If there was an error in accessing the database.
     */
    @Throws(JSONException::class, DatabaseException::class)
    fun query(statement: String, values: Array<String?>): JSONArray {
        Log.d(LOG_TAG, "query '" + statement + "', " + values.contentToString())
        val cursor: Cursor?
        try {
            cursor = _database!!.rawQuery(statement, values)
        } catch (e: IllegalStateException) {
            // The database is inaccessible - we tried to query in the background
            throw DatabaseException("Query '$statement' failed to run.")
        }
        if (cursor == null) {
            throw DatabaseException("Query '$statement' returned null cursor.")
        }
        val resultArr = JSONArray()
        if (cursor.moveToFirst()) {
            do {
                resultArr.put(getRowDataAsJsonObject(cursor))
            } while (cursor.moveToNext())
        }
        return resultArr
    }

    /**
     * Execute a statement on the database and return the id of the last inserted row.
     *
     * @param stmt
     * The SQL statement to execute
     * @param values
     * Ordered list of arguments to substitute
     * @return A string containing the id of the inserted row.
     * @throws DatabaseException
     * If there was an error in accessing the database.
     */
    @Throws(DatabaseException::class)
    fun stmt(stmt: String, values: Array<String>): String {
        Log.d(LOG_TAG, "stmt '" + stmt + "', " + values.contentToString())
        try {
            _database!!.execSQL(stmt, values)
        } catch (e: IllegalStateException) {
            // The database is inaccessible - we tried to run a statement in the background
            throw DatabaseException("Query '$stmt' failed to run.")
        }

        // get last inserted row id
        val cursor = _database!!.rawQuery("SELECT last_insert_rowid()", null)
            ?: throw DatabaseException("Query '$stmt' returned null cursor.")
        cursor.moveToFirst()
        val id = cursor.getLong(0)
        cursor.close()
        return id.toString()
    }

    @Throws(DatabaseException::class)
    fun insert(table: String?, data: JSONObject): String {
        val values: MutableList<String> = ArrayList()
        val keys: MutableList<String?> = ArrayList()
        val placeholders: MutableList<String?> = ArrayList()
        val names = data.names()
        for (i in 0 until names.length()) {
            val key = names.optString(i)
            keys.add(key)
            placeholders.add("?")
            values.add(data.optString(key))
        }
        val statement = String.format(
            "INSERT INTO %s (%s) VALUES (%s)",
            table,
            TextUtils.join(",", keys),
            TextUtils.join(",", placeholders)
        )

        return stmt(statement, values.toTypedArray<String>())
    }

    @Throws(SQLiteException::class, JSONException::class)
    private fun getRowDataAsJsonObject(cursor: Cursor): JSONObject {
        val result = JSONObject()
        val count = cursor.columnCount
        for (i in 0 until count) {
            if (!cursor.isNull(i)) {
                val columnName = cursor.getColumnName(i).lowercase()
                val value = cursor.getString(i)
                result.put(columnName, value)
            }
        }
        return result
    }

    companion object {
        private const val LOG_TAG = "ScratchJr.DBManager"
        private const val DB_NAME = "ScratchJr"
        private const val DB_VERSION = 1
    }
}