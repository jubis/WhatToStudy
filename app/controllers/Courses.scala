package controllers

import play.api.mvc.{Result, Action, Controller}
import services.{Course, CourseService}
import play.api.libs.json._
import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Courses extends Controller {

  def index(random: Option[Boolean]) = Action {

    val result = random.flatMap(
      if(_) CourseService.loadRandomCourse()
      else None
    )

    sendResultAsJson(result)

  }

  def sendResultAsJson(course: Option[Course]): Result = {
    Ok(course.map(Json.format[Course].writes(_)).map(_.toString).getOrElse(""))
  }

  def sendError() {

  }
}
