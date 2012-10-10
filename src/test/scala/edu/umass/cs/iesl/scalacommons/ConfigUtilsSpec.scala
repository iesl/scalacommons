package edu.umass.cs.iesl.scalacommons

import org.specs2.mutable

object ConfigUtilsSpec extends mutable.Specification {
  import ConfigUtils._

  "config utils" should {
    "format uri" in {
      implicit val config = configFromMap(Map(
        "file.name" -> "some/file",
        "delay" -> "100ms"
      ))
      
      val uri = mkUri("""
        |file:{{file.name}}
        |  initialDelay={{delay}}
        |  delay=1000
        """)

      uri must_== "file:some/file?initialDelay=100ms&delay=1000"

    }

  }

}
