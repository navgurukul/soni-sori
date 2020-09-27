package org.navgurukul.learn.courses.db

import androidx.lifecycle.LiveData
import androidx.room.*
import org.navgurukul.learn.courses.db.models.Course
import org.navgurukul.learn.courses.db.models.CurrentStudy
import org.navgurukul.learn.courses.db.models.Exercise
import org.navgurukul.learn.courses.db.typeadapters.Converters

const val DB_VERSION = 1

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
}

@Dao
interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExercise(course: List<Exercise?>?)

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


// When ever we do any change in local db need to write migration script here.
@Database(
    entities = [Course::class, Exercise::class, CurrentStudy::class],
    version = DB_VERSION,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CoursesDatabase : RoomDatabase() {

    // DAOs for course, exercises and its sub exercise
    abstract fun courseDao(): CourseDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun currentStudyDao(): CurrentStudyDao
}