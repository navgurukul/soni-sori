package org.navgurukul.learn.courses.db

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import org.navgurukul.learn.courses.db.models.*
import org.navgurukul.learn.courses.db.typeadapters.Converters

const val DB_VERSION = 5

@Dao
interface PathwayDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPathways(course: List<Pathway>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPathway(course: Pathway)

    @Query("select * from pathway")
    fun getAllPathways(): List<Pathway>

    @Query("select * from pathway where id=:pathwayId")
    fun getByPathwayId(pathwayId: String): List<Pathway>
}

@Dao
interface CourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCourses(course: List<Course>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCourse(course: Course?)

    @Query("select * from pathway_course where id= :id")
    fun course(id: String): Course?

    @Query("select * from pathway_course")
    fun getAllCoursesDirect(): List<Course>?

    @Query("select * from pathway_course where id=:courseId")
    suspend fun getCourseById(courseId: String): Course

    @Query("select * from pathway_course where pathwayId=:pathwayId")
    fun getCoursesByPathwayId(pathwayId: Int): List<Course>
}

@Dao
interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExercise(course: List<Exercise?>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseAsync(course: List<Exercise?>?)

    @Query("select * from course_exercise where courseId = :courseId and lang = :lang")
    suspend fun getAllExercisesForCourse(courseId: String, lang: String): List<Exercise>

    @Query("select * from course_exercise where id = :exerciseId and lang = :lang")
    fun getExerciseById(exerciseId: String, lang: String): LiveData<Exercise>


    @Query("select * from course_exercise where courseId = :courseId and lang = :lang")
    fun getAllExercisesForCourseDirect(courseId: String, lang: String): List<Exercise>

    @Query("Update course_exercise set exerciseProgress = :exerciseProgress where id = :exerciseId")
    suspend fun markCourseExerciseCompleted(exerciseProgress: String, exerciseId: String)
}

@Dao
interface CurrentStudyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCourseExerciseCurrent(course: CurrentStudy)

    @Query("select * from user_current_study where courseId = :courseId")
    suspend fun getCurrentStudyForCourse(courseId: String?): CurrentStudy?
}

val MIGRATION_1_2 = object: Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `pathway` (`code` TEXT NOT NULL, `createdAt` TEXT NOT NULL, `description` TEXT NOT NULL, `id` INTEGER NOT NULL, `name` TEXT NOT NULL,  `logo` TEXT, PRIMARY KEY(`id`))")
    }

}

val MIGRATION_2_3 = object: Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {

        //create new table
        database.execSQL("CREATE TABLE `new_course_exercise`(" +
                " `content` TEXT," +
                " `courseId` TEXT," +
                " `githubLink` TEXT, " +
                " `id` TEXT NOT NULL," +
                " `name` TEXT," +
                " `parentExerciseId` TEXT," +
                " `reviewType` TEXT," +
                " `sequenceNum` TEXT," +
                " `slug` TEXT," +
                " `solution` TEXT," +
                " `submissionType` TEXT," +
                " `lang` TEXT NOT NULL," +
                " `courseName` TEXT," +
                " PRIMARY KEY(`id`, `lang`) )")

        //insert data from old table into new table
        database.execSQL("INSERT INTO `new_course_exercise`(" +
                " `content`," +
                " `courseId`," +
                " `githubLink`," +
                " `id` ," +
                " `name` ," +
                " `parentExerciseId` ," +
                " `reviewType` ," +
                " `sequenceNum` ," +
                " `slug` ," +
                " `solution` ," +
                " `submissionType` ," +
                " 'lang' ," +
                " `courseName` )" +
                " SELECT `content`, `courseId`, `githubLink`, `id`, `name`, `parentExerciseId`, `reviewType`, `sequenceNum`," +
                " `slug`, `solution`, `submissionType`, 'en', `courseName`  FROM `course_exercise`")

        //drop old table
        database.execSQL("DROP TABLE course_exercise")

        //rename new table to the old table name
        database.execSQL("ALTER TABLE new_course_exercise RENAME TO course_exercise")

        // Pathway Course
        database.execSQL("ALTER TABLE `pathway_course` ADD COLUMN `supportedLanguages` TEXT NOT NULL DEFAULT '[\"en\"]'")
    }

}

val MIGRATION_3_4 = object: Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Pathway
        database.execSQL("ALTER TABLE `pathway` ADD COLUMN `supportedLanguages` TEXT NOT NULL DEFAULT '[{\"code\": \"en\", \"label\": \"English\"}]'")
    }

}

val MIGRATION_4_5 = object: Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {

        //drop old table
        database.execSQL("DROP TABLE course_exercise")

        //create new table
        database.execSQL("CREATE TABLE `course_exercise`(" +
                " `content` TEXT NOT NULL," +
                " `courseId` TEXT NOT NULL," +
                " `id` TEXT NOT NULL," +
                " `name` TEXT NOT NULL," +
                " `slug` TEXT," +
                " `lang` TEXT NOT NULL," +
                " `courseName` TEXT," +
                " PRIMARY KEY(`id`, `lang`) )"
        )

        database.execSQL("ALTER TABLE `pathway_course`(" +
                "DROP COLUMN 'created_at'," +
                "DROP COLUMN 'logo'," +
                "DROP COLUMN 'notes'," +
                "DROP COLUMN 'pathwayName'," +
                "DROP COLUMN 'sequence_num'," +
                "DROP COLUMN 'type'," +
                "DROP COLUMN 'days_to_complete')"
        )
    }

}


// When ever we do any change in local db need to write migration script here.
@Database(
    entities = [Pathway::class, Course::class, Exercise::class, CurrentStudy::class],
    version = DB_VERSION,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CoursesDatabase : RoomDatabase() {

    // DAOs for course, exercises and its sub exercise
    abstract fun courseDao(): CourseDao
    abstract fun pathwayDao(): PathwayDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun currentStudyDao(): CurrentStudyDao
}