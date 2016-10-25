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

@Api(value = "/pages", description = "Operations for pages, get,add or update")
@Singleton
class PagesController @Inject() (pageops: PageOperations) extends Controller {

  @ApiOperation(nickname = "search for files", value =
    "search for a filename and return paths",
    notes = "search for a filename and return paths",
    responseContainer = "List", response = classOf[models.SwaggerFilenameRow],
    httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 500, message = "Opps")))
  def searchFiles(@ApiParam(name = "filename", value = "search", required = true, defaultValue = "string") filename: String) =
    Action.async {

      pageops.fileList(filename).map { list =>
        val back = list.map { l => SwaggerFilenameRow(l.path, l.filename) }
        Ok(Json.toJson(back))
      }
    }

  @ApiOperation(nickname = "get file", value =
    "get a files contents",
    notes = "get a files contents",
    response = classOf[models.SwaggerDocument],
    httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 500, message = "Opps")))
  def getFile(@ApiParam(name = "path", value = "path", required = true, defaultValue = "string") path: String,
    @ApiParam(name = "filename", value = "filename", required = true, defaultValue = "string") filename: String) =
    Action.async {

      pageops.getFile(path, filename).map { list =>
        val back = list.map { l => SwaggerDocument(l.data) }
        Ok(Json.toJson(back.head))
      }
      
    }

  @ApiOperation(nickname = "postByName", value = "create a new file",
    notes = "create a new file",
    response = classOf[models.PageRow],
    httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
      name = "body",
      dataType = "models.SwaggerDocument",
      required = true,
      paramType = "body",
      value = " t b d..")))
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Invalid")))
  def postByName(@ApiParam(name = "path", value = "path", required = true, defaultValue = "string") path: String,
    @ApiParam(name = "filename", value = "filename", required = true, defaultValue = "string") filename: String) = Action.async(BodyParsers.parse.json) { implicit request =>

    val create = request.body.validate[models.SwaggerDocument]

    create.fold(
      errors => {
        Future.successful(InternalServerError(JsError.toJson(errors)))
      },
      row => {
        pageops.create(path, filename, row.data).map(created =>
          Ok(Json.toJson(created)))

      })

  }

  @ApiOperation(nickname = "putByName", value = "update a file",
    notes = "create a new file",
    response = classOf[models.PageRow],
    httpMethod = "PUT")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
      name = "body",
      dataType = "models.SwaggerDocument",
      required = true,
      paramType = "body",
      value = " t b d..")))
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Invalid")))
  def putByName(@ApiParam(name = "path", value = "path", required = true, defaultValue = "string") path: String,
    @ApiParam(name = "filename", value = "filename", required = true, defaultValue = "string") filename: String) = 
      Action.async(BodyParsers.parse.json) { implicit request =>

    val create = request.body.validate[models.SwaggerDocument]

    create.fold(
      errors => {
        Future.successful(InternalServerError(JsError.toJson(errors)))
      },
      row => {
       
         val result:Future[Seq[PageRow]] = for  {
          update <- pageops.update(path, filename, row.data)
          retrieve <- pageops.getFile(path, filename)    
         } yield retrieve
         
         result.map( row =>  Ok(Json.toJson(SwaggerDocument(row(0).data) )  ) )
        
       
      })
  }
}