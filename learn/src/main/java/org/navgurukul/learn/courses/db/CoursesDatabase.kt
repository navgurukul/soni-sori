package org.navgurukul.learn.courses.db

import androidx.lifecycle.LiveData
import androidx.room.*
import org.navgurukul.learn.courses.db.models.Course
import org.navgurukul.learn.courses.db.models.Exercise
import org.navgurukul.learn.courses.db.typeadapters.Converters

const val DB_VERSION = 1

@Dao
interface CourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCourses(course: List<Course>)

    @Query("select * from saral_courses where id= :id")
    fun course(id: String): LiveData<Course>

    @Query("select * from saral_courses")
    fun getAllCoursesDirect(): List<Course>?
}

@Dao
interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExercise(course: List<Exercise>)

    @Query("select * from course_exercise where course_id = :courseId")
    fun getAllExercisesForCourse(courseId: String): LiveData<List<Exercise>>
}


@Database(entities = [Course::class, Exercise::class], version = DB_VERSION, exportSchema = false)
@TypeConverters(Converters::class)
abstract class CoursesDatabase : RoomDatabase() {

    // DAOs for course and exercise
    abstract fun courseDao(): CourseDao
    abstract fun exerciseDao(): ExerciseDao
}