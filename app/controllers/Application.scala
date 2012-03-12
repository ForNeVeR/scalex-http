package controllers

import play.api._
import play.api.mvc._

import scalex.http._
import format._

import scalex.search.{ Engine, RawQuery, Results }

import collection.mutable.WeakHashMap
import scalaz.{ Validation, Failure, Success }

object Application extends Controller {

  type Result = Validation[String, String]

  lazy val env = new ScalexHttpEnv
  lazy val engine = env.scalexEnv.engine

  val limit = 15
  val cache = WeakHashMap[RawQuery, Result]()

  def index(page: Int, callback: String) = Action { request ⇒

    request.queryString get "q" flatMap (_.headOption) map { q =>

      val (rawResult, millis) = Timer.monitor {
        val query = RawQuery(q.trim, page, limit)
        cache.getOrElseUpdate(query, search(query))
      }
      val result = rawResult map { _.replace("{%milliseconds%}", millis.toString) }
      val json = result.fold(errorJson, identity)

      if (callback.isEmpty) Ok(json) as "application/json"
      else Ok("%s(%s)" format (callback, json)) as "application/javascript"
    } getOrElse {
      Redirect("http://scalex.org")
    }
  }

  def errorJson(err: String): String = JsonObject("error" -> err) toString

  def search(query: RawQuery): Result = engine find query map {
    case Results(paginator, defs) ⇒ Formatter(query.string, paginator, defs) toString
  }
}
