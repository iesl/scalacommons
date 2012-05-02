package edu.umass.cs.iesl.scalacommons
package util

object StringOps  {
  implicit def str2Ops(s:String): StringOps = {
    new StringOps(s)
  }

  def lines(s:String): Seq[String] = {
    val lines = s.split('\n')
    val lls = for (l <- lines.iterator) yield l
    lls.toSeq
  }

  // comma, whitespace separated values
  def csv(s:String): Array[String] = "\\s*,\\s*".r.split(s.trim())
  def wsv(s:String): Array[String] = "\\s+".r.split(s.trim())
  def strsv(s:String, sep:String): List[String] = s.split(sep).toList map (_.trim)


  val toPair = (ss:Array[String]) => (ss(0), ss(1))

  type OptionValues = List[String]
  type OptionMap = Map[String, OptionValues]

  def argsToMap(args: Array[String]): OptionMap = {
    import scala.collection.mutable.{ ListMap => LMap }
    val argmap = LMap[String, List[String]]()
    args.foldLeft(argmap)({ (m, k) =>
      {
        val ss: Seq[Char] = k
        ss match {
          case Seq('-', '-', opt@_*) => m.put(opt.toString, List[String]())
          case Seq('-', opt@_*) => m.put(opt.toString, List[String]())
          case opt@_ => m.put(m.head._1, m.head._2 ++ List(opt.toString))
        }
        m
      }
    })
    Map[String, List[String]](argmap.toList.reverse: _*)
  }


  /* Remove left/right border from a single or multi-line string, leaving left/right
   * whitespace intact, e.g.,
   * 
   * stripBorder("""
   *   | x |
   *   | y |
   * """)
   * == " x " + "\n" + " y "
   */
  def stripBorder(str:String): String = {
    val lines = str.trim.lines.map(_.trim)
    
    val stripped = lines.map { line =>
      if (line.startsWith("|") && line.endsWith("|")) 
        line.substring(1, line.length-1)
      else sys.error("badly formatted border on line: " + line)
    }
    stripped.mkString("\n")
  }


}

class StringOps(s:String) {
  import StringOps._
  import StringOps.{csv => xcsv}
  import StringOps.{wsv => xwsv}
  import StringOps.{strsv => _strsv}

  def wsva: Array[String] = xwsv(s)
  def wsvl: List[String] = wsva.toList
  def csva: Array[String] = xcsv(s)
  def csvl: List[String] = csva.toList
  def xsvl: List[String] = csva.toList
  def strsv(sep:String): List[String] = _strsv(s, sep)
}

