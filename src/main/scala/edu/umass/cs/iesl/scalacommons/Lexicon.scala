package edu.umass.cs.iesl.scalacommons

import java.io.InputStream

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id$
 */

// usage:
// val stopwords = new Lexicon("stopwords")
// or
// val stopwords = new Lexicon(getClass.getResourceAsStream("/lexicons/stopwords"))

class Lexicon(s: InputStream) {

  def this(name: String) = this(getClass.getResourceAsStream("/lexicons/" + name))

  val (lexTokens, lexTokensLC) = {
    val text: String = IOUtils.loadText(s)
    val lexTokensLC: Map[String, Boolean] = text.toLowerCase.split("\n").map(x => (x -> true)).toMap
    val lexTokens: Map[String, Boolean] = text.split("\n").map(x => (x -> true)).toMap
    (lexTokens, lexTokensLC)
  }

  def countMatches(tokens: Seq[String]): Int = {
    tokens.map(lexTokens.get(_)).flatten.length
  }

  def countMatchesLC(tokens: Seq[String]): Int = {
    tokens.map((x: String) => lexTokensLC.get(x.toLowerCase)).flatten.length
  }

  def matches(tokens: Seq[String]): Seq[String] = {
    tokens.filter(t => {
      val r: Option[Boolean] = lexTokens.get(t)
      r.getOrElse(false)
    })
  }

  def matchesLC(tokens: Seq[String]): Seq[String] = {
    tokens.filter(t => {
      val r: Option[Boolean] = lexTokensLC.get(t.toLowerCase)
      r.getOrElse(false)
    })
  }
}
