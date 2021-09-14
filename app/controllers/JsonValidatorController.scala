package controllers

import play.api.mvc._
import org.slf4j.{Logger, LoggerFactory}

import javax.inject.Inject
import com.fasterxml.jackson.databind.JsonNode
import com.github.fge.jsonschema.core.report.ProcessingReport
import com.github.fge.jackson.JsonLoader.fromString
import io.circe.generic.auto._
import io.circe.syntax._
import utils.JsonSchema
import utils.Response._
import utils.ValidationJsonHelperFunctions._
import utils.DatabaseUtilityFunctions._
import utils.DataBaseImplementation.db


case class JsonValidatorController @Inject()(controllerComponents: ControllerComponents) extends BaseController {

  implicit val logger: Logger = LoggerFactory.getLogger(this.getClass())

  def validateJsonAgainSchema(schemaId: String): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val jsonWithoutNullsOption: Option[String] = getJsonToValidateFromRequestBody(request)
    jsonWithoutNullsOption match {
      case Some(jsonWithoutNulls: String) =>
        val jsonToValidate: JsonNode = fromString(jsonWithoutNulls)
        val schemaToValidateAgainst: Option[JsonSchema] = getSchemaFromDB(schemaId)
        schemaToValidateAgainst match {
          case Some(schemas: JsonSchema) =>
            val jsonValidationReport: ProcessingReport = generateJsonProcessingReport(jsonToValidate, schemas)
            if (jsonValidationReport.isSuccess) {
              validationSuccessResponse(schemaId, jsonWithoutNulls)
            } else {
              val errorMessages = getErrorMessagesFromProcessingReport(jsonValidationReport)
              validationFailedErrorResponse(schemaId, errorMessages)
            }
          case _ =>
            logger.error(s"Error when finding schema for schemaId: $schemaId.")
            noSchemaMatchingSchemaIdResponse(schemaId)
        }
      case None => noSchemaToValidateAgainstParsedInRequestResponse()
    }

  }

  def uploadSchema(schemaId: String): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val jsonSchemaOption: Option[String] = getDataFromRequestBody(request)
    jsonSchemaOption match {
      case Some(jsonSchema: String) => insertSchemaIntoDB(schemaId, jsonSchema)
      case None => noSchemaToUploadParsedInRequestResponse(schemaId)
    }
  }

  def getSchema(schemaId: String): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val schema: Option[JsonSchema] = getSchemaFromDB(schemaId)
    schema match {
      case Some(jsonSchema: JsonSchema) =>
        val schemaWithNoSpaces: String = jsonSchema.asJson.noSpaces
        getJsonSchemaSuccessResponse(schemaId, schemaWithNoSpaces)
      case None => noSchemaMatchingSchemaIdResponse(schemaId)
    }
  }
}