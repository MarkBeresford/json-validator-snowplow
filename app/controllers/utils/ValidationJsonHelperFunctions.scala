package controllers.utils

import com.fasterxml.jackson.databind.JsonNode
import com.github.fge.jackson.JsonLoader.fromString
import com.github.fge.jsonschema.core.report.{ProcessingMessage, ProcessingReport}
import com.github.fge.jsonschema.main.JsonSchemaFactory
import controllers.utils.DatabaseUtilityFunctions.getDataFromRequestBody
import io.circe.parser.{decode => circleDecode, parse => circleParse}
import io.circe.{ParsingFailure, Json => circleJson}
import io.circe.generic.auto._
import org.slf4j.Logger
import play.api.mvc.{AnyContent, Request}

import java.util
import scala.collection.mutable.ListBuffer

object ValidationJsonHelperFunctions {

  def generateJsonProcessingReport(jsonToValidate: JsonNode,
                                   schemaToValidateAgainst: JsonSchema): ProcessingReport = {
    val factory: JsonSchemaFactory = JsonSchemaFactory.byDefault
    val schemaAsJsonSchema = factory.getJsonSchema(fromString(schemaToValidateAgainst.schema))
    schemaAsJsonSchema.validate(jsonToValidate, true)
  }

  def createErrorMessageFromProcessingReportError(messageAsJson: String)(implicit logger: Logger): String =
    circleDecode[Result](messageAsJson)
      .fold(
        _ => {
          logger.error("Error parsing Json response from validation report original message was: " + messageAsJson)
          "Error parsing: " + messageAsJson + " into expected report format."
        },
        (message: Result) => {
          "Error validating parsed JSON for field: " + message.instance.pointer + ", Error message: " + message.message
        }
      )

  def getErrorMessagesFromProcessingReport(jsonValidationReport: ProcessingReport)(implicit logger: Logger): ListBuffer[String] = {
    val errorMessages: ListBuffer[String] = new ListBuffer[String]()
    val processingMessageIterator: util.Iterator[ProcessingMessage] = jsonValidationReport.iterator()
    while (processingMessageIterator.hasNext) {
      val messageAsJson: String = processingMessageIterator.next().asJson().toString
      errorMessages += createErrorMessageFromProcessingReportError(messageAsJson)
    }
    errorMessages
  }

  def getJsonToValidateFromRequestBody(request: Request[AnyContent]): Option[String] = {
    val dataRequestBody: Option[String] = getDataFromRequestBody(request)
    dataRequestBody match {
      case Some(requestBody) => {
        Some(
          circleParse(requestBody)
            .getOrElse(circleJson.Null)
            .deepDropNullValues
            .noSpaces
        )
      }
      case None => None

    }
  }

}
