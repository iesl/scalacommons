package edu.umass.cs.iesl.scalacommons
package util


/* Originally from:
 * sbt -- Simple Build Tool
 * Copyright 2008, 2009 Mark Harrah
 */

/**The trap methods execute the provided code in a try block and handle a thrown exception.*/
object Control {
  def trap[T](errorMessagePrefix: => String)(execute: => Either[String, T]): Either[String, T] = {
    try {
      execute
    }
    catch {
      case e:Throwable => println(e); Left(errorMessagePrefix + e.toString)
    }
  }

  def trapAndFinally[T](errorMessagePrefix: => String)(execute: => Either[String, T])(doFinally: => Unit): Either[String, T] = {
    try {
      execute
    }
    catch {
      case e:Throwable => println(e); Left(errorMessagePrefix + e.toString)
    }
    finally {
      trapAndLog()(doFinally)
    }
  }

  def trapUnit(errorMessagePrefix: => String)(execute: => Option[String]): Option[String] = {
    try {
      execute
    }
    catch {
      case e:Throwable => println(e); Some(errorMessagePrefix + e.toString)
    }
  }

  def trapUnitAndFinally(errorMessagePrefix: => String)(execute: => Option[String])(doFinally: => Unit): Option[String] = {
    try {
      execute
    }
    catch {
      case e:Throwable => println(e); Some(errorMessagePrefix + e.toString)
    }
    finally {
      trapAndLog()(doFinally)
    }
  }

  def trap(execute: => Unit) {
    try {
      execute
    }
    catch {
      case e:Throwable => ()
    }
  }

  def trapAndLog()(execute: => Unit) {
    try {
      execute
    }
    catch {
      case e:Throwable => println(e); println(e.toString)
    }
  }

  def convertException[T](t: => T): Either[Exception, T] = {
    try {
      Right(t)
    }
    catch {
      case e:Exception => Left(e)
    }
  }

  def convertErrorMessage[T]()(t: => T): Either[String, T] = {
    try {
      Right(t)
    }
    catch {
      case e:Throwable => println(e); Left(e.toString)
    }
  }

  def getOrError[T](result: Either[String, T]): T = result.fold(sys.error, x => x)

  final def lazyFold[T](list: List[T])(f: T => Option[String]): Option[String] = list match {
    case Nil => None
    case head :: tail => f(head) match {
      case None => lazyFold(tail)(f)
      case x => x
    }
  }

  final def lazyFold[T, S](list: List[T], value: S)(f: (S, T) => Either[String, S]): Either[String, S] = list match {
    case Nil => Right(value)
    case head :: tail => f(value, head) match {
      case Right(newValue) => lazyFold(tail, newValue)(f)
      case x => x
    }
  }

  def thread[T](e: Either[String, T])(f: T => Option[String]): Option[String] =
    e.right.flatMap(t => f(t).toLeft(())).left.toOption
}
