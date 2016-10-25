package controllers

import javax.inject._
import play.api._
import play.api.db.slick.HasDatabaseConfig
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import play.api.mvc.Action
import play.api.mvc.Controller

import com.eviware.soapui.impl.wsdl.WsdlInterface;
import com.eviware.soapui.impl.wsdl.WsdlOperation;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlImporter;
import com.eviware.soapui.model.iface.Operation;
import collection.JavaConverters._
import dal.PageOperations
import scala.concurrent.Await
import models.PageRow

@Singleton
class HomeController @Inject() (pageops: PageOperations) extends Controller {

  def navigate = Action {
    request =>
      Ok(views.html.navigate())
  }
  def swagger = Action {
    request =>
      Ok(views.html.swagger())
  }

  def rawwsdl(wsdl: String) = Action {
    request =>

      val project = new WsdlProject();

      val wsdls = WsdlImporter.importWsdl(project, wsdl);
      val w = wsdls(0);

      Ok(views.html.getwsdl(w.getOperationList.asScala.toList)(wsdl))
  }

  def echo = Action { request =>
    val r = request.body.asXml

    Ok(r.getOrElse(<empty></empty>))
  }

  def markdown(path: String, filename: String) = Action.async { request =>

    for {
      all <- pageops.getAllPages()
      page <- pageops.getFile(path, filename).map { x =>
        if (x.size > 0) {
          x.head
        } else {
          if (filename != "home")
            PageRow(0, "", "", "# no page available")
          else
            PageRow(0, "", "", null)
        }
      }
    } yield Ok(views.html.markdown(page.data, path, filename, all.map { x => x.replaceAll("^//","") }.toList))

  }
}