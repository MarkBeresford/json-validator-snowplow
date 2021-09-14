package controllers.utils

import controllers.utils.DatabaseUtilityFunctions.prettyPrintJsonResponse
import play.api.mvc
import play.api.mvc.Results._

import scala.collection.mutable.ListBuffer

case class Response(
                     action: String,
                     id: Option[String] = None,
                     status: String,
                     message: Option[String] = None,
                     validJson: Option[String] = None
                   )

object Response{
  def validationSuccessResponse(schemaId: String, jsonWithoutNulls: String): mvc.Result =
    Ok(
      prettyPrintJsonResponse(
        Response(
          "validateDocument",
          Some(schemaId),
          "success",
          validJson = Some(jsonWithoutNulls)
        )
      )
    )

  def validationFailedErrorResponse(schemaId: String, errorMessages: ListBuffer[String]): mvc.Result = BadRequest(
    prettyPrintJsonResponse(
      Response(
        "validateDocument",
        Some(schemaId),
        "error",
        message = Some("Error Messages: " + errorMessages.mkString(", ")))
    )
  )

  def noSchemaMatchingSchemaIdResponse(schemaId: String): mvc.Result = BadRequest(
    prettyPrintJsonResponse(
      Response(
        "getSchema",
        Some(schemaId),
        "error",
        message = Some(s"No Valid JSON schema found with schemaId: $schemaId.")
      )
    )
  )

  def uploadSchemaSuccessResponse(schemaId: String): mvc.Result = Created(
    prettyPrintJsonResponse(
      Response(
        "uploadSchema",
        Some(schemaId),
        "success"
      )
    )
  )

  def uploadSchemaFailureResponse(schemaId: String): mvc.Result = BadRequest(
    prettyPrintJsonResponse(
      Response(
        "uploadSchema",
        Some(schemaId),
        status = "error",
        message = Some("Json schema upload Failed. Does the id you are using for the schema already exist?"))
    )
  )

  def getJsonSchemaSuccessResponse(schemaId: String, jsonSchema: String): mvc.Result =
    Ok(
      prettyPrintJsonResponse(
        Response(
          "getSchema",
          Some(schemaId),
          status = "success",
          message = Some(jsonSchema)
        )
      )
    )

  def noSchemaParsedInRequestResponse(): mvc.Result = BadRequest(
    prettyPrintJsonResponse(
      Response(
        action = "validateDocument",
        status = "error",
        message = Some(s"Empty Json to validate against.")
      )
    )
  )
}