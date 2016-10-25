
import scala.concurrent.Future
import scala.concurrent.Future

import org.scalatestplus.play._

import controllers.PagesController
import play.api.Configuration
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json._
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._
import slick.profile.BasicProfile

object TestData {


  val configuration: Configuration = Configuration.from(
    Map(
    "slick.dbs.default.driver" ->"slick.driver.SQLiteDriver$",
    "slick.dbs.default.db.driver" ->"org.sqlite.JDBC",
    "slick.dbs.default.db.url" ->"jdbc:sqlite:test.db"

  )) 

}

class ExampleSpec  extends PlaySpec with OneServerPerSuite {
  
    val injector = new GuiceApplicationBuilder().configure(TestData.configuration).injector()
  
    val d = injector.instanceOf[controllers.PagesController]
  
    val ops = injector.instanceOf[dal.PageOperations]
  
    val dbConfig = ops.dbConfig
  
    import slick.driver.SQLiteDriver.api._
   
    dbConfig.db.run(  sqlu"delete from pages" )
    
   "basic test" should {
    "be empty" in {
      val request = FakeRequest() withTextBody("")
      
      val result: Future[Result] = d.searchFiles("string").apply(request)
     
      val bodyText: String = contentAsString(result)
      bodyText mustBe "[]" 
    }
    "now add a row" in {
      val j = Json.parse("""{ "data" : "hello world" }""")
      val rr = FakeRequest().withBody(j)
      
      val result: Future[Result] = d.postByName("demo", "page1").apply(rr.asInstanceOf[ play.api.mvc.Request[play.api.libs.json.JsValue]  ] )
     
      val bodyText: String = contentAsString(result)

      assert(bodyText endsWith "path\":\"demo\",\"filename\":\"page1\",\"data\":\"hello world\"}" ) 
    }
    "now contains" in {
      val request = FakeRequest() withTextBody("")
      
      val result: Future[Result] = d.searchFiles("page1").apply(request)
     
      val bodyText: String = contentAsString(result)
      println("XXX",bodyText)
      bodyText mustBe """[{"path":"demo","filename":"page1"}]"""
    }
  }

}
