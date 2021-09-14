package controllers.utils

import slick.lifted.{Rep, Tag}
import slick.jdbc.H2Profile.api._

case class JsonSchema(schema_Id: String, schema: String)

class JsonSchemaTable(tag: Tag) extends Table[JsonSchema](tag, None, "schemas") {
  val id: Rep[String] = column[String]("schema_id", O.Unique)
  val schema: Rep[String] = column[String]("schema")
  override def * = (id, schema) <> (JsonSchema.tupled, JsonSchema.unapply)
}
