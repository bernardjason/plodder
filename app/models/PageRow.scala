package models

import play.api.libs.json._

case class SwaggerPageRow( 
     path: String, 
     filename:String , 
     data:String) {
}

object SwaggerPageRow extends ((String, String,String) => SwaggerPageRow) {
  
  implicit val timeSeriesFormat = Json.format[SwaggerPageRow]
}


case class SwaggerPathRow( 
     path: String ) 
object SwaggerPathRow extends ((String) => SwaggerPathRow) {
  
  implicit val timeSeriesFormat = Json.format[SwaggerPathRow]
}

case class SwaggerFilenameRow( 
     path: String, 
     filename:String ) 
object SwaggerFilenameRow extends ((String, String) => SwaggerFilenameRow) {
  
  implicit val timeSeriesFormat = Json.format[SwaggerFilenameRow]
}



case class SwaggerDocument(data:String ) 

object SwaggerDocument extends ((String) => SwaggerDocument) {
  
  implicit val timeSeriesFormat = Json.format[SwaggerDocument]
}


case class PageRow( id: Long, 
     path: String, 
     filename:String , 
     data:String) 

object PageRow extends ((Long, String, String,String) => PageRow) {
  
  implicit val timeSeriesFormat = Json.format[PageRow]
}
