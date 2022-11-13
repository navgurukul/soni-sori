package org.navgurukul.learn.courses.db

import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import org.navgurukul.learn.courses.db.models.*
import org.navgurukul.learn.courses.db.typeadapters.Converters
import org.navgurukul.learn.courses.network.model.CompletedContentsIds

const val DB_VERSION = 10

@Dao
interface PathwayDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPathways(course: List<Pathway>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPathway(course: Pathway)

    @Query("select * from pathway")
    fun getAllPathways(): List<Pathway>?

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

    @Query("DELETE FROM pathway_course")
    fun deleteAllCourses()

}

@Dao
interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExercise(course: List<CourseExerciseContent?>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseAsync(course: List<CourseExerciseContent?>?)

    @Query("select * from course_exercise where courseId = :courseId and lang = :lang")
    suspend fun getAllExercisesForCourse(courseId: String, lang: String): List<CourseExerciseContent>

    @Query("select * from course_exercise where id = :exerciseId and lang = :lang")
    fun getExerciseById(exerciseId: String, lang: String): LiveData<CourseExerciseContent>


    @Query("select * from course_exercise where courseId = :courseId and lang = :lang")
    fun getAllExercisesForCourseDirect(courseId: String, lang: String): List<CourseExerciseContent>

    @Query("Update course_exercise set courseContentProgress = :exerciseProgress where id = :exerciseId")
    suspend fun markCourseExerciseCompleted(exerciseProgress: String, exerciseId: String)

    @Query("Update course_exercise set courseContentProgress = :exerciseProgress where id in (:exerciseIdList) ")
    suspend fun markExerciseCompleted(exerciseProgress: String,exerciseIdList : List<String>?)
}


@Dao
interface CurrentStudyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCourseContentCurrent(course: CurrentStudy)

    @Query("select * from user_current_study where courseId = :courseId")
    suspend fun getCurrentStudyForCourse(courseId: String?): CurrentStudy?
}

@Dao
interface ClassDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCourseExerciseCurrent(course: CurrentStudy)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertClass(course: List<CourseClassContent?>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClassAsync(course: List<CourseClassContent?>?)

    @Query("select * from course_class where id = :classId and lang = :lang")
    fun getClassById(classId: String, lang: String): LiveData<CourseClassContent>

    @Query("select * from course_class where courseId = :courseId and lang = :lang")
    suspend fun getAllClassesForCourse(courseId: String, lang: String): List<CourseClassContent>

    @Query("Update course_class set courseContentProgress = :contentProgress where id = :classId")
    suspend fun markCourseClassCompleted(contentProgress: String, classId: String)

    @Query("Update course_class set courseContentProgress = :classProgress where id in (:classIdList) ")
    suspend fun markClassCompleted(classProgress: String,classIdList : List<String>?)

}

@Dao
interface AssessmentDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCourseAssessmentCurrent(course: CurrentStudy)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAssessment(course: List<CourseAssessmentContent?>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessmentAsync(course: List<CourseAssessmentContent?>?)

    @Query("select * from course_assessment where id = :assessmentId and lang= :lang")
    fun getAssessmentById(assessmentId: String, lang: String): LiveData<CourseAssessmentContent>

    @Query("select * from course_assessment where courseId = :courseId and lang = :lang")
    suspend fun getAllAssessmentForCourse(courseId: String, lang: String): List<CourseAssessmentContent>

    @Query("Update course_assessment set courseContentProgress = :assessmentProgress where id= :assessmentId")
    suspend fun markCourseAssessmentCompleted(assessmentProgress: String, assessmentId: String)

    @Query("Update course_assessment set courseContentProgress = :assessmentProgress where id in (:assessmentIdList)" )
    suspend fun markAssessmentCompleted(assessmentProgress: String, assessmentIdList : List<String>?)


}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `pathway` (`code` TEXT NOT NULL, `createdAt` TEXT NOT NULL, `description` TEXT NOT NULL, `id` INTEGER NOT NULL, `name` TEXT NOT NULL,  `logo` TEXT, PRIMARY KEY(`id`))")
    }

}

val MIGRATION_2_3 = object : Migration(2, 3) {
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

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Pathway
        database.execSQL("ALTER TABLE `pathway` ADD COLUMN `supportedLanguages` TEXT NOT NULL DEFAULT '[{\"code\": \"en\", \"label\": \"English\"}]'")
    }

}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {

        //drop old table
        database.execSQL("DROP TABLE course_exercise")

        //create new table
        database.execSQL("CREATE TABLE `course_exercise`(" +
                " `content` TEXT NOT NULL," +
                " `courseId` TEXT NOT NULL," +
                " `id` TEXT NOT NULL," +
                " `name` TEXT NOT NULL," +
                " `lang` TEXT NOT NULL," +
                " `courseName` TEXT," +
                " PRIMARY KEY(`id`, `lang`) )"
        )

        //drop old table
        database.execSQL("DROP TABLE pathway_course")

        //create new table
        database.execSQL("CREATE TABLE `pathway_course`(" +
                " `id` TEXT NOT NULL," +
                " `name` TEXT NOT NULL," +
                " `shortDescription` TEXT NOT NULL," +
                " `pathwayId` INTEGER," +
                " `supportedLanguages` TEXT NOT NULL DEFAULT '[\"en\"]'," +
                " PRIMARY KEY(`id`) )"
        )

    }

}


