package org.navgurukul.learn.ui.learn.c4ca

import android.app.Application
import org.intellij.lang.annotations.Language
import org.navgurukul.learn.courses.network.SaralCoursesApi
import org.navgurukul.learn.courses.network.model.Course
import org.navgurukul.learn.courses.network.model.ModuleCourseExerciseContainer
import org.navgurukul.learn.courses.network.model.PathwayC4CA

class C4CARepo(
    private val pathwayC4CA: SaralCoursesApi,
) {
    suspend fun getPathwayC4CA() : PathwayC4CA {
        return try {
            pathwayC4CA.getC4CAPathway()
        } catch (e: Exception) {
            throw e
        }
    }


    suspend fun moduleGetCourseContentAsync(courseId : String, language: String): Course {
       return try {
            pathwayC4CA.moduleGetCourseContentAsync(courseId, language).course
        } catch (ex : Exception){
            throw ex
        }
    }
}