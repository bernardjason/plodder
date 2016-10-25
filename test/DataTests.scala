
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



class DataTests  extends PlaySpec with OneServerPerSuite {
  
  object TestData {


  val configuration: Configuration = Configuration.from(
    Map(
    "slick.dbs.default.driver" ->"slick.driver.SQLiteDriver$",
    "slick.dbs.default.db.driver" ->"org.sqlite.JDBC",
    "slick.dbs.default.db.url" ->"jdbc:sqlite:test.db"

  )) 

}
  
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
    "now add test data rows" in {
      def insertARow(path:String,filename:String,t:String) = {
        val j = Json.parse("""{ "data" : """"+t+"""" }""")
        
        val rr = FakeRequest().withBody(j)
        val result: Future[Result] = d.postByName(path,filename).apply(rr.asInstanceOf[ play.api.mvc.Request[play.api.libs.json.JsValue]  ] )
        result
      }

      {
        val bodyText: String = contentAsString(   insertARow("/ebm/log", "smsmessages","some text messages")   )
        assert(bodyText endsWith "path\":\"/ebm/log\",\"filename\":\"smsmessages\",\"data\":\"some text messages\"}" )
      }
      {
        val bodyText: String = contentAsString(   insertARow("/ebm/log", "fred","here is what is in fred")   )
        assert(bodyText endsWith "path\":\"/ebm/log\",\"filename\":\"fred\",\"data\":\"here is what is in fred\"}" )
      }
      
      {
        val bodyText: String = contentAsString(   insertARow("/ebm", "weblogic","some notes on weblogic")   )
        assert(bodyText endsWith "path\":\"/ebm\",\"filename\":\"weblogic\",\"data\":\"some notes on weblogic\"}" )
      }
            
    }
    "now contains" in {
      val request = FakeRequest() withTextBody("")
      
      val result: Future[Result] = d.searchFiles("smsmessages").apply(request)
     
      val bodyText: String = contentAsString(result)
      println("XXX",bodyText)
      bodyText mustBe """[{"path":"/ebm/log","filename":"smsmessages"}]"""
    }
  }

}