val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {

        database.execSQL(
            "ALTER TABLE `course_exercise`" +
                    "ADD COLUMN 'exerciseProgress' TEXT"
        )

        database.execSQL("DROP TABLE user_current_study")

        //create new table
        database.execSQL(
            "CREATE TABLE `user_current_study`(" +
                    " `courseId` TEXT NOT NULL," +
                    " `exerciseId` TEXT NOT NULL," +
                    " PRIMARY KEY(`courseId`) )"
        )

    }

}


val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {

        database.execSQL(
            "ALTER TABLE `pathway` ADD COLUMN 'cta' TEXT"
        )
    }
}

val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(database: SupportSQLiteDatabase) {

        database.execSQL("DROP TABLE course_exercise")
        database.execSQL("CREATE TABLE `course_exercise`(" +
                " `content` TEXT NOT NULL," +
                " `courseId` TEXT NOT NULL," +
                " `id` TEXT NOT NULL," +
                " `name` TEXT NOT NULL," +
                " `lang` TEXT NOT NULL," +
                " `courseName` TEXT," +
                " 'courseContentProgress' TEXT," +
                " 'sequenceNumber' INTEGER," +
                " 'courseContentType' TEXT NOT NULL," +
                " 'description' TEXT NOT NULL DEFAULT ''," +
                " PRIMARY KEY(`id`, `lang`) )")

        database.execSQL("DROP TABLE pathway")
        database.execSQL("CREATE TABLE IF NOT EXISTS `pathway` (`code` TEXT NOT NULL, `createdAt` TEXT, `description` TEXT NOT NULL, `id` INTEGER NOT NULL, `name` TEXT NOT NULL, `logo` TEXT, `supportedLanguages` TEXT NOT NULL DEFAULT '[{\"code\": \"en\", \"label\": \"English\"}]' ,'cta' TEXT, PRIMARY KEY(`id`))")


        database.execSQL("CREATE TABLE `course_class`(" +
                " `courseId` TEXT NOT NULL," +
                " `id` TEXT NOT NULL," +
                " `lang` TEXT NOT NULL," +
                " `courseName` TEXT," +
                " 'courseContentProgress' TEXT," +
                " 'sequenceNumber' INTEGER," +
                " 'courseContentType' TEXT NOT NULL," +
                " 'description' TEXT NOT NULL," +
                " 'title' TEXT NOT NULL," +
                " 'subTitle' TEXT," +
                " 'facilitator' TEXT ," +
                " 'startTime' INTEGER NOT NULL ," +
                " 'endTime' INTEGER NOT NULL ," +
                " 'type' TEXT NOT NULL," +
                " 'meetLink' TEXT," +
                " 'isEnrolled' INTEGER NOT NULL," +
                " 'parentId' TEXT," +
                " PRIMARY KEY(`id`, `lang`) )")

    }
}

val MIGRATION_8_9 = object : Migration(8,9) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "CREATE TABLE `course_assessment`(" +
                    " `content` TEXT NOT NULL," +
                    " `courseId` TEXT NOT NULL," +
                    " `id` TEXT NOT NULL," +
                    " `lang` TEXT NOT NULL," +
                    " `courseName` TEXT," +
                    " 'courseContentProgress' TEXT," +
                    " 'sequenceNumber' INTEGER," +
                    " 'courseContentType' TEXT NOT NULL," +
                    " 'assess_selectedOption' INTEGER," +
                    " PRIMARY KEY(`id`, `lang`) )"
        )
    }
}
val MIGRATION_9_10 = object : Migration(9,10){
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE `pathway_course` ADD COLUMN 'completed_portion' INTEGER"
        )
    }

}

// When ever we do any change in local db need to write migration script here.
@Database(
    entities = [Pathway::class, Course::class, CourseExerciseContent::class, CurrentStudy::class, CourseClassContent::class, CourseAssessmentContent::class],
    version = DB_VERSION,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CoursesDatabase : RoomDatabase() {

    // DAOs for course, classes, exercises and its sub exercise
    abstract fun courseDao(): CourseDao
    abstract fun pathwayDao(): PathwayDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun currentStudyDao(): CurrentStudyDao
    abstract fun classDao(): ClassDao
    abstract fun assessmentDao() : AssessmentDao
}