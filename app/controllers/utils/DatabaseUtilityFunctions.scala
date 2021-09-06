package controllers.utils

import play.api.mvc._
import play.api.libs.json._
import play.api.Logger
import play.api.db.Database
import play.api.mvc.Results._
import anorm._
import controllers.utils.Response.{uploadSchemaFailureResponse, uploadSchemaSuccessResponse}
import io.circe.syntax._
import io.circe.generic.auto._

import java.sql.Connection


object DatabaseUtilityFunctions {

  implicit val residentWrites = Json.writes[JsonSchema]
  val JsonSchemaParser = Macro.namedParser[JsonSchema]

  def prettyPrintJsonResponse(response: Response): String = response
    .asJson
    .dropNullValues
    .noSpaces

  def getSchemaFromDB(schemaId: String)(implicit db: Database, logger: Logger): Option[JsonSchema] = db.withConnection {
    implicit conn: Connection =>
      try {
        SQL"""SELECT * FROM schemas WHERE schema_id=$schemaId""".as(JsonSchemaParser.singleOpt)
      }
      catch {
        case e: anorm.AnormException =>
          logger.error(e.message)
          None
      }
  }

  def insertSchemaIntoDB(schemaId: String, jsonSchema: String)(implicit db: Database, logger: Logger): Result = {
    db.withConnection { implicit conn =>
      try {
        SQL"""INSERT INTO schemas(schema_id, schema) VALUES ($schemaId, $jsonSchema)"""
          .executeUpdate()
        uploadSchemaSuccessResponse(schemaId)
      }
      catch {
        case e: org.postgresql.util.PSQLException =>
          logger.error(e.getMessage)
          uploadSchemaFailureResponse(schemaId)
      }
    }
  }

  // TODO: TIDY UP
  def getDataFromRequestBody(request: Request[AnyContent]): String = {
    request.body.asFormUrlEncoded.map(
      (test: Map[String, Seq[String]]) => test.head
    ).head._1
      .filter(_ >= ' ')

  }

}
