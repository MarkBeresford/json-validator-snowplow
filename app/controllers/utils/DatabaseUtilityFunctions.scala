package controllers.utils

import play.api.mvc._
import play.api.libs.json._
import org.slf4j.Logger

import scala.concurrent._
import controllers.utils.Response.{uploadSchemaFailureResponse, uploadSchemaSuccessResponse}
import io.circe.syntax._
import io.circe.generic.auto._
import slick.lifted.TableQuery
import slick.jdbc.H2Profile.api._
import scala.concurrent.duration.FiniteDuration

object DatabaseUtilityFunctions {


  val jsonSchemaTable = TableQuery[JsonSchemaTable]


  def prettyPrintJsonResponse(response: Response): String = response
    .asJson
    .dropNullValues
    .noSpaces

  def syncResult[R](action:slick.dbio.DBIOAction[R, slick.dbio.NoStream, scala.Nothing])(implicit db: Database):R =
    Await
      .result(
        db.run(action),
        FiniteDuration(10L, "s")
      )

  def getSchemaFromDB(schemaId: String)(implicit db: Database, logger: Logger): Option[JsonSchema] = {
    val schemaMatchingSchemaId = jsonSchemaTable.filter(_.id === schemaId)
    val query = for (schema <- schemaMatchingSchemaId) yield schema
    syncResult { query.result.headOption }
  }

  def insertSchemaIntoDB(schemaId: String, jsonSchema: String)(implicit db: Database, logger: Logger): Result = {
    try {
      val query = DBIO.seq(
        jsonSchemaTable += JsonSchema(schemaId, jsonSchema)
      )
      syncResult(query)
      uploadSchemaSuccessResponse(schemaId)
    }
    catch {
      case e: org.postgresql.util.PSQLException =>
        logger.error(e.getMessage)
        uploadSchemaFailureResponse(schemaId)
    }
  }

  def getDataFromRequestBody(request: Request[AnyContent]): Option[String] = {
    val requestBody = request.body
    val requestHeaders = request.headers
    val contextFromHeader: String = requestHeaders
      .toMap
      .getOrElse("Content-Type", Seq("application/x-www-form-urlencoded"))
      .head
    contextFromHeader match {
      case "application/x-www-form-urlencoded" =>
        val encodedRequestBodyOption: Option[(String, Seq[String])] = requestBody
          .asFormUrlEncoded
          .map(_.head)
        encodedRequestBodyOption.map(
          _._1.filter(_ >= ' ')
        )
      case "application/json" => requestBody.asJson.map(_.toString())
      case _ => None
    }
  }

  def createSchemaTable(implicit db: Database, logger: Logger): Unit = {
    logger.info("Stating to create database Schemas.")
    try {
      val query = DBIO.seq(
        jsonSchemaTable.schema.create
      )
      syncResult(query)
      logger.info("Successfully created database Schemas.")
    }
    catch {
      case e: org.postgresql.util.PSQLException =>
        logger.error(e.getMessage)
    }
  }

}
