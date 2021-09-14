package controllers

import controllers.utils.DatabaseUtilityFunctions.createSchemaTable
import org.slf4j.{Logger, LoggerFactory}
import com.google.inject.AbstractModule
import play.api.inject.ApplicationLifecycle
import javax.inject.Inject

class ApplicationLifeCycle @Inject() (lifecycle: ApplicationLifecycle) {
  implicit val logger: Logger = LoggerFactory.getLogger("ApplicationLifeCycle")
  import utils.DataBaseImplementation.db
  logger.info("Creating database Schemas.")
  createSchemaTable(db, logger)
}

class Module extends AbstractModule {
  override def configure() = {
    bind(classOf[ApplicationLifeCycle]).asEagerSingleton()
  }
}