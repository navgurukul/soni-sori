package org.navgurukul.learn.courses.db

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import org.navgurukul.learn.courses.db.models.Course
import org.navgurukul.learn.courses.db.models.CurrentStudy
import org.navgurukul.learn.courses.db.models.Exercise
import org.navgurukul.learn.courses.db.models.Pathway
import org.navgurukul.learn.courses.db.typeadapters.Converters

const val DB_VERSION = 2

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
    fun course(id: String): LiveData<Course>

    @Query("select * from pathway_course")
    fun getAllCoursesDirect(): List<Course>?

    @Query("select * from pathway_course where id=:courseId")
    fun getCourseById(courseId: String): List<Course>

    @Query("select * from pathway_course where pathwayId=:pathwayId")
    fun getCoursesByPathwayId(pathwayId: Int): List<Course>
}

@Dao
interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExercise(course: List<Exercise?>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseAsync(course: List<Exercise?>?)

    @Query("select * from course_exercise where courseId = :courseId")
    fun getAllExercisesForCourse(courseId: String): LiveData<List<Exercise>>

    @Query("select * from course_exercise where id = :exerciseId")
    fun getExerciseById(exerciseId: String): LiveData<List<Exercise>>


    @Query("select * from course_exercise where courseId = :courseId")
    fun getAllExercisesForCourseDirect(courseId: String): List<Exercise>
}

@Dao
interface CurrentStudyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCourseExerciseCurrent(course: CurrentStudy)

    @Query("select * from user_current_study where courseId = :courseId")
    suspend fun getCurrentStudyForCourse(courseId: String?): List<CurrentStudy>
}

val MIGRATION_1_2 = object: Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `pathway` (`code` TEXT NOT NULL, `createdAt` TEXT NOT NULL, `description` TEXT NOT NULL, `id` INTEGER NOT NULL, `name` TEXT NOT NULL,  `logo` TEXT, PRIMARY KEY(`id`))")
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