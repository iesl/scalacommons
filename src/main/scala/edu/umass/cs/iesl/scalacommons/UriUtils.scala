package edu.umass.cs.iesl.scalacommons

trait UriUtils {
  // import util._

  def mkQuery(ps: (String, Any)*): String =
    (ps map {case (a,b) => a+"="+b}).mkString("&")
  
  def joinQueries(q0:String, qs:String*): String = (q0 :: qs.toList).mkString("&")

  def joinQueries(qs:Seq[String]): String = qs.mkString("&")

  val addParam: (String, Any) => String => String =
    (a, b) => s  => joinQueries(s, mkQuery(a -> b))


  /**
   * Creates a URI using a path and optional query string
   */
  def uri(path: String, query: String = "") = {
    if (query != null && query.length > 0) {
      val separator = if (path.contains("?")) "&" else "?"
      path + separator + query
    }
    else {
      path
    }
  }

  /**
   * Combines the URI path, query string with additional query terms which will avoid duplicates
   */
  def uriPlus(path: String, query: String, addQuery: String)= {
    val newQuery = (splitQuery(query) ++ splitQuery(addQuery)).distinct
    uri(path, joinQueries(newQuery))

  }

  /**
   * Removes the given query terms from the query string if they are there
   */
  def uriMinus(path: String, query: String, removeQuery: String)= {
    val remove = splitQuery(removeQuery)
    val newQuery = splitQuery(query).filter(!remove.contains(_))
    uri(path, joinQueries(newQuery))
  }

  /**
   * Split a query expression into separate clauses
   */
  def splitQuery(query:String): Seq[String] = if (query != null && query.length > 0) query.split("&").toSeq else Nil

}

object UriUtils extends UriUtils
