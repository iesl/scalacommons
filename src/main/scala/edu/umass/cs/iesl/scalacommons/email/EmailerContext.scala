package edu.umass.cs.iesl.scalacommons.email

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 */
import edu.umass.cs.iesl.scalacommons.NonemptyString
import org.joda.time.DateTime

trait EmailerContext {
  def serverHostname : String  // should establish a separate UrlContext?
  def resolvePath(urlPath: String): String
  def sendEmail(subject: String, recipient: String, body: String, from:Option[String]=None, asHtml: Boolean=false)
}

// ** could be some abstract "PluginPort" mechanism for objects like this?
object Emailer extends EmailerContext {
  // default this to memory based storage
  var _emailer: EmailerContext = new EmailerContext {
    def resolvePath(urlPath: String) = throw new Error("No emailer set.")
    def sendEmail(subject: String, recipient: String, body: String, from: Option[String], asHtml: Boolean) { throw new Error("No emailer set.") }
    def serverHostname = throw new Error("No emailer set.")
  }

  def setEmailer(s: EmailerContext) {
    _emailer = s
  }

  def serverHostname : String = _emailer.serverHostname
  def resolvePath(urlPath: String): String = _emailer.resolvePath(urlPath)
  def sendEmail(subject: String, recipient: String, body: String, from:Option[String]=None, asHtml: Boolean=false) = _emailer.sendEmail(subject, recipient,body,from,asHtml)
}


// maybe this should not exist at all??  It's not the same thing as setting mock=true for a PlayEmailerContext!
object PrintlnEmailerContext extends EmailerContext {
  def resolvePath(urlPath: String):String = urlPath
  def serverHostname : String = "bogus.openreview.net"

  override def sendEmail(subject: String, recipient: String, body: String, from:Option[String]=None, asHtml: Boolean=false) {
    println("Email:")
    println("  to:" + recipient)
    println("  subject:" + subject)
    println("  body:" + body)
    println("=======================")
  }

}



trait EmailMessage {
  val date: DateTime
  val to: NonemptyString
  val from: NonemptyString
  val subject: NonemptyString
  val body: NonemptyString
  val result: EmailMessageResult
}

sealed trait EmailMessageResult
case object Success extends EmailMessageResult
case class SmtpFailure(failureMessage: String) extends EmailMessageResult
case class Bounced(failureMessage: String) extends EmailMessageResult
