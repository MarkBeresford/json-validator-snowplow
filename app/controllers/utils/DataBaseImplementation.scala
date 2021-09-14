package controllers.utils

import slick.jdbc.H2Profile
import slick.jdbc.H2Profile.api._

object DataBaseImplementation{
  implicit val db: H2Profile.backend.Database = Database.forConfig("postgres")
}
