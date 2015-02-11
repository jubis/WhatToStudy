package controllers

import play.api.mvc.{Action, Result, Controller}
import scala.concurrent.Future
import services.{Course, CourseService}
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global

object Courses extends Controller {

  def index(random: Option[Boolean]) = Action.async {
    random match {
      case Some(true) => CourseService.loadRandomCourse().map(getResult(_))
      case _ => Future(getResult(null))
    }
  }

  def getResult(course: Course): Result = course match {
    case course: Course => Ok(Json.format[Course].writes(course))
    case _ => Ok("")
  }

}
