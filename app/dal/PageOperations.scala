package dal

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.concurrent.Await

import javax.inject.Inject
import javax.inject.Singleton
import models.PageRow
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import models.SwaggerPathRow

@Singleton
class PageOperations @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  class PagesTable(tag: Tag) extends Table[models.PageRow](tag, "pages") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def path = column[String]("path")
    def filename = column[String]("filename")
    def data = column[String]("data")

    override def * = (id, path, filename, data) <> (models.PageRow.tupled, models.PageRow.unapply _)
  }

  private val pages = TableQuery[PagesTable]

  def create(path: String, filename: String, data: String): Future[PageRow] = db.run {
    (pages.map(p => (p.path, p.filename, p.data))
      returning pages.map(_.id)
      into ((entry, id) => {
        PageRow(id,
          entry._1, entry._2,
          entry._3)
      })).+=(path, filename, data)

  }



  def update(path: String, filename: String, data: String) = {

     val found = pages.filter(_.path === path).filter(_.filename === filename).result

     db.run(found).map { r => 
       val value = PageRow(r(0).id, path, filename, data)

       Await.result(db.run(pages.filter(_.path === path).filter(_.filename === filename).update(value) ) ,Duration.Inf)
     }
  }
    
  
  def getFile(path: String, filename: String): Future[Seq[PageRow]] = db.run {
    pages.filter(_.path === path).filter(_.filename === filename).result
  }

  def dirList(path: String): Future[Seq[PageRow]] = db.run {
    pages.filter(_.path === path).result
  }

  def fileList(filename: String): Future[Seq[PageRow]] = db.run {
    pages.filter(_.filename === filename).result.map(_.map { h => PageRow(h.id, h.path, h.filename, null) })
  }

  def fileStartsWith(search: String): Future[Seq[PageRow]] = db.run {
    pages.filter(_.filename like search).result
  }
  
  def pathFilter(search: String): Future[Seq[PageRow]] = db.run {
    pages.filter(_.path like search).sortBy(_.path.asc).distinct.result
  }
  
  def justPathFilter(search: String): Future[Seq[SwaggerPathRow]] = db.run {
    pages.filter(_.path like search).map { x => (x.path) }.distinct.result.map( _.map { x => SwaggerPathRow(x) })
  }
  
  def getAllPages() :  Future[Seq[String]] = db.run {
    pages.filter(_.path like "%").map { x =>  (x.path,x.filename) }.distinct.result.map( _.map { x => x._1+"/"+x._2 } )   //.map( _.map { x => SwaggerPathRow(x) })
  }
}