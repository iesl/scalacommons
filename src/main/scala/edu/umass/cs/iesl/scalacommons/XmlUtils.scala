package edu.umass.cs.iesl.scalacommons

import _root_.javax.xml.parsers.SAXParser
import xml.factory.XMLLoader
import xml.Elem

class XmlUtils {}


object XMLIgnoreDTD extends XMLLoader[Elem] {
  override def parser: SAXParser = {
    val f = javax.xml.parsers.SAXParserFactory.newInstance()
    f.setNamespaceAware(false)
    f.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    f.newSAXParser()
  }
}
