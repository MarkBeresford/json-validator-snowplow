package controllers.utils

case class Schema(loadingURI: String, pointer: String)
case class Instance(pointer: String)
case class Result(
                   level: String,
                   schema: Schema,
                   instance: Instance,
                   domain:  String,
                   keyword: String,
                   message:  String,
                   found: String,
                   expected: Seq[String]
                 )