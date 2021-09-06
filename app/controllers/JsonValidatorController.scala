package controllers

import play.api.mvc._
import play.api.db._
import play.api.Logger

import javax.inject.Inject

import com.fasterxml.jackson.databind.JsonNode
import com.github.fge.jsonschema.core.report.ProcessingReport
import com.github.fge.jackson.JsonLoader.fromString

import io.circe.generic.auto._
import io.circe.syntax._

import utils.{Response, JsonSchema}
import utils.Response._
import utils.ValidationJsonHelperFunctions._
import utils.DatabaseUtilityFunctions._


class JsonValidatorController @Inject()(
                                         val controllerComponents: ControllerComponents,
                                         implicit val db: Database,
                                         implicit val databaseExecutionContext: DatabaseExecutionContext)
  extends BaseController {

  implicit val logger: Logger = Logger(this.getClass())

  def validateJsonAgainSchema(schemaId: String): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val jsonWithoutNulls: String = getJsonToValidateFromRequestBody(request)
    val jsonToValidate: JsonNode = fromString(jsonWithoutNulls)
    val schemaToValidateAgainst: Option[JsonSchema] = getSchemaFromDB(schemaId)
    schemaToValidateAgainst match {
      case Some(schema) =>
        val jsonValidationReport: ProcessingReport = generateJsonProcessingReport(jsonToValidate, schema)
        if (jsonValidationReport.isSuccess) validationSuccessResponse(schemaId, jsonWithoutNulls)
        else {
          val errorMessages = getErrorMessagesFromProcessingReport(jsonValidationReport)
          validationFailedErrorResponse(schemaId, errorMessages)
        }
      case None =>
        noSchemaMatchingSchemaIdResponse(schemaId)
    }
  }

  def uploadSchema(schemaId: String): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val jsonSchema: String = getDataFromRequestBody(request)
    insertSchemaIntoDB(schemaId, jsonSchema)
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