package services

import play.api.libs.ws.{WSRequestHolder, WS}
import play.libs.Json
import com.fasterxml.jackson.databind.JsonNode
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration.Duration
import play.api.Logger._
import scala.util.Try
import scala.Some
import services.Course

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

    def selectRandomFromList(): String = {
      searchStrings((Math.random() * searchStrings.size).asInstanceOf[Int])
    }

    selectRandomFromList()
  }

  def loadRandomCourse(): Option[Course] = {

    def getText(node: JsonNode, field: String): String = {
      node.get(field).asText()
    }

    val courseFuture: Future[Course] = Future{ Course(null,null,null,null,null) }.flatMap(a => {
      getRequestForSearch(
        randomSearchString()
      ).get().map(response => {

        def randomCourseNode(): JsonNode = {
          val courses = Json.parse(response.body)
          courses.get((Math.random()*courses.size()).asInstanceOf[Int])
        }

        val courseNode = randomCourseNode()
        val courseId = getText(courseNode, "course_id")

        println(courseNode)

        Course(
          courseId,
          getText(courseNode, "name"),
          null,
          null,
          null
        )

      })
    }).flatMap(course => {
      println
      getRequestForOverview(course.courseCode).get().map(response => {

        val overviewNode = Json.parse(response.body)

        course.copy(
          credits = getText(overviewNode, "credits"),
          timePeriod = "Dunno",
          description = getText(overviewNode, "content")
        )
      })
    })

    try {
      Some(Await.result(courseFuture, Duration("30 seconds")))
    }
    catch {
      case e: Exception => {
        logger.warn(e.getMessage, e);
        None
      }

    }

  }

}
