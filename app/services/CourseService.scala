package services

import play.api.libs.ws.{WSRequestHolder, WS}
import play.libs.Json
import com.fasterxml.jackson.databind.JsonNode
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import utils.JsonUtils._
import play.Logger

case class Course(courseCode: String, courseName: String, timePeriod: String, credits: String, description: String)

object CourseService {

  val key = "cdda4ae4833c0114005de5b5c4371bb8"
  val url = "http://noppa-api-dev.aalto.fi/api/v1/courses"

  private def getRequestForSearch(searchString: String): WSRequestHolder = {
    WS.url(url).withQueryString(
      "search" -> searchString,
      "key" -> key
    )
  }

  private def getRequestForOverview(courseId: String): WSRequestHolder = {
    WS.url(url + "/" + courseId + "/overview").withQueryString(
      "key" -> key
    )
  }

  private def randomSearchString(): String = {
    val searchStrings =
      List(
        "introduction","software","management","marketing","media","law","english","network","programming","culture",
        "business","product","seminar","supply","innovation","knowledge"
      )

    def selectRandomFromList = {
      searchStrings((Math.random() * searchStrings.size).toInt)
    }

    selectRandomFromList
  }

  def loadRandomCourse(): Future[Course] = {

    def getSimpleCourse: Future[Course] = {

      def requestCourses = getRequestForSearch(randomSearchString()).get()

      requestCourses.map { response =>

          def randomCourseNode: JsonNode = {
            val courses = Json.parse(response.body)
            courses.get((Math.random() * courses.size()).toInt)
          }

          val courseNode = randomCourseNode
          val courseId = getText(courseNode, "course_id")

          Logger.debug(courseNode.toString)

          Course(courseId, getText(courseNode, "name"), null, null, null)
      }
    }

    def getFullCourse(course: Course): Future[Course] = {

      def requestFullCourse = getRequestForOverview(course.courseCode).get()

      requestFullCourse.map { response =>
        val overviewNode = Json.parse(response.body)

        course.copy(
          credits = getText(overviewNode, "credits"),
          timePeriod = "Dunno",
          description = getText(overviewNode, "content")
        )
      }
    }

    for(
      simpleCourse <- getSimpleCourse;
      fullCourse <- getFullCourse(simpleCourse)
    ) yield fullCourse

  }

}
