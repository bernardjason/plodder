package controllers

import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.Duration

import dal.PageOperations
import javax.inject.Inject
import javax.inject.Singleton
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.JsError
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.BodyParsers
import play.api.mvc.Controller
import scala.collection.mutable.ArrayBuffer
import models.PageRow
import io.swagger.annotations._
import play.api.mvc._
import models.SwaggerFilenameRow
import models.SwaggerDocument
import models.SwaggerPathRow

@Api(value = "/path", description = "Operations for pages, get,add or update")
@Singleton
class PathController @Inject() (pageops: PageOperations) extends Controller {

  @ApiOperation(nickname = "get all paths", value =
    "return all paths available",
    notes = "get all the paths",
    responseContainer = "List", response = classOf[models.SwaggerPathRow],
    httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 500, message = "Opps")))
    def getAllPaths(@ApiParam(name = "pathfilter", value = "search, try a % as a wildcard", required = false, defaultValue = "%") pathfilter: String) =
    Action.async {
      pageops.justPathFilter(pathfilter).map { list =>
        val back = list.map { l => SwaggerPathRow(l.path) }
        Ok(Json.toJson(back))
      }
  }
  
   @ApiOperation(nickname = "get files in a path", value =
    "get files in a path",
    notes = "get files in a path",
    responseContainer = "List", response = classOf[models.SwaggerFilenameRow],
    httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 500, message = "Opps")))
    def getPath(@ApiParam(name = "path", value = "search", required = false, defaultValue = "string") path: String) =
    Action.async {
      pageops.pathFilter(path).map { list =>
        val back = list.map { l => SwaggerFilenameRow(l.path, l.filename) }
        Ok(Json.toJson(back))
      }
  }
}