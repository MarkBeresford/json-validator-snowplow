package controllers.utils

import controllers.utils.DatabaseUtilityFunctions.prettyPrintJsonResponse
import play.api.mvc
import play.api.mvc.Results._

import scala.collection.mutable.ListBuffer

case class Response(
                     action: String,
                     id: String,
                     status: String,
                     message: Option[String] = None,
                     validJson: Option[String] = None
                   )

object Response{
  // TODO: Should these Reponses contian PUT, GET, POST etc????
  def validationSuccessResponse(schemaId: String, jsonWithoutNulls: String): mvc.Result =
    Ok(
      prettyPrintJsonResponse(
        Response(
          "validateDocument",
          schemaId,
          "success",
          validJson = Some(jsonWithoutNulls)
        )
      )
    )

  def validationFailedErrorResponse(schemaId: String, errorMessages: ListBuffer[String]): mvc.Result = BadRequest(
    prettyPrintJsonResponse(
      Response(
        "validateDocument",
        schemaId,
        "error",
        message = Some("Error Messages: " + errorMessages.mkString(", ")))
    )
  )

  def noSchemaMatchingSchemaIdResponse(schemaId: String): mvc.Result = BadRequest(
    prettyPrintJsonResponse(
      Response(
        "validateDocument",
        schemaId,
        "error",
        message = Some(s"No Valid JSON schema found for $schemaId.")
      )
    )
  )

  def uploadSchemaSuccessResponse(schemaId: String): mvc.Result = Created(
    prettyPrintJsonResponse(
      Response(
        "uploadSchema",
        schemaId,
        "success")
    )
  )

  def uploadSchemaFailureResponse(schemaId: String): mvc.Result = BadRequest(
    prettyPrintJsonResponse(
      Response(
        "uploadSchema",
        schemaId,
        status = "error",
        message = Some("Json schema upload Failed."))
    )
  )

  def getJsonSchemaSuccessResponse(schemaId: String, jsonSchema: String): mvc.Result =
    Ok(
      prettyPrintJsonResponse(
        Response(
          "uploadSchema",
          schemaId,
          status = "error",
          message = Some("Json schema upload Failed."),
          validJson = Some(jsonSchema)
        )
      )
    )
}