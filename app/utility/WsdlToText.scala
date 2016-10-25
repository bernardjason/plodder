package utility

import scala.concurrent.Future
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
import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.duration._

import play.api.mvc._
import play.api.libs.ws._

import akka.actor.ActorSystem
import akka.util.ByteString
import scala.util.{Success, Failure}


object WsdlToText extends App {
  
  val builder = new com.ning.http.client.AsyncHttpClientConfig.Builder()
  val ws = new play.api.libs.ws.ning.NingWSClient(builder.build())
  
  def getWsdl() = {
    /*
    val futureResult = ws.url("http://10.243.129.9:8080/QueryHssx/1.0?wsdl").get().map { x => 
      println("GOT "+x.xml \ "types" )
      System.exit(0)
    } 
    *    
    */
        val futureResult = ws.url("http://10.243.129.9:8080/QueryHss/1.0?wsdl").get().onComplete { 
          case Success(response) => response.status match {
            case success if success >= 200 && success < 300 =>
                println("ALL GOOD "+response.xml)
              case other =>
                println("ERROR "+response)
          }
          System.exit(0);

          
          case Failure(x) => println("Error "+x);
          
          System.exit(0);
        }
  }
  println("TEST")
  getWsdl();
  
}