package edu.umass.cs.iesl.scalacommons

import org.scalatest.{BeforeAndAfter, FunSuite}
import com.weiglewilczek.slf4s.Logging

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id$
 */
class RichStringTest
  extends FunSuite with BeforeAndAfter with Logging {

  import StringUtils._

  test("punctuation is trimmed") {
    assert(".Hello World,".trimPunctuation === "Hello World")
  }
}
